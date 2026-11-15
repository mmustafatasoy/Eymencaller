package com.atasoy.eymentelefon.ui.ekranlar.sonaramalar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.veri.engel.EngelDeposu
import com.atasoy.eymentelefon.veri.gecmis.AramaGecmisiDeposu
import com.atasoy.eymentelefon.veri.model.AramaGrubu
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import com.atasoy.eymentelefon.veri.model.AramaTuru
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SonAramaFiltresi { TUMU, CEVAPSIZ, GELEN, GIDEN }

data class SonAramalarUiState(
    val gruplar: List<AramaGrubu> = emptyList(),
    val filtre: SonAramaFiltresi = SonAramaFiltresi.TUMU,
    val cevapsizSayisi: Int = 0,
    val profilAdi: String? = null,
    val acikMenu: AramaGrubu? = null,
)

sealed interface SonAramalarOlay {
    data class FiltreSec(val filtre: SonAramaFiltresi) : SonAramalarOlay
    data class MenuAc(val grup: AramaGrubu) : SonAramalarOlay
    data object MenuKapat : SonAramalarOlay
    data class Sil(val grup: AramaGrubu) : SonAramalarOlay
    data class Engelle(val numara: String) : SonAramalarOlay
}

/** Depo akışlarından tek yönlü UI durumu üretir. Context ve Android Intent tutmaz. */
class SonAramalarViewModel(
    private val gecmis: AramaGecmisiDeposu,
    private val engel: EngelDeposu,
    private val rehber: RehberDeposu,
) : ViewModel() {

    private val _durum = MutableStateFlow(SonAramalarUiState())
    val durum: StateFlow<SonAramalarUiState> = _durum.asStateFlow()
    private var tumKayitlar: List<AramaKaydi> = emptyList()
    private var numaraFiltresi: String = ""

    init {
        viewModelScope.launch {
            gecmis.kayitlar().collect { guncel ->
                tumKayitlar = guncel
                durumuYenile()
            }
        }
        viewModelScope.launch {
            val ad = rehber.benimKartim()?.ad
            _durum.value = _durum.value.copy(profilAdi = ad)
        }
    }

    /** Kişi detayından Son aramalara geçildiğinde o numarayı göster. */
    fun numaraFiltrele(numara: String) {
        val hedef = NumaraBicimleyici.normalize(numara)
        if (hedef == numaraFiltresi) return
        numaraFiltresi = hedef
        durumuYenile()
    }

    /** Ekran her görüntülendiğinde cevapsız kayıtların görüldü durumunu güncelle. */
    fun gorulduYap() {
        viewModelScope.launch { gecmis.cevapsizlariGorulduYap() }
    }

    fun olay(olay: SonAramalarOlay) {
        when (olay) {
            is SonAramalarOlay.FiltreSec -> {
                _durum.value = _durum.value.copy(filtre = olay.filtre)
                durumuYenile()
            }
            is SonAramalarOlay.MenuAc -> _durum.value = _durum.value.copy(acikMenu = olay.grup)
            SonAramalarOlay.MenuKapat -> _durum.value = _durum.value.copy(acikMenu = null)
            is SonAramalarOlay.Sil -> {
                _durum.value = _durum.value.copy(acikMenu = null)
                viewModelScope.launch { gecmis.sil(olay.grup.kayitIdleri) }
            }
            is SonAramalarOlay.Engelle -> {
                _durum.value = _durum.value.copy(acikMenu = null)
                if (olay.numara.isNotBlank()) viewModelScope.launch { engel.engelle(olay.numara) }
            }
        }
    }

    private fun durumuYenile() {
        val gorunurKayitlar = if (numaraFiltresi.isBlank()) tumKayitlar else tumKayitlar.filter {
            NumaraBicimleyici.normalize(it.numara) == numaraFiltresi
        }
        val filtrelenmis = when (_durum.value.filtre) {
            SonAramaFiltresi.TUMU -> gorunurKayitlar
            SonAramaFiltresi.CEVAPSIZ -> gorunurKayitlar.filter { it.tur == AramaTuru.CEVAPSIZ }
            SonAramaFiltresi.GELEN -> gorunurKayitlar.filter { it.tur == AramaTuru.GELEN }
            SonAramaFiltresi.GIDEN -> gorunurKayitlar.filter { it.tur == AramaTuru.GIDEN }
        }
        _durum.value = _durum.value.copy(
            gruplar = gecmis.grupla(filtrelenmis),
            cevapsizSayisi = gorunurKayitlar.count { it.tur == AramaTuru.CEVAPSIZ },
        )
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer {
                val k = konteyner()
                SonAramalarViewModel(k.aramaGecmisiDeposu, k.engelDeposu, k.rehberDeposu)
            }
        }
    }
}
