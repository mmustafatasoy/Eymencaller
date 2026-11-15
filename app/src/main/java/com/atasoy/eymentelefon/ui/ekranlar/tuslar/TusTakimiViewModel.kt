package com.atasoy.eymentelefon.ui.ekranlar.tuslar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.arama.AramaBaslatici
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.T9Eslestirici
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.gecmis.AramaGecmisiDeposu
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TusTakimiUiState(
    val numara: String = "",
    val eslesme: Kisi? = null,
)

sealed interface TusTakimiOlay {
    data class TusaBasildi(val tus: Char) : TusTakimiOlay
    data class NumaraDoldur(val numara: String) : TusTakimiOlay
    data object Sil : TusTakimiOlay
    data object TumunuSil : TusTakimiOlay
    data object Ara : TusTakimiOlay
}

/** UI'dan tek yönlü olay alır; çağrı yalnızca AramaBaslatici üzerinden başlar. */
class TusTakimiViewModel(
    private val rehber: RehberDeposu,
    private val gecmis: AramaGecmisiDeposu,
    private val aramaBaslatici: AramaBaslatici,
) : ViewModel() {
    private val _durum = MutableStateFlow(TusTakimiUiState())
    val durum: StateFlow<TusTakimiUiState> = _durum.asStateFlow()
    private var kisiler: List<Kisi> = emptyList()

    init {
        viewModelScope.launch {
            rehber.kisiler().collect { yeniKisiler ->
                kisiler = yeniKisiler
                guncelle(_durum.value.numara)
            }
        }
    }

    fun olay(olay: TusTakimiOlay) {
        when (olay) {
            is TusTakimiOlay.TusaBasildi -> {
                if (olay.tus in "0123456789*#+") guncelle(_durum.value.numara + olay.tus)
            }
            is TusTakimiOlay.NumaraDoldur -> guncelle(olay.numara)
            TusTakimiOlay.Sil -> guncelle(_durum.value.numara.dropLast(1))
            TusTakimiOlay.TumunuSil -> guncelle("")
            TusTakimiOlay.Ara -> {
                val numara = _durum.value.numara
                if (numara.isNotBlank()) {
                    aramaBaslatici.ara(numara)
                } else {
                    viewModelScope.launch {
                        val son = gecmis.sonArananNumara()
                        if (!son.isNullOrBlank()) guncelle(son)
                    }
                }
            }
        }
    }

    private fun guncelle(numara: String) {
        _durum.value = TusTakimiUiState(numara = numara, eslesme = eslesmeBul(numara))
    }

    private fun eslesmeBul(numara: String): Kisi? {
        val aranan = NumaraBicimleyici.normalize(numara)
        if (aranan.length < 3 || !aranan.all(Char::isDigit)) return null
        return kisiler.asSequence().map { kisi ->
            val numaraPuani = if (kisi.numaralar.any {
                NumaraBicimleyici.normalize(it.numara).contains(aranan)
            }) 3 else 0
            kisi to maxOf(numaraPuani, T9Eslestirici.puan(kisi.ad, aranan))
        }.filter { it.second > 0 }
            .maxByOrNull { it.second }?.first
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer {
                val k = konteyner()
                TusTakimiViewModel(k.rehberDeposu, k.aramaGecmisiDeposu, k.aramaBaslatici)
            }
        }
    }
}
