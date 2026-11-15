package com.atasoy.eymentelefon.ui.ekranlar.arama

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.gecmis.AramaGecmisiDeposu
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/** GOREV-09: Görünür metin anlık, filtrelenmiş sonuçlar 150 ms gecikmeyle güncellenir. */
data class AramaUiState(
    val sorgu: String = "",
    val sonucSorgusu: String = "",
    val kisiler: List<Kisi> = emptyList(),
    val sonAramalar: List<AramaKaydi> = emptyList(),
    val yukleniyor: Boolean = true,
)

sealed interface AramaOlay {
    data class SorguDegisti(val sorgu: String) : AramaOlay
    data object Temizle : AramaOlay
}

@OptIn(FlowPreview::class)
class AramaViewModel(
    private val rehber: RehberDeposu,
    private val gecmis: AramaGecmisiDeposu,
) : ViewModel() {

    private val sorguAkisi = MutableStateFlow("")
    private val _durum = MutableStateFlow(AramaUiState())
    val durum: StateFlow<AramaUiState> = _durum.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                rehber.kisiler(),
                gecmis.kayitlar(),
                sorguAkisi.debounce(150L),
            ) { tumKisiler, tumAramalar, aranan ->
                val metin = aranan.trim()
                val rakamMi = metin.isNotEmpty() && metin.all(Char::isDigit)
                val rakamArama = metin.filter(Char::isDigit)

                val bulunanKisiler = if (metin.isEmpty()) emptyList() else tumKisiler.filter { kisi ->
                    TurkceMetin.iceriyorMu(kisi.ad, metin) ||
                        TurkceMetin.iceriyorMu(kisi.not, metin) ||
                        kisi.numaralar.any { numara ->
                            if (rakamMi) {
                                numara.numara.filter(Char::isDigit).contains(rakamArama)
                            } else {
                                TurkceMetin.iceriyorMu(numara.numara, metin)
                            }
                        }
                }
                val bulunanAramalar = if (metin.isEmpty()) {
                    tumAramalar.take(5)
                } else {
                    tumAramalar.filter { kayit ->
                        TurkceMetin.iceriyorMu(kayit.ad, metin) ||
                            if (rakamMi) kayit.numara.filter(Char::isDigit).contains(rakamArama)
                            else TurkceMetin.iceriyorMu(kayit.numara, metin)
                    }
                }
                AramaUiState(
                    sorgu = aranan,
                    sonucSorgusu = aranan,
                    kisiler = bulunanKisiler,
                    sonAramalar = bulunanAramalar,
                    yukleniyor = false,
                )
            }.flowOn(Dispatchers.Default).collect { filtrelenmis ->
                // Yeni karakter girildiyse eski sorgunun sonuçlarını göstermek yerine
                // son debounce tamamlanana kadar bekleriz.
                _durum.value = filtrelenmis.copy(sorgu = sorguAkisi.value)
            }
        }
    }

    fun olay(olay: AramaOlay) {
        when (olay) {
            is AramaOlay.SorguDegisti -> {
                sorguAkisi.value = olay.sorgu
                _durum.value = _durum.value.copy(sorgu = olay.sorgu)
            }
            AramaOlay.Temizle -> {
                sorguAkisi.value = ""
                _durum.value = _durum.value.copy(sorgu = "")
            }
        }
    }

    /** Klavye "ara" tuşu: önce rakamsal doğrudan arama, sonra ilk kişi, son arama. */
    fun ilkAranacakNumara(): String? {
        val s = _durum.value
        val metin = s.sorgu.trim()
        if (metin.isNotEmpty() && metin.all(Char::isDigit)) return metin
        if (s.sorgu != s.sonucSorgusu) return null
        return s.kisiler.firstNotNullOfOrNull { it.varsayilanNumara?.numara }
            ?: s.sonAramalar.firstOrNull { it.numara.isNotBlank() }?.numara
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer {
                val k = konteyner()
                AramaViewModel(k.rehberDeposu, k.aramaGecmisiDeposu)
            }
        }
    }
}
