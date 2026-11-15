package com.atasoy.eymentelefon.ui.ekranlar.kisidetay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.arama.AramaBaslatici
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.navigasyon.Rotalar
import com.atasoy.eymentelefon.veri.engel.EngelDeposu
import com.atasoy.eymentelefon.veri.gecmis.AramaGecmisiDeposu
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class KisiDetayUiState(
    /** Rehberde yoksa null (numara ekranı) */
    val kisi: Kisi? = null,
    /** Rehberde olmayan numara ya da kişinin varsayılan numarası */
    val numara: String = "",
    val aramalar: List<AramaKaydi> = emptyList(),
    val tumAramalar: Boolean = false,
    val engelli: Boolean = false,
    val goruntuluVar: Boolean = false,
    val yukleniyor: Boolean = true,
    val silindi: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
class KisiDetayViewModel(
    kayit: SavedStateHandle,
    private val rehber: RehberDeposu,
    private val gecmis: AramaGecmisiDeposu,
    private val engel: EngelDeposu,
    private val aramaBaslatici: AramaBaslatici,
) : ViewModel() {

    private val kisiId: Long = kayit[Rotalar.Arguman.KISI_ID] ?: -1L
    private val gelenNumara: String = kayit[Rotalar.Arguman.NUMARA] ?: ""

    private val _durum = MutableStateFlow(KisiDetayUiState(numara = gelenNumara))
    val durum: StateFlow<KisiDetayUiState> = _durum.asStateFlow()

    private val anaNumara = MutableStateFlow<String?>(null)

    init {
        yukle()
        viewModelScope.launch {
            anaNumara.filterNotNull().flatMapLatest { n -> gecmis.numaraKayitlari(n).map { n to it } }
                .collectLatest { (n, liste) ->
                    _durum.value = _durum.value.copy(aramalar = liste, engelli = engel.engelliMi(n))
                }
        }
    }

    fun yukle() {
        viewModelScope.launch {
            val kisi = when {
                kisiId >= 0 -> rehber.kisi(kisiId)
                gelenNumara.isNotBlank() -> rehber.numaradanKisi(gelenNumara)?.let { rehber.kisi(it.id) }
                else -> null
            }
            val numara = kisi?.varsayilanNumara?.numara ?: gelenNumara
            _durum.value = _durum.value.copy(
                kisi = kisi,
                numara = numara,
                goruntuluVar = aramaBaslatici.goruntuluDestekleniyor(),
                yukleniyor = false,
            )
            anaNumara.value = numara
        }
    }

    fun ara(numara: String = _durum.value.numara, goruntulu: Boolean = false) {
        aramaBaslatici.ara(numara, goruntulu = goruntulu)
    }

    fun favori() {
        val k = _durum.value.kisi ?: return
        viewModelScope.launch {
            rehber.favoriYap(k.id, !k.favori)
            _durum.value = _durum.value.copy(kisi = k.copy(favori = !k.favori))
        }
    }

    fun engelDegistir() {
        val d = _durum.value
        viewModelScope.launch {
            if (d.engelli) engel.engeliKaldir(d.numara) else engel.engelle(d.numara)
            _durum.value = _durum.value.copy(engelli = engel.engelliMi(d.numara))
        }
    }

    fun tumAramalar() {
        _durum.value = _durum.value.copy(tumAramalar = !_durum.value.tumAramalar)
    }

    fun sil() {
        val k = _durum.value.kisi ?: return
        viewModelScope.launch {
            rehber.sil(k.id)
            _durum.value = _durum.value.copy(silindi = true)
        }
    }

    fun kisiUri(k: Kisi) = rehber.kisiUri(k)
    fun vcardUri(k: Kisi) = rehber.vcardUri(k)

    companion object {
        val Fabrika = viewModelFactory {
            initializer {
                val k = konteyner()
                KisiDetayViewModel(createSavedStateHandle(), k.rehberDeposu, k.aramaGecmisiDeposu, k.engelDeposu, k.aramaBaslatici)
            }
        }
    }
}
