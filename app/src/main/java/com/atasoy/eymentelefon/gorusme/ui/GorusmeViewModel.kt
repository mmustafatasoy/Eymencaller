package com.atasoy.eymentelefon.gorusme.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.ayar.AyarDeposu
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.gorusme.GorusmeAsamasi
import com.atasoy.eymentelefon.gorusme.GorusmeDurumu
import com.atasoy.eymentelefon.gorusme.GorusmeYoneticisi
import com.atasoy.eymentelefon.gorusme.SesDurumu
import com.atasoy.eymentelefon.gorusme.SesYolu
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class GorusmeUiState(
    val gorusme: GorusmeDurumu? = null,
    val ad: String = "",
    val fotoUri: Uri? = null,
    val ses: SesDurumu = SesDurumu(),
    val sureSaniye: Long = 0L,
    val tuslarAcik: Boolean = false,
    val sesMenusuAcik: Boolean = false,
    val hizliYanitAcik: Boolean = false,
    val hizliYanitlar: List<String> = emptyList(),
)

sealed interface GorusmeOlay {
    data object Cevapla : GorusmeOlay
    data object Reddet : GorusmeOlay
    data class MesajlaReddet(val mesaj: String) : GorusmeOlay
    data object Kapat : GorusmeOlay
    data object SessizDegistir : GorusmeOlay
    data object HoparlorDegistir : GorusmeOlay
    data object BekletDegistir : GorusmeOlay
    data object TuslariDegistir : GorusmeOlay
    data class Dtmf(val tus: Char) : GorusmeOlay
    data class SesYoluSec(val yol: SesYolu) : GorusmeOlay
    data object SesMenusuDegistir : GorusmeOlay
    data object HizliYanitMenusuDegistir : GorusmeOlay
}

/** GOREV-11: Tek görüşme kaynağı GorusmeYoneticisi; ViewModel Context tutmaz. */
class GorusmeViewModel(
    private val rehber: RehberDeposu,
    private val ayarlar: AyarDeposu,
) : ViewModel() {
    private val _durum = MutableStateFlow(GorusmeUiState())
    val durum: StateFlow<GorusmeUiState> = _durum.asStateFlow()

    private var kisiIslemi: Job? = null
    private var aktifKimlik: String? = null

    init {
        viewModelScope.launch {
            GorusmeYoneticisi.gorusmeler.collectLatest { liste ->
                val g = GorusmeYoneticisi.oncelikli(liste)
                if (g?.id != aktifKimlik) {
                    kisiIslemi?.cancel()
                    aktifKimlik = g?.id
                    _durum.value = _durum.value.copy(
                        ad = g?.sistemAdi.orEmpty(),
                        fotoUri = null,
                        tuslarAcik = false,
                        sesMenusuAcik = false,
                        hizliYanitAcik = false,
                    )
                    if (g != null && g.numara.isNotBlank()) {
                        kisiIslemi = viewModelScope.launch(Dispatchers.IO) {
                            try {
                                val kisi = rehber.numaradanKisi(g.numara)
                                if (aktifKimlik == g.id) {
                                    _durum.value = _durum.value.copy(
                                        ad = kisi?.ad ?: g.sistemAdi.orEmpty(),
                                        fotoUri = kisi?.fotoUri,
                                    )
                                }
                            } catch (hata: RuntimeException) {
                                HataKaydedici.kaydet("GorusmeViewModel.kisi", hata)
                            }
                        }
                    }
                }
                _durum.value = _durum.value.copy(gorusme = g)
                sureyiHesapla()
            }
        }
        viewModelScope.launch {
            GorusmeYoneticisi.ses.collect { yeni ->
                _durum.value = _durum.value.copy(ses = yeni)
            }
        }
        viewModelScope.launch {
            ayarlar.ayarlar.collect { bilgi ->
                _durum.value = _durum.value.copy(
                    hizliYanitlar = bilgi.hizliYanitlar ?: ayarlar.varsayilanHizliYanitlar()
                )
            }
        }
        viewModelScope.launch {
            while (true) {
                sureyiHesapla()
                delay(1000L)
            }
        }
    }

    fun olay(olay: GorusmeOlay) {
        val d = _durum.value
        val g = d.gorusme ?: return
        when (olay) {
            GorusmeOlay.Cevapla -> GorusmeYoneticisi.cevapla(g.id)
            GorusmeOlay.Reddet -> GorusmeYoneticisi.reddet(g.id)
            is GorusmeOlay.MesajlaReddet -> {
                if (olay.mesaj.isNotBlank()) GorusmeYoneticisi.reddet(g.id, olay.mesaj)
                _durum.value = d.copy(hizliYanitAcik = false)
            }
            GorusmeOlay.Kapat -> GorusmeYoneticisi.kapat(g.id)
            GorusmeOlay.SessizDegistir -> GorusmeYoneticisi.sessiz(!d.ses.sessiz)
            GorusmeOlay.HoparlorDegistir -> {
                GorusmeYoneticisi.sesYolu(
                    if (d.ses.yol == SesYolu.HOPARLOR) SesYolu.TELEFON
                    else SesYolu.HOPARLOR
                )
            }
            GorusmeOlay.BekletDegistir -> {
                if (g.beklemeDesteklenir) {
                    GorusmeYoneticisi.beklet(g.id, g.asama != GorusmeAsamasi.BEKLEMEDE)
                }
            }
            GorusmeOlay.TuslariDegistir ->
                _durum.value = d.copy(tuslarAcik = !d.tuslarAcik)
            is GorusmeOlay.Dtmf -> GorusmeYoneticisi.dtmf(g.id, olay.tus)
            is GorusmeOlay.SesYoluSec -> {
                GorusmeYoneticisi.sesYolu(olay.yol)
                _durum.value = d.copy(sesMenusuAcik = false)
            }
            GorusmeOlay.SesMenusuDegistir ->
                _durum.value = d.copy(sesMenusuAcik = !d.sesMenusuAcik)
            GorusmeOlay.HizliYanitMenusuDegistir ->
                _durum.value = d.copy(hizliYanitAcik = !d.hizliYanitAcik)
        }
    }

    private fun sureyiHesapla() {
        val g = _durum.value.gorusme
        val saniye = if (g != null &&
            g.asama in listOf(GorusmeAsamasi.AKTIF, GorusmeAsamasi.BEKLEMEDE)
        ) {
            g.baglanmaZamani?.let {
                ((System.currentTimeMillis() - it) / 1000L).coerceAtLeast(0L)
            } ?: 0L
        } else 0L
        if (_durum.value.sureSaniye != saniye) {
            _durum.value = _durum.value.copy(sureSaniye = saniye)
        }
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer {
                val k = konteyner()
                GorusmeViewModel(k.rehberDeposu, k.ayarDeposu)
            }
        }
    }
}
