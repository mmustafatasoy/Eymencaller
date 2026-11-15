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
        // CallStyle etkinse Android kilitli ekranda FSI, açıksa heads-up seçer.
        // Bildirim kapalıysa veya FSI yetkisi yoksa doğrudan CallActivity açılır.
        GorusmeBildirimi.goster(this)
        if (
            GorusmeYoneticisi.oncelikli()?.asama != GorusmeAsamasi.CALIYOR ||
            !GorusmeBildirimi.tamEkranBildirimMumkun(this)
        ) {
            CallActivity.ac(this)
        }
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        GorusmeYoneticisi.cikar(call)
        if (GorusmeYoneticisi.gorusmeler.value.isEmpty()) GorusmeBildirimi.kaldir(this)
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
