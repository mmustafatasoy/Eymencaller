package com.atasoy.eymentelefon.ui.ekranlar.tuslar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.arama.AramaBaslatici
import com.atasoy.eymentelefon.cekirdek.ayar.AyarDeposu
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.T9Eslestirici
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.gecmis.AramaGecmisiDeposu
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TusTakimiUiState(
    val numara: String = "",
    val eslesme: Kisi? = null,
    val titresim: Boolean = true,
    val tusSesi: Boolean = false,
)

sealed interface TusTakimiOlay {
    data class Tus(val tus: Char) : TusTakimiOlay
    data class UzunBas(val tus: Char) : TusTakimiOlay
    data object Sil : TusTakimiOlay
    data object HepsiniSil : TusTakimiOlay
    data object Ara : TusTakimiOlay
    data object EslesmeSec : TusTakimiOlay
}

class TusTakimiViewModel(
    rehber: RehberDeposu,
    private val gecmis: AramaGecmisiDeposu,
    private val aramaBaslatici: AramaBaslatici,
    ayarlar: AyarDeposu,
    baslangicNumara: String,
) : ViewModel() {

    private val numara = MutableStateFlow(baslangicNumara)

    val durum: StateFlow<TusTakimiUiState> =
        combine(numara, rehber.kisiler(), ayarlar.ayarlar) { n, kisiler, a ->
            TusTakimiUiState(n, eslesmeBul(n, kisiler), a.tusTitresimi, a.tusSesi)
        }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(BEKLEME_MS), TusTakimiUiState(baslangicNumara))

    fun olay(o: TusTakimiOlay) {
        when (o) {
            is TusTakimiOlay.Tus -> ekle(o.tus)
            is TusTakimiOlay.UzunBas -> when (o.tus) {
                '0' -> ekle('+')
                '1' -> if (numara.value.isEmpty()) aramaBaslatici.sesliMesaj() else ekle('1')
                else -> ekle(o.tus)
            }
            TusTakimiOlay.Sil -> numara.value = numara.value.dropLast(1)
            TusTakimiOlay.HepsiniSil -> numara.value = ""
            TusTakimiOlay.Ara -> {
                val n = numara.value
                if (n.isBlank()) {
                    viewModelScope.launch { gecmis.sonArananNumara()?.let { numara.value = it } }
                } else {
                    aramaBaslatici.ara(n)
                }
            }
            TusTakimiOlay.EslesmeSec -> durum.value.eslesme?.varsayilanNumara?.let { numara.value = it.numara }
        }
    }

    private fun ekle(c: Char) {
        if (numara.value.length < AZAMI_UZUNLUK) numara.value += c
    }

    private fun eslesmeBul(girdi: String, kisiler: List<Kisi>): Kisi? {
        val rakam = girdi.filter { it.isDigit() }
        if (rakam.length < ASGARI_HANE) return null
        val norm = NumaraBicimleyici.normalize(rakam).ifEmpty { rakam }
        kisiler.firstOrNull { k ->
            k.numaralar.any { num ->
                val kayitli = num.numara.filter { it.isDigit() }
                kayitli.contains(rakam) || NumaraBicimleyici.normalize(kayitli).contains(norm)
            }
        }?.let { return it }
        var enIyi: Kisi? = null
        var enIyiPuan = 0
        for (k in kisiler) {
            val p = T9Eslestirici.puan(k.ad, rakam)
            if (p > enIyiPuan) {
                enIyi = k
                enIyiPuan = p
                if (p == 2) break
            }
        }
        return enIyi
    }

    companion object {
        private const val BEKLEME_MS = 5_000L
        private const val AZAMI_UZUNLUK = 40
        private const val ASGARI_HANE = 3

        fun fabrika(baslangicNumara: String) = viewModelFactory {
            initializer {
                val k = konteyner()
                TusTakimiViewModel(k.rehberDeposu, k.aramaGecmisiDeposu, k.aramaBaslatici, k.ayarDeposu, baslangicNumara)
            }
        }
    }
}
