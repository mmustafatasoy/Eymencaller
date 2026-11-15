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
import com.atasoy.eymentelefon.cekirdek.izin.IzinYoneticisi
import com.atasoy.eymentelefon.konteyner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class IzinKartDurumu(val tanim: IzinTanimi, val durum: IzinDurumu)

data class IzinlerUiState(
    val kartlar: List<IzinKartDurumu> = emptyList(),
    val verilen: Int = 0,
    val toplam: Int = 0,
    val devamAktif: Boolean = false,
    val nedenAcik: Boolean = false,
)

class IzinlerViewModel(
    private val izinler: IzinYoneticisi,
    private val ayarlar: AyarDeposu,
) : ViewModel() {

    private val _durum = MutableStateFlow(IzinlerUiState())
    val durum: StateFlow<IzinlerUiState> = _durum.asStateFlow()

    /** Ekran açılınca, ON_RESUME'da ve her izin sonucundan sonra çağrılır. Activity saklanmaz. */
    fun yenile(activity: Activity) {
        viewModelScope.launch {
            val istenenler = ayarlar.ayarlar.first().istenenIzinler
            val kartlar = IzinListesi.gecerli().map { IzinKartDurumu(it, izinler.durum(it, activity, istenenler)) }
            _durum.value = _durum.value.copy(
                kartlar = kartlar,
                verilen = kartlar.count { it.durum == IzinDurumu.VERILDI },
                toplam = kartlar.size,
                devamAktif = kartlar.filter { it.tanim.zorunlu }.all { it.durum == IzinDurumu.VERILDI },
            )
        }
    }

    fun istendi(androidIzinleri: Array<String>) {
        viewModelScope.launch { ayarlar.izinlerIstendi(androidIzinleri.toList()) }
    }

    fun eksikler(tanim: IzinTanimi): Array<String> = izinler.eksikAndroidIzinleri(tanim)

    fun rolIntenti() = izinler.rolIstemeIntenti()

    fun ayarlarIntenti() = izinler.uygulamaAyarlariIntenti()

    fun nedenGoster(acik: Boolean) {
        _durum.value = _durum.value.copy(nedenAcik = acik)
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer { konteyner().let { IzinlerViewModel(it.izinYoneticisi, it.ayarDeposu) } }
        }
    }
}
