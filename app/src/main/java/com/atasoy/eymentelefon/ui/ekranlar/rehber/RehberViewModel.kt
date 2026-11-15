package com.atasoy.eymentelefon.ui.ekranlar.rehber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.arama.AramaBaslatici
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HarfGrubu(val harf: String, val kisiler: List<Kisi>)

data class RehberUiState(
    val gruplar: List<HarfGrubu> = emptyList(),
    val favoriler: List<Kisi> = emptyList(),
    val benim: Kisi? = null,
    val sayi: Int = 0,
    val yukleniyor: Boolean = true,
)

class RehberViewModel(
    private val rehber: RehberDeposu,
    private val aramaBaslatici: AramaBaslatici,
) : ViewModel() {

    val durum: StateFlow<RehberUiState> =
        combine(rehber.kisiler(), flow { emit(rehber.benimKartim()) }) { kisiler, benim ->
            // Harf sırası TurkceMetin.HARFLER, "#" en sonda
            val sira = TurkceMetin.HARFLER + "#"
            val gruplar = kisiler.groupBy { it.grupHarfi }
                .toList()
                .sortedBy { (h, _) -> sira.indexOf(h).let { if (it < 0) sira.size else it } }
                .map { (h, liste) -> HarfGrubu(h, liste) }
            RehberUiState(
                gruplar = gruplar,
                favoriler = kisiler.filter { it.favori },
                benim = benim,
                sayi = kisiler.size,
                yukleniyor = false,
            )
        }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(BEKLEME_MS), RehberUiState())

    fun favoriDegistir(kisi: Kisi) {
        viewModelScope.launch { rehber.favoriYap(kisi.id, !kisi.favori) }
    }

    fun ara(kisi: Kisi) {
        kisi.varsayilanNumara?.let { aramaBaslatici.ara(it.numara) }
    }

    companion object {
        private const val BEKLEME_MS = 5_000L

        val Fabrika = viewModelFactory {
            initializer { konteyner().let { RehberViewModel(it.rehberDeposu, it.aramaBaslatici) } }
        }
    }
}
