package com.atasoy.eymentelefon.ui.ekranlar.izinler

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.cekirdek.ayar.AyarDeposu
import com.atasoy.eymentelefon.cekirdek.izin.IzinDurumu
import com.atasoy.eymentelefon.cekirdek.izin.IzinListesi
import com.atasoy.eymentelefon.cekirdek.izin.IzinTanimi
import com.atasoy.eymentelefon.cekirdek.izin.IzinTuru
import com.atasoy.eymentelefon.cekirdek.izin.IzinYoneticisi
import com.atasoy.eymentelefon.konteyner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class IzinKartiBilgisi(val tanim: IzinTanimi, val durum: IzinDurumu)

data class IzinlerUiState(
    val kartlar: List<IzinKartiBilgisi> = emptyList(),
    val verilen: Int = 0,
    val toplam: Int = 0,
    val zorunlularTamam: Boolean = false,
)

/** İzin kararları yalnızca IzinYoneticisi üzerinden okunur, UI yalnızca state gösterir. */
class IzinlerViewModel(
    private val izinYoneticisi: IzinYoneticisi,
    private val ayarDeposu: AyarDeposu,
) : ViewModel() {
    private val _durum = MutableStateFlow(IzinlerUiState())
    val durum: StateFlow<IzinlerUiState> = _durum.asStateFlow()
    private var istenenIzinler: Set<String> = emptySet()

    init {
        viewModelScope.launch {
            ayarDeposu.ayarlar.collectLatest { ayarlar ->
                istenenIzinler = ayarlar.istenenIzinler
            }
        }
    }

    fun yenile(activity: Activity) {
        val kartlar = IzinListesi.gecerli().map { tanim ->
            IzinKartiBilgisi(tanim, izinYoneticisi.durum(tanim, activity, istenenIzinler))
        }
        _durum.value = IzinlerUiState(
            kartlar = kartlar,
            verilen = kartlar.count { it.durum == IzinDurumu.VERILDI },
            toplam = kartlar.size,
            zorunlularTamam = kartlar.filter { it.tanim.zorunlu }.all { it.durum == IzinDurumu.VERILDI },
        )
    }

    /** Android izinleri istemeden önce kalıcı ret kontrolü için kaydedilir. */
    fun istegiKaydet(tur: IzinTuru, baslat: () -> Unit) {
        val izinler = IzinListesi.bul(tur).androidIzinleri
        if (izinler.isEmpty()) {
            baslat()
            return
        }
        viewModelScope.launch {
            ayarDeposu.izinlerIstendi(izinler)
            istenenIzinler = istenenIzinler + izinler
            baslat()
        }
    }

    fun tamamla(onTamam: () -> Unit) {
        if (!izinYoneticisi.zorunlularTamam()) return
        viewModelScope.launch {
            ayarDeposu.ilkAcilisiTamamla()
            onTamam()
        }
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer {
                val k = konteyner()
                IzinlerViewModel(k.izinYoneticisi, k.ayarDeposu)
            }
        }
    }
}
