package com.atasoy.eymentelefon.ui.ekranlar.favoriler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.arama.AramaBaslatici
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavorilerUiState(val kisiler: List<Kisi> = emptyList(), val yukleniyor: Boolean = true)

class FavorilerViewModel(private val rehber: RehberDeposu, private val aramaBaslatici: AramaBaslatici) : ViewModel() {

    val durum: StateFlow<FavorilerUiState> = rehber.favoriler()
        .map { FavorilerUiState(it, false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), FavorilerUiState())

    fun ara(kisi: Kisi) {
        kisi.varsayilanNumara?.let { aramaBaslatici.ara(it.numara) }
    }

    fun favoridenCikar(kisi: Kisi) {
        viewModelScope.launch { rehber.favoriYap(kisi.id, false) }
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer { konteyner().let { FavorilerViewModel(it.rehberDeposu, it.aramaBaslatici) } }
        }
    }
}
