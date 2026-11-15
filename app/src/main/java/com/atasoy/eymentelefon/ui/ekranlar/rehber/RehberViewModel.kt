package com.atasoy.eymentelefon.ui.ekranlar.rehber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RehberUiState(
    val kisiler: List<Kisi> = emptyList(),
    val favoriler: List<Kisi> = emptyList(),
    val benimKartim: Kisi? = null,
    val yukleniyor: Boolean = true,
)

sealed interface RehberOlay {
    data class FavoridenCikar(val kisiId: Long) : RehberOlay
    data object ProfilYenile : RehberOlay
}

class RehberViewModel(private val depo: RehberDeposu) : ViewModel() {
    private val _durum = MutableStateFlow(RehberUiState())
    val durum: StateFlow<RehberUiState> = _durum.asStateFlow()

    init {
        viewModelScope.launch {
            depo.kisiler().collect { liste ->
                _durum.value = _durum.value.copy(
                    kisiler = liste.sortedWith { a, b ->
                        TurkceMetin.karsilastirici.compare(a.ad, b.ad)
                    },
                    yukleniyor = false,
                )
            }
        }
        viewModelScope.launch {
            depo.favoriler().collect { favoriler ->
                _durum.value = _durum.value.copy(favoriler = favoriler)
            }
        }
        olay(RehberOlay.ProfilYenile)
    }

    fun olay(olay: RehberOlay) {
        when (olay) {
            is RehberOlay.FavoridenCikar -> viewModelScope.launch {
                depo.favoriYap(olay.kisiId, false)
            }
            RehberOlay.ProfilYenile -> viewModelScope.launch {
                val profil = depo.benimKartim()
                _durum.value = _durum.value.copy(benimKartim = profil)
            }
        }
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer { RehberViewModel(konteyner().rehberDeposu) }
        }
    }
}
