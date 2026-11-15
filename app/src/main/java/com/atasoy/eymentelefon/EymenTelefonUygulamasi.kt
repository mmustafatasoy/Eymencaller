package com.atasoy.eymentelefon

// ✅ HAZIR — DEĞİŞTİRME.

import android.app.Application
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.gorusme.GorusmeBildirimi

class EymenTelefonUygulamasi : Application() {

    lateinit var konteyner: UygulamaKonteyneri
        private set

    override fun onCreate() {
        super.onCreate()
        HataKaydedici.kur(this)
        konteyner = UygulamaKonteyneri(this)
        GorusmeBildirimi.kanallariOlustur(this)
    }
}
