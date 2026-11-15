package com.atasoy.eymentelefon.ui.ekranlar.sonaramalar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.arama.AramaBaslatici
import com.atasoy.eymentelefon.cekirdek.yardimci.GunGrubu
import com.atasoy.eymentelefon.cekirdek.yardimci.ZamanBicimleyici
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.engel.EngelDeposu
import com.atasoy.eymentelefon.veri.gecmis.AramaGecmisiDeposu
import com.atasoy.eymentelefon.veri.model.AramaGrubu
import com.atasoy.eymentelefon.veri.model.AramaTuru
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AramaFiltresi { TUMU, CEVAPSIZ, GELEN, GIDEN }

data class AramaBolumu(val grup: GunGrubu, val satirlar: List<AramaGrubu>)

data class SonAramalarUiState(
    val filtre: AramaFiltresi = AramaFiltresi.TUMU,
    val bolumler: List<AramaBolumu> = emptyList(),
    val cevapsizSayisi: Int = 0,
    val yukleniyor: Boolean = true,
    /** Uzun basılan satır (alt menü açık) */
    val secili: AramaGrubu? = null,
)

class SonAramalarViewModel(
    private val gecmis: AramaGecmisiDeposu,
    private val engel: EngelDeposu,
    private val aramaBaslatici: AramaBaslatici,
) : ViewModel() {

    private val filtre = MutableStateFlow(AramaFiltresi.TUMU)
    private val secili = MutableStateFlow<AramaGrubu?>(null)

    val durum: StateFlow<SonAramalarUiState> =
        combine(gecmis.kayitlar(), filtre, secili) { kayitlar, f, s ->
            val suzulmus = kayitlar.filter {
                when (f) {
                    AramaFiltresi.TUMU -> true
                    AramaFiltresi.CEVAPSIZ -> it.tur == AramaTuru.CEVAPSIZ
                    AramaFiltresi.GELEN -> it.tur == AramaTuru.GELEN
                    AramaFiltresi.GIDEN -> it.tur == AramaTuru.GIDEN
                }
            }
            val bolumler = gecmis.grupla(suzulmus)
                .groupBy { ZamanBicimleyici.grup(it.ilk.tarih) }
                .toSortedMap()
                .map { (g, liste) -> AramaBolumu(g, liste) }
            SonAramalarUiState(
                filtre = f,
                bolumler = bolumler,
                cevapsizSayisi = kayitlar.count {
                    it.tur == AramaTuru.CEVAPSIZ && ZamanBicimleyici.grup(it.tarih) == GunGrubu.BUGUN
                },
                yukleniyor = false,
                secili = s,
            )
        }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(BEKLEME_MS), SonAramalarUiState())

    fun filtreSec(f: AramaFiltresi) {
        filtre.value = f
    }

    fun ara(numara: String) {
        aramaBaslatici.ara(numara)
    }

    fun menuAc(grup: AramaGrubu?) {
        secili.value = grup
    }

    fun sil(grup: AramaGrubu) {
        secili.value = null
        viewModelScope.launch { gecmis.sil(grup.kayitIdleri) }
    }

    fun engelle(numara: String) {
        secili.value = null
        viewModelScope.launch { engel.engelle(numara) }
    }

    /** "Temizle": ekrandaki (filtreye uyan) tüm kayıtları siler. */
    fun tumunuSil() {
        val idler = durum.value.bolumler.flatMap { b -> b.satirlar.flatMap { it.kayitIdleri } }
        viewModelScope.launch { gecmis.sil(idler) }
    }

    fun gorulduYap() {
        viewModelScope.launch { gecmis.cevapsizlariGorulduYap() }
    }

    companion object {
        private const val BEKLEME_MS = 5_000L

        val Fabrika = viewModelFactory {
            initializer { konteyner().let { SonAramalarViewModel(it.aramaGecmisiDeposu, it.engelDeposu, it.aramaBaslatici) } }
        }
    }
}
