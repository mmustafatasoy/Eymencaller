package com.atasoy.eymentelefon.gorusme

// ✅ HAZIR. GorusmeAlicisi (dışarıya kapalı): bildirim eylemleri (reddet / kapat) ve "Hatırlat" alarmı.
// CevapsizAramaAlicisi (dışarıya açık): sadece Telecom'un cevapsız arama isteğini işler.

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.telecom.TelecomManager

class GorusmeAlicisi : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            EYLEM_REDDET -> intent.getStringExtra(EK_ID)?.let { GorusmeYoneticisi.reddet(it) }
            EYLEM_KAPAT -> intent.getStringExtra(EK_ID)?.let(GorusmeYoneticisi::kapat)
            EYLEM_HATIRLAT -> {
                val numara = intent.getStringExtra(EK_NUMARA) ?: return
                GorusmeBildirimi.hatirlatGoster(context, numara, intent.getStringExtra(EK_AD))
            }
        }
    }

    companion object {
        const val EYLEM_REDDET = "com.atasoy.eymentelefon.REDDET"
        const val EYLEM_KAPAT = "com.atasoy.eymentelefon.KAPAT"
        const val EYLEM_HATIRLAT = "com.atasoy.eymentelefon.HATIRLAT"
        const val EK_ID = "id"
        const val EK_NUMARA = "numara"
        const val EK_AD = "ad"
        private const val HATIRLAT_MS = 10 * 60 * 1000L

        fun yayin(context: Context, eylem: String, id: String, istekKodu: Int): PendingIntent =
            PendingIntent.getBroadcast(
                context, istekKodu,
                Intent(context, GorusmeAlicisi::class.java).setAction(eylem).putExtra(EK_ID, id),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        /** 10 dk sonra hatırlatma (kesin olmayan alarm: ek izin gerekmez). */
        fun hatirlatKur(context: Context, numara: String, ad: String?) {
            val pi = PendingIntent.getBroadcast(
                context, numara.hashCode(),
                Intent(context, GorusmeAlicisi::class.java)
                    .setAction(EYLEM_HATIRLAT)
                    .putExtra(EK_NUMARA, numara)
                    .putExtra(EK_AD, ad),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            context.getSystemService(AlarmManager::class.java)
                ?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + HATIRLAT_MS, pi)
        }
    }
}

/** Varsayılan telefon uygulaması olunca cevapsız arama bildirimini sistem değil biz gösteririz. */
class CevapsizAramaAlicisi : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelecomManager.ACTION_SHOW_MISSED_CALLS_NOTIFICATION) return
        GorusmeBildirimi.cevapsizGoster(
            context,
            intent.getIntExtra(TelecomManager.EXTRA_NOTIFICATION_COUNT, 0),
            intent.getStringExtra(TelecomManager.EXTRA_NOTIFICATION_PHONE_NUMBER),
        )
    }
}
