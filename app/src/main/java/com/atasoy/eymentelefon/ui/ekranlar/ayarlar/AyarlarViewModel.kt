package com.atasoy.eymentelefon.ui.ekranlar.ayarlar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.ayar.AyarDeposu
import com.atasoy.eymentelefon.cekirdek.ayar.Ayarlar
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.izin.IzinYoneticisi
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.engel.EngelDeposu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AyarlarUiState(
    val ayarlar: Ayarlar = Ayarlar(),
    val verilenIzin: Int = 0,
    val toplamIzin: Int = 0,
    val varsayilanUygulama: Boolean = false,
    val engelliSayisi: Int = 0,
    val hataSayisi: Int = 0,
    val hataMetni: String = "",
)

private data class SistemDurumu(val verilen: Int, val toplam: Int, val varsayilan: Boolean, val hata: Int, val hataMetni: String)

@OptIn(FlowPreview::class)
class AyarlarViewModel(
    private val ayarDeposu: AyarDeposu,
    private val izinler: IzinYoneticisi,
    engel: EngelDeposu,
) : ViewModel() {

    private val sistem = MutableStateFlow(SistemDurumu(0, 0, false, 0, ""))
    private val hizliTaslak = MutableStateFlow<List<String>?>(null)

    val durum: StateFlow<AyarlarUiState> =
        combine(ayarDeposu.ayarlar, sistem, engel.engelliler()) { a, s, e ->
            AyarlarUiState(a, s.verilen, s.toplam, s.varsayilan, e.size, s.hata, s.hataMetni)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), AyarlarUiState())

    init {
        viewModelScope.launch {
            hizliTaslak.filterNotNull().debounce(KAYIT_GECIKMESI_MS).collect { ayarDeposu.hizliYanitlariKaydet(it) }
        }
    }

    /** ON_RESUME: izin, rol ve hata kayıtları yeniden okunur. */
    fun yenile() {
        viewModelScope.launch {
            val (sayi, metin) = withContext(Dispatchers.IO) { HataKaydedici.kayitSayisi() to HataKaydedici.tumMetin() }
            sistem.value = SistemDurumu(izinler.verilenSayisi(), izinler.toplamSayi(), izinler.varsayilanUygulamaMi(), sayi, metin)
        }
    }

    fun temaSec(t: TemaSecimi) = viewModelScope.launch { ayarDeposu.temaSec(t) }
    fun vurguSec(i: Int) = viewModelScope.launch { ayarDeposu.vurguSec(i) }
    fun titresim(a: Boolean) = viewModelScope.launch { ayarDeposu.tusTitresimi(a) }
    fun tusSesi(a: Boolean) = viewModelScope.launch { ayarDeposu.tusSesi(a) }

    fun hizliYanitlar(): List<String> = durum.value.ayarlar.hizliYanitlar ?: ayarDeposu.varsayilanHizliYanitlar()

    fun hizliYanitDegisti(liste: List<String>) {
        hizliTaslak.value = liste
    }

    fun hatalariTemizle() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { HataKaydedici.temizle() }
            yenile()
        }
    }

    fun rolIntenti() = izinler.rolIstemeIntenti()

    companion object {
        private const val KAYIT_GECIKMESI_MS = 400L

        val Fabrika = viewModelFactory {
            initializer { konteyner().let { AyarlarViewModel(it.ayarDeposu, it.izinYoneticisi, it.engelDeposu) } }
        }
    }
}
