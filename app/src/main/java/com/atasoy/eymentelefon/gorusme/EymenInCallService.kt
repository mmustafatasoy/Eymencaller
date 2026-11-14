package com.atasoy.eymentelefon.gorusme

// ✅ HAZIR — DEĞİŞTİRME (sadece "GOREV-11" işaretli satır eklenecek).
// Varsayılan telefon uygulaması olunca sistem tüm aramaları bu servise bağlar.

import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import com.atasoy.eymentelefon.gorusme.ui.CallActivity

class EymenInCallService : InCallService() {

    override fun onCreate() {
        super.onCreate()
        GorusmeYoneticisi.servisBaglandi(this)
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        GorusmeYoneticisi.ekle(call)
        // Sistem tarafından bağlı InCallService arka planda activity açabilir (BAL istisnası).
        CallActivity.ac(this)
        // GOREV-11: GorusmeBildirimi.goster(this) — gelen arama tam ekran bildirimi + devam eden arama bildirimi
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        GorusmeYoneticisi.cikar(call)
        // GOREV-11: arama kalmadıysa GorusmeBildirimi.kaldir(this)
    }

    @Deprecated("API 34'te eskidi; yerine onCallEndpointChanged var ama tüm sürümlerde çağrılır")
    @Suppress("DEPRECATION")
    override fun onCallAudioStateChanged(audioState: CallAudioState?) {
        super.onCallAudioStateChanged(audioState)
        audioState?.let(GorusmeYoneticisi::sesDegisti)
    }

    override fun onDestroy() {
        GorusmeYoneticisi.servisAyrildi(this)
        super.onDestroy()
    }
}
