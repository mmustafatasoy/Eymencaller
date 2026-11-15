package com.atasoy.eymentelefon.ui.ekranlar.favoriler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavorilerUiState(val kisiler: List<Kisi> = emptyList(), val yukleniyor: Boolean = true)

class FavorilerViewModel(private val depo: RehberDeposu) : ViewModel() {
    private val _durum = MutableStateFlow(FavorilerUiState())
    val durum: StateFlow<FavorilerUiState> = _durum.asStateFlow()

    init {
        viewModelScope.launch {
            depo.favoriler().collect { kisiler ->
                _durum.value = FavorilerUiState(kisiler = kisiler, yukleniyor = false)
            }
        }
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer { FavorilerViewModel(konteyner().rehberDeposu) }
        }
    }
}
