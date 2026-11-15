package com.atasoy.eymentelefon.ui.ekranlar.arama

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.arama.AramaBaslatici
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.gecmis.AramaGecmisiDeposu
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

data class AramaSonucu(
    val sorgu: String = "",
    val kisiler: List<Kisi> = emptyList(),
    val aramalar: List<AramaKaydi> = emptyList(),
    /** Sorgu numaraya benziyorsa en üstte "Ara: …" satırı */
    val aranabilirNumara: String? = null,
)

@OptIn(FlowPreview::class)
class AramaViewModel(
    rehber: RehberDeposu,
    gecmis: AramaGecmisiDeposu,
    private val aramaBaslatici: AramaBaslatici,
) : ViewModel() {

    private val _sorgu = MutableStateFlow("")
    val sorgu: StateFlow<String> = _sorgu.asStateFlow()

    val sonuc: StateFlow<AramaSonucu> =
        combine(_sorgu.debounce(GECIKME_MS), rehber.kisiler(), gecmis.kayitlar()) { s, kisiler, kayitlar ->
            ara(s.trim(), kisiler, kayitlar)
        }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), AramaSonucu())

    fun sorguDegisti(s: String) {
        _sorgu.value = s
    }

    fun ara(numara: String) {
        aramaBaslatici.ara(numara)
    }

    /** Klavyedeki "ara" tuşu: ilk sonucu ara */
    fun ilkSonucuAra() {
        val s = sonuc.value
        val numara = s.aranabilirNumara
            ?: s.kisiler.firstOrNull()?.varsayilanNumara?.numara
            ?: s.aramalar.firstOrNull()?.numara
        numara?.let(aramaBaslatici::ara)
    }

    private fun ara(s: String, kisiler: List<Kisi>, kayitlar: List<AramaKaydi>): AramaSonucu {
        if (s.isEmpty()) {
            return AramaSonucu(aramalar = kayitlar.distinctBy { NumaraBicimleyici.normalize(it.numara) }.take(SON_SAYI))
        }
        val rakam = s.filter { it.isDigit() }
        val numaraGibi = s.all { it.isDigit() || it in "+ *#()-" } && rakam.isNotEmpty()
        val kisiSonuc = kisiler.filter { k ->
            TurkceMetin.iceriyorMu(k.ad, s) ||
                (rakam.length >= 2 && k.numaralar.any { it.numara.filter(Char::isDigit).contains(rakam) })
        }.take(AZAMI)
        val aramaSonuc = kayitlar.filter { a ->
            TurkceMetin.iceriyorMu(a.ad, s) || (rakam.length >= 2 && a.numara.filter(Char::isDigit).contains(rakam))
        }.distinctBy { NumaraBicimleyici.normalize(it.numara) }.take(SON_SAYI)
        return AramaSonucu(
            sorgu = s,
            kisiler = kisiSonuc,
            aramalar = aramaSonuc,
            aranabilirNumara = if (numaraGibi) s else null,
        )
    }

    companion object {
        private const val GECIKME_MS = 150L
        private const val SON_SAYI = 5
        private const val AZAMI = 50

        val Fabrika = viewModelFactory {
            initializer { konteyner().let { AramaViewModel(it.rehberDeposu, it.aramaGecmisiDeposu, it.aramaBaslatici) } }
        }
    }
}
