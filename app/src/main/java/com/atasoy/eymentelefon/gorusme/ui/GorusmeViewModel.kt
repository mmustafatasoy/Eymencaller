package com.atasoy.eymentelefon.gorusme.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.ayar.AyarDeposu
import com.atasoy.eymentelefon.gorusme.GorusmeAsamasi
import com.atasoy.eymentelefon.gorusme.GorusmeDurumu
import com.atasoy.eymentelefon.gorusme.GorusmeYoneticisi
import com.atasoy.eymentelefon.gorusme.SesDurumu
import com.atasoy.eymentelefon.gorusme.SesYolu
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GorusmeUiState(
    val gorusme: GorusmeDurumu? = null,
    val kisi: Kisi? = null,
    val ses: SesDurumu = SesDurumu(),
    /** Saniye */
    val sure: Long = 0,
    val tusTakimiAcik: Boolean = false,
    val dtmf: String = "",
    val hizliYanitlar: List<String> = emptyList(),
    /** Aynı anda başka arama varsa (beklemede / çalan) */
    val digerSayisi: Int = 0,
    val sesMenusuAcik: Boolean = false,
    val yanitMenusuAcik: Boolean = false,
)

sealed interface GorusmeOlay {
    data object Cevapla : GorusmeOlay
    data object Reddet : GorusmeOlay
    data class MesajlaReddet(val mesaj: String) : GorusmeOlay
    data object Kapat : GorusmeOlay
    data object Sessiz : GorusmeOlay
    data object Hoparlor : GorusmeOlay
    data class SesYoluSec(val yol: SesYolu) : GorusmeOlay
    data object Beklet : GorusmeOlay
    data object TusTakimi : GorusmeOlay
    data class Dtmf(val tus: Char) : GorusmeOlay
    data class SesMenusu(val acik: Boolean) : GorusmeOlay
    data class YanitMenusu(val acik: Boolean) : GorusmeOlay
}

private data class EkranDurumu(
    val tusTakimiAcik: Boolean = false,
    val dtmf: String = "",
    val sesMenusuAcik: Boolean = false,
    val yanitMenusuAcik: Boolean = false,
)

class GorusmeViewModel(
    private val rehber: RehberDeposu,
    private val ayarDeposu: AyarDeposu,
) : ViewModel() {

    private val ekran = MutableStateFlow(EkranDurumu())
    private val kisi = MutableStateFlow<Kisi?>(null)
    private val saniye = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(SANIYE_MS)
        }
    }

    val durum: StateFlow<GorusmeUiState> =
        combine(
            GorusmeYoneticisi.gorusmeler,
            GorusmeYoneticisi.ses,
            combine(ekran, kisi, ayarDeposu.ayarlar) { e, k, a -> Triple(e, k, a.hizliYanitlar ?: ayarDeposu.varsayilanHizliYanitlar()) },
            saniye,
        ) { liste, ses, (e, k, yanitlar), simdi ->
            val g = GorusmeYoneticisi.oncelikli(liste)
            GorusmeUiState(
                gorusme = g,
                kisi = k,
                ses = ses,
                sure = g?.baglanmaZamani?.let { ((simdi - it) / SANIYE_MS).coerceAtLeast(0) } ?: 0,
                tusTakimiAcik = e.tusTakimiAcik,
                dtmf = e.dtmf,
                hizliYanitlar = yanitlar,
                digerSayisi = (liste.size - 1).coerceAtLeast(0),
                sesMenusuAcik = e.sesMenusuAcik,
                yanitMenusuAcik = e.yanitMenusuAcik,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(BEKLEME_MS), GorusmeUiState(gorusme = GorusmeYoneticisi.oncelikli()))

    init {
        // Ekrandaki aramanın numarası değişince rehberden kişi bulunur
        viewModelScope.launch {
            GorusmeYoneticisi.gorusmeler
                .map { GorusmeYoneticisi.oncelikli(it)?.numara }
                .distinctUntilChanged()
                .collect { n -> kisi.value = n?.let { rehber.numaradanKisi(it) } }
        }
    }

    fun olay(o: GorusmeOlay) {
        val id = durum.value.gorusme?.id
        when (o) {
            GorusmeOlay.Cevapla -> id?.let(GorusmeYoneticisi::cevapla)
            GorusmeOlay.Reddet -> id?.let { GorusmeYoneticisi.reddet(it) }
            is GorusmeOlay.MesajlaReddet -> {
                ekran.value = ekran.value.copy(yanitMenusuAcik = false)
                id?.let { GorusmeYoneticisi.reddet(it, o.mesaj) }
            }
            GorusmeOlay.Kapat -> id?.let(GorusmeYoneticisi::kapat)
            GorusmeOlay.Sessiz -> GorusmeYoneticisi.sessiz(!durum.value.ses.sessiz)
            GorusmeOlay.Hoparlor -> GorusmeYoneticisi.sesYolu(
                if (durum.value.ses.yol == SesYolu.HOPARLOR) SesYolu.TELEFON else SesYolu.HOPARLOR,
            )
            is GorusmeOlay.SesYoluSec -> {
                ekran.value = ekran.value.copy(sesMenusuAcik = false)
                GorusmeYoneticisi.sesYolu(o.yol)
            }
            GorusmeOlay.Beklet -> durum.value.gorusme?.let {
                GorusmeYoneticisi.beklet(it.id, it.asama != GorusmeAsamasi.BEKLEMEDE)
            }
            GorusmeOlay.TusTakimi -> ekran.value = ekran.value.copy(tusTakimiAcik = !ekran.value.tusTakimiAcik)
            is GorusmeOlay.Dtmf -> {
                id?.let { GorusmeYoneticisi.dtmf(it, o.tus) }
                ekran.value = ekran.value.copy(dtmf = (ekran.value.dtmf + o.tus).takeLast(AZAMI_DTMF))
            }
            is GorusmeOlay.SesMenusu -> ekran.value = ekran.value.copy(sesMenusuAcik = o.acik)
            is GorusmeOlay.YanitMenusu -> ekran.value = ekran.value.copy(yanitMenusuAcik = o.acik)
        }
    }

    companion object {
        private const val SANIYE_MS = 1_000L
        private const val BEKLEME_MS = 2_000L
        private const val AZAMI_DTMF = 24

        val Fabrika = viewModelFactory {
            initializer { konteyner().let { GorusmeViewModel(it.rehberDeposu, it.ayarDeposu) } }
        }
    }
}
