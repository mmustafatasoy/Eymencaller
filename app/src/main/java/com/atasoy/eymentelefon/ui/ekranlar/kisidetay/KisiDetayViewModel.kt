package com.atasoy.eymentelefon.ui.ekranlar.kisidetay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.veri.engel.EngelDeposu
import com.atasoy.eymentelefon.veri.gecmis.AramaGecmisiDeposu
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KisiDetayUiState(
    val kisi: Kisi? = null,
    val numara: String = "",
    val aramalar: List<AramaKaydi> = emptyList(),
    val engelli: Boolean = false,
    val yukleniyor: Boolean = true,
    val silindi: Boolean = false,
)

sealed interface KisiDetayOlay {
    data object FavoriDegistir : KisiDetayOlay
    data object EngeliDegistir : KisiDetayOlay
    data object KisiSil : KisiDetayOlay
    data object Yenile : KisiDetayOlay
}

/**
 * Rehber/CallLog/engelleme verileri tek kaynaktan gelir.
 * Görünür UI çağrı, mesaj ve paylaşım niyetlerini ekran katmanına aktarır.
 */
class KisiDetayViewModel(
    private val rehber: RehberDeposu,
    private val gecmis: AramaGecmisiDeposu,
    private val engel: EngelDeposu,
) : ViewModel() {
    private val _durum = MutableStateFlow(KisiDetayUiState())
    val durum: StateFlow<KisiDetayUiState> = _durum.asStateFlow()
    private var kisiId = -1L
    private var ilkNumara = ""
    private var gecmisIsi: Job? = null

    fun yukle(id: Long, numara: String) {
        if (id == kisiId && numara == ilkNumara && !_durum.value.yukleniyor) return
        kisiId = id
        ilkNumara = numara
        oku()
    }

    fun olay(olay: KisiDetayOlay) {
        when (olay) {
            KisiDetayOlay.Yenile -> oku()
            KisiDetayOlay.FavoriDegistir -> {
                val kisi = _durum.value.kisi ?: return
                viewModelScope.launch {
                    rehber.favoriYap(kisi.id, !kisi.favori)
                    _durum.value = _durum.value.copy(
                        kisi = rehber.kisi(kisi.id) ?: kisi,
                    )
                }
            }
            KisiDetayOlay.EngeliDegistir -> {
                val numara = _durum.value.numara
                if (numara.isBlank()) return
                viewModelScope.launch {
                    if (_durum.value.engelli) engel.engeliKaldir(numara) else engel.engelle(numara)
                    _durum.value = _durum.value.copy(engelli = engel.engelliMi(numara))
                }
            }
            KisiDetayOlay.KisiSil -> {
                val kisi = _durum.value.kisi ?: return
                viewModelScope.launch {
                    rehber.sil(kisi.id)
                    val silindi = rehber.kisi(kisi.id) == null
                    _durum.value = _durum.value.copy(silindi = silindi)
                }
            }
        }
    }

    private fun oku() {
        viewModelScope.launch {
            _durum.value = _durum.value.copy(yukleniyor = true)
            val kisi = if (kisiId >= 0L) rehber.kisi(kisiId)
                else ilkNumara.takeIf(String::isNotBlank)?.let { rehber.numaradanKisi(it) }
            val numara = ilkNumara.ifBlank { kisi?.varsayilanNumara?.numara.orEmpty() }
            val engelli = numara.isNotBlank() && engel.engelliMi(numara)
            _durum.value = _durum.value.copy(
                kisi = kisi, numara = numara, engelli = engelli,
                aramalar = emptyList(), yukleniyor = false,
            )
            gecmisIsi?.cancel()
            val numaralar = (kisi?.numaralar?.map { it.numara }.orEmpty() + numara)
                .map(NumaraBicimleyici::normalize).filter(String::isNotBlank).toSet()
            if (numaralar.isNotEmpty()) {
                gecmisIsi = viewModelScope.launch {
                    gecmis.kayitlar().collect { kayitlar ->
                        _durum.value = _durum.value.copy(
                            aramalar = kayitlar.filter {
                                NumaraBicimleyici.normalize(it.numara) in numaralar
                            }.take(3)
                        )
                    }
                }
            }
        }
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer {
                val k = konteyner()
                KisiDetayViewModel(k.rehberDeposu, k.aramaGecmisiDeposu, k.engelDeposu)
            }
        }
    }
}
