package com.atasoy.eymentelefon.gorusme

// ✅ HAZIR. Engellenen numaralardan gelen aramaları çaldırmadan reddeder (arama geçmişine "Engellendi" düşer).

import android.telecom.Call
import android.telecom.CallScreeningService
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.konteyner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class EymenCallScreeningService : CallScreeningService() {

    private val kapsam = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onScreenCall(callDetails: Call.Details) {
        // Sadece gelen aramalar elenir
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }
        val numara = callDetails.handle?.schemeSpecificPart.orEmpty()
        kapsam.launch {
            val k = konteyner()
            val engelli = withTimeoutOrNull(ZAMAN_ASIMI_MS) { k.engelDeposu.engelliMi(numara) } ?: false
            // V12-04: "Bilinmeyen Arayanları Sustur" açıksa rehberde olmayan numara çalmaz (kayda düşer)
            val sustur = !engelli && withTimeoutOrNull(ZAMAN_ASIMI_MS) {
                k.ayarDeposu.ayarlar.first().bilinmeyenSustur && numara.isNotBlank() && k.rehberDeposu.numaradanKisi(numara) == null
            } == true
            val yanit = if (sustur) {
                CallResponse.Builder().setSilenceCall(true).build()
            } else if (engelli) {
                CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipNotification(true)
                    .setSkipCallLog(false)
                    .build()
            } else {
                CallResponse.Builder().build()
            }
            try {
                respondToCall(callDetails, yanit)
            } catch (e: RuntimeException) {
                HataKaydedici.kaydet("EymenCallScreeningService", e)
            }
        }
    }

    override fun onDestroy() {
        kapsam.cancel()
        super.onDestroy()
    }

    private companion object {
        const val ZAMAN_ASIMI_MS = 3_000L
    }
}
