package com.atasoy.eymentelefon.gorusme

// ⬜ CODEX YAZACAK — GOREV-12
// Şu an tüm aramalara izin verir. GOREV-12: EngelDeposu.engelliMi(numara) true ise
// setDisallowCall(true).setRejectCall(true).setSkipNotification(true).setSkipCallLog(false)
// Not: onScreenCall ana thread'de; depo sorgusu için kısa süreli coroutine kullan, 5 sn içinde respondToCall çağır.

import android.telecom.Call
import android.telecom.CallScreeningService

class EymenCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        respondToCall(callDetails, CallResponse.Builder().build()) // GOREV-12
    }
}
