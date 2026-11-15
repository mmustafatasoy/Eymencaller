package com.atasoy.eymentelefon.gorusme

// ✅ HAZIR. Varsayılan telefon uygulaması olunca sistem tüm aramaları bu servise bağlar.
// Görevleri: aramaları GorusmeYoneticisi'ne vermek, arama ekranını açmak, bildirimi güncel tutmak.

import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import com.atasoy.eymentelefon.gorusme.ui.CallActivity
import com.atasoy.eymentelefon.konteyner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EymenInCallService : InCallService() {

    private val kapsam = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val adOnbellegi = HashMap<String, String?>()

    override fun onCreate() {
        super.onCreate()
        GorusmeYoneticisi.servisBaglandi(this)
        GorusmeBildirimi.kanallariOlustur(this)
        kapsam.launch {
            GorusmeYoneticisi.gorusmeler
                .map { GorusmeYoneticisi.oncelikli(it) }
                .distinctUntilChanged()
                .collect { g ->
                    val ad = g?.numara?.let { n ->
                        if (adOnbellegi.containsKey(n)) adOnbellegi[n]
                        else konteyner().rehberDeposu.numaradanKisi(n)?.ad.also { adOnbellegi[n] = it }
                    }
                    GorusmeBildirimi.guncelle(this@EymenInCallService, g, ad)
                }
        }
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        GorusmeYoneticisi.ekle(call)
        // Sistem tarafından bağlı InCallService arka planda activity açabilir (BAL istisnası).
        // Ekran kapalıysa ayrıca gelen arama bildirimindeki tam ekran intent devreye girer.
        CallActivity.ac(this)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        GorusmeYoneticisi.cikar(call)
    }

    @Deprecated("API 34'te eskidi; yerine onCallEndpointChanged var ama tüm sürümlerde çağrılır")
    @Suppress("DEPRECATION")
    override fun onCallAudioStateChanged(audioState: CallAudioState?) {
        super.onCallAudioStateChanged(audioState)
        audioState?.let(GorusmeYoneticisi::sesDegisti)
    }

    override fun onDestroy() {
        GorusmeYoneticisi.servisAyrildi(this)
        GorusmeBildirimi.kaldir(this)
        kapsam.cancel()
        super.onDestroy()
    }
}
