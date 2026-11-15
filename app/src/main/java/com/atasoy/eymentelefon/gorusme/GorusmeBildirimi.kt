package com.atasoy.eymentelefon.gorusme

// ✅ HAZIR. Bildirimler: gelen arama (tam ekran, CallStyle), devam eden arama, cevapsız arama, hatırlatma.

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import com.atasoy.eymentelefon.MainActivity
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.gorusme.ui.CallActivity
import com.atasoy.eymentelefon.ui.navigasyon.AnaSekme

object GorusmeBildirimi {

    const val KANAL_GELEN = "gelen_arama"
    const val KANAL_DEVAM = "devam_eden_arama"
    const val KANAL_CEVAPSIZ = "cevapsiz_arama"

    private const val ID_GORUSME = 1001
    private const val ID_CEVAPSIZ = 1002
    private const val ID_HATIRLAT = 1003

    fun kanallariOlustur(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        nm.createNotificationChannels(
            listOf(
                // Zil sesini sistem (Telecom) çalar; kanal sessizdir
                NotificationChannel(KANAL_GELEN, context.getString(R.string.kanal_gelen), NotificationManager.IMPORTANCE_HIGH).apply {
                    setSound(null, null)
                    enableVibration(false)
                },
                NotificationChannel(KANAL_DEVAM, context.getString(R.string.kanal_devam), NotificationManager.IMPORTANCE_LOW),
                NotificationChannel(KANAL_CEVAPSIZ, context.getString(R.string.kanal_cevapsiz), NotificationManager.IMPORTANCE_DEFAULT),
            ),
        )
    }

    /** Aramalar değiştikçe çağrılır (EymenInCallService). */
    fun guncelle(context: Context, gorusme: GorusmeDurumu?, ad: String?) {
        if (gorusme == null || gorusme.asama == GorusmeAsamasi.BITTI) {
            kaldir(context)
            return
        }
        val gorunen = ad ?: gorusme.sistemAdi ?: NumaraBicimleyici.bicimle(gorusme.numara)
            .ifBlank { context.getString(R.string.gorusme_bilinmeyen) }
        val kisi = Person.Builder().setName(gorunen).setImportant(true).build()
        val ekranIntenti = PendingIntent.getActivity(
            context, 0,
            Intent(context, CallActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val bildirim = if (gorusme.asama == GorusmeAsamasi.CALIYOR) {
            // Cevapla: activity PendingIntent (Android 12+ "trampoline" yasağı yüzünden yayın değil)
            val cevapla = PendingIntent.getActivity(
                context, 1,
                Intent(context, CallActivity::class.java)
                    .setAction(CallActivity.EYLEM_CEVAPLA)
                    .putExtra(CallActivity.EK_ID, gorusme.id)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            val reddet = GorusmeAlicisi.yayin(context, GorusmeAlicisi.EYLEM_REDDET, gorusme.id, 2)
            NotificationCompat.Builder(context, KANAL_GELEN)
                .setSmallIcon(R.drawable.ik_telefon)
                .setContentTitle(gorunen)
                .setContentText(context.getString(R.string.bildirim_gelen))
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(ekranIntenti)
                .setFullScreenIntent(ekranIntenti, true)
                .setStyle(NotificationCompat.CallStyle.forIncomingCall(kisi, reddet, cevapla))
                .build()
        } else {
            val kapat = GorusmeAlicisi.yayin(context, GorusmeAlicisi.EYLEM_KAPAT, gorusme.id, 3)
            NotificationCompat.Builder(context, KANAL_DEVAM)
                .setSmallIcon(R.drawable.ik_telefon)
                .setContentTitle(gorunen)
                .setContentText(
                    context.getString(
                        when (gorusme.asama) {
                            GorusmeAsamasi.ARANIYOR -> R.string.gorusme_araniyor
                            GorusmeAsamasi.BAGLANIYOR -> R.string.gorusme_baglaniyor
                            GorusmeAsamasi.BEKLEMEDE -> R.string.gorusme_beklemede
                            else -> R.string.bildirim_devam
                        },
                    ),
                )
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setUsesChronometer(gorusme.baglanmaZamani != null)
                .setWhen(gorusme.baglanmaZamani ?: System.currentTimeMillis())
                .setContentIntent(ekranIntenti)
                .addAction(R.drawable.ik_telefon, context.getString(R.string.bildirim_bitir), kapat)
                .build()
        }
        goster(context, ID_GORUSME, bildirim)
    }

    fun kaldir(context: Context) {
        NotificationManagerCompat.from(context).cancel(ID_GORUSME)
    }

    /** Telecom'un "cevapsız aramaları göster" isteği (varsayılan uygulama olunca sistem göstermez). */
    fun cevapsizGoster(context: Context, sayi: Int, numara: String?) {
        if (sayi <= 0) {
            NotificationManagerCompat.from(context).cancel(ID_CEVAPSIZ)
            return
        }
        val ac = PendingIntent.getActivity(
            context, 4, MainActivity.sekmeIntenti(context, AnaSekme.SON),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val b = NotificationCompat.Builder(context, KANAL_CEVAPSIZ)
            .setSmallIcon(R.drawable.ik_gelen)
            .setContentTitle(context.getString(R.string.bildirim_cevapsiz, sayi))
            .setContentText(numara?.let(NumaraBicimleyici::bicimle))
            .setCategory(NotificationCompat.CATEGORY_MISSED_CALL)
            .setAutoCancel(true)
            .setContentIntent(ac)
            .build()
        goster(context, ID_CEVAPSIZ, b)
    }

    /** "Hatırlat" (gelen aramada) 10 dk sonra: dokununca Tuşlar'da numara dolu açılır. */
    fun hatirlatGoster(context: Context, numara: String, ad: String?) {
        val ac = PendingIntent.getActivity(
            context, 5,
            Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", numara, null)).setClass(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val b = NotificationCompat.Builder(context, KANAL_CEVAPSIZ)
            .setSmallIcon(R.drawable.ik_alarm)
            .setContentTitle(context.getString(R.string.hatirlat_bildirim, ad ?: NumaraBicimleyici.bicimle(numara)))
            .setAutoCancel(true)
            .setContentIntent(ac)
            .build()
        goster(context, ID_HATIRLAT, b)
    }

    @SuppressLint("MissingPermission") // İzin aşağıda kontrol ediliyor
    private fun goster(context: Context, id: Int, bildirim: android.app.Notification) {
        val izinVar = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        if (!izinVar) return
        try {
            NotificationManagerCompat.from(context).notify(id, bildirim)
        } catch (e: RuntimeException) {
            HataKaydedici.kaydet("GorusmeBildirimi.goster", e)
        }
    }
}
