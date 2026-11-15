package com.atasoy.eymentelefon

// ✅ HAZIR — DEĞİŞTİRME. Elle bağımlılık enjeksiyonu (Hilt/Koin YASAK).
// ViewModel fabrikalarında:  initializer { XxxViewModel(konteyner().rehberDeposu) }

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.atasoy.eymentelefon.cekirdek.arama.AramaBaslatici
import com.atasoy.eymentelefon.cekirdek.ayar.AyarDeposu
import com.atasoy.eymentelefon.cekirdek.izin.IzinYoneticisi
import com.atasoy.eymentelefon.veri.engel.EngelDeposu
import com.atasoy.eymentelefon.veri.gecmis.AramaGecmisiDeposu
import com.atasoy.eymentelefon.veri.poster.PosterDeposu
import com.atasoy.eymentelefon.veri.rehber.RehberDeposu

class UygulamaKonteyneri(uygulama: Application) {
    val ayarDeposu = AyarDeposu(uygulama)
    val izinYoneticisi = IzinYoneticisi(uygulama)
    val aramaBaslatici = AramaBaslatici(uygulama, izinYoneticisi)
    val rehberDeposu by lazy { RehberDeposu(uygulama) }
    val aramaGecmisiDeposu by lazy { AramaGecmisiDeposu(uygulama) }
    val engelDeposu by lazy { EngelDeposu(uygulama) }
    val kisiYazici by lazy { com.atasoy.eymentelefon.veri.rehber.KisiYazici(uygulama) } // V12-10
    val posterDeposu by lazy { PosterDeposu(uygulama) } // V12-05
}

fun Context.konteyner(): UygulamaKonteyneri =
    (applicationContext as EymenTelefonUygulamasi).konteyner

fun CreationExtras.konteyner(): UygulamaKonteyneri =
    (checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as EymenTelefonUygulamasi).konteyner
