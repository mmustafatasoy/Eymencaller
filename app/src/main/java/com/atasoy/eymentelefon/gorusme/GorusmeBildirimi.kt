package com.atasoy.eymentelefon.gorusme

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.gorusme.ui.CallActivity
import com.atasoy.eymentelefon.konteyner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Arama bildirimleri: minSdk 29'da NotificationCompat.CallStyle, Android 12+'ta
 * platformun CallStyle görünümü. Eylemler çalışan InCallService işlemlerine gider.
 * Yeni manifest receiver eklenmez: alıcı sadece görüşme sürerken kayıtlıdır.
 */
object GorusmeBildirimi {
    private const val KANAL = "eymen_gorusme"
    private const val BILDIRIM = 7011
    private const val CEVAPLA = "com.atasoy.eymentelefon.gorusme.CEVAPLA"
    private const val REDDET = "com.atasoy.eymentelefon.gorusme.REDDET"
    private const val KAPAT = "com.atasoy.eymentelefon.gorusme.KAPAT"
    private const val KIMLIK = "gorusme_kimlik"

    private var islem: Job? = null
    private var aliciKayitli = false
    private var tamEkranUyarisiVerildi = false

    private val alici = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getStringExtra(KIMLIK) ?: return
            if (GorusmeYoneticisi.gorusmeler.value.none { it.id == id }) return
            when (intent.action) {
                CEVAPLA -> GorusmeYoneticisi.cevapla(id)
                REDDET -> GorusmeYoneticisi.reddet(id)
                KAPAT -> GorusmeYoneticisi.kapat(id)
            }
        }
    }

    /** EymenInCallService bağlandıktan sonra çağrılır; durum değiştikçe yenilenir. */
    fun goster(context: Context) {
        val app = context.applicationContext
        try {
            if (!aliciKayitli) {
                ContextCompat.registerReceiver(
                    app,
                    alici,
                    IntentFilter().apply {
                        addAction(CEVAPLA)
                        addAction(REDDET)
                        addAction(KAPAT)
                    },
                    ContextCompat.RECEIVER_NOT_EXPORTED,
                )
                aliciKayitli = true
            }
            if (islem == null) {
                islem = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
                    GorusmeYoneticisi.gorusmeler.collect { gorusmeler ->
                        if (gorusmeler.isEmpty()) kaldir(app)
                        else bildirimYenile(app, GorusmeYoneticisi.oncelikli(gorusmeler))
                    }
                }
            }
        } catch (hata: RuntimeException) {
            HataKaydedici.kaydet("GorusmeBildirimi.goster", hata)
        }
    }

    fun kaldir(context: Context) {
        val app = context.applicationContext
        islem?.cancel()
        islem = null
        try {
            app.getSystemService(NotificationManager::class.java)?.cancel(BILDIRIM)
            if (aliciKayitli) {
                app.unregisterReceiver(alici)
                aliciKayitli = false
            }
        } catch (hata: RuntimeException) {
            HataKaydedici.kaydet("GorusmeBildirimi.kaldir", hata)
        }
    }

    @SuppressLint("MissingPermission") // IzinYoneticisi ile kontrol edilir; SecurityException loglanır.
    private fun bildirimYenile(context: Context, g: GorusmeDurumu?) {
        if (g == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !context.konteyner().izinYoneticisi.izinVarMi(Manifest.permission.POST_NOTIFICATIONS)
        ) return

        val yonetici = context.getSystemService(NotificationManager::class.java) ?: return
        try {
            yonetici.createNotificationChannel(
                NotificationChannel(
                    KANAL,
                    context.getString(R.string.uygulama_adi),
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = context.getString(R.string.gorusme_gelen)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
            )
            val ekran = PendingIntent.getActivity(
                context, 7001,
                Intent(context, CallActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            val kisi = Person.Builder()
                .setName(g.sistemAdi?.takeIf(String::isNotBlank)
                    ?: g.numara.ifBlank { context.getString(R.string.gorusme_bilinmeyen) })
                .build()

            val gelen = g.asama == GorusmeAsamasi.CALIYOR
            val stil = if (gelen) {
                NotificationCompat.CallStyle.forIncomingCall(
                    kisi,
                    eylem(context, REDDET, g.id, 7012),
                    eylem(context, CEVAPLA, g.id, 7013),
                )
            } else {
                NotificationCompat.CallStyle.forOngoingCall(
                    kisi, eylem(context, KAPAT, g.id, 7014)
                )
            }

            var tamEkran = gelen
            if (tamEkran && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                !yonetici.canUseFullScreenIntent()
            ) {
                tamEkran = false
                if (!tamEkranUyarisiVerildi) {
                    tamEkranUyarisiVerildi = true
                    HataKaydedici.kaydet(
                        "GorusmeBildirimi.tamEkran",
                        IllegalStateException(context.getString(R.string.gorusme_tam_ekran_izni)),
                    )
                }
            }
            val bildirim = NotificationCompat.Builder(context, KANAL)
                .setSmallIcon(R.drawable.ik_telefon)
                .setContentIntent(ekran)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setAutoCancel(false)
                .setStyle(stil)
                .setFullScreenIntent(ekran, tamEkran)
                .build()
            yonetici.notify(BILDIRIM, bildirim)
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("GorusmeBildirimi.izin", hata)
        } catch (hata: RuntimeException) {
            HataKaydedici.kaydet("GorusmeBildirimi.yenile", hata)
        }
    }

    private fun eylem(context: Context, tur: String, id: String, kod: Int): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            kod,
            Intent(tur).apply {
                setPackage(context.packageName)
                putExtra(KIMLIK, id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
