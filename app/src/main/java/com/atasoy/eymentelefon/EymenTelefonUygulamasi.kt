package com.atasoy.eymentelefon

// ✅ HAZIR — DEĞİŞTİRME.

import android.app.Application
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici

class EymenTelefonUygulamasi : Application() {

    lateinit var konteyner: UygulamaKonteyneri
        private set

    override fun onCreate() {
        super.onCreate()
        HataKaydedici.kur(this)
        konteyner = UygulamaKonteyneri(this)
    }
}
