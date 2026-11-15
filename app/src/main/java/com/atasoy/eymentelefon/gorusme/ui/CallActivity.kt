package com.atasoy.eymentelefon.gorusme.ui

// ✅ HAZIR. Kilit ekranı üstünde açılan arama penceresi. Hangi ekranın görüneceğine durum karar verir:
// CALIYOR → GelenAramaEkrani (14/15) · diğer tüm aşamalar → GorusmeEkrani (11/12/13). Tüm aramalar bitince kapanır.

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PowerManager
import android.telecom.TelecomManager
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atasoy.eymentelefon.MainActivity
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.gorusme.GorusmeAlicisi
import com.atasoy.eymentelefon.gorusme.GorusmeAsamasi
import com.atasoy.eymentelefon.gorusme.GorusmeYoneticisi
import com.atasoy.eymentelefon.gorusme.SesYolu
import com.atasoy.eymentelefon.ui.navigasyon.AnaSekme
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import kotlinx.coroutines.delay

class CallActivity : ComponentActivity() {

    private val vm: GorusmeViewModel by viewModels { GorusmeViewModel.Fabrika }
    private var yakinlikKilidi: PowerManager.WakeLock? = null

    /** Ekran kilitliyse gelen aramada "kaydırarak cevapla", değilse Reddet / Kabul Et (iOS gibi). */
    private var kilitli by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Arama ekranı her zaman koyu: durum/gezinme çubuğu ikonları beyaz
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        kilitli = getSystemService(KeyguardManager::class.java)?.isKeyguardLocked == true
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        bildirimEylemi(intent)

        setContent {
            EymenTelefonTemasi(zorlaKoyu = true) {
                val durum by vm.durum.collectAsStateWithLifecycle()
                val poster by vm.poster.collectAsStateWithLifecycle()
                val liste by GorusmeYoneticisi.gorusmeler.collectAsStateWithLifecycle()
                val g = durum.gorusme

                // Tüm aramalar bitince kısa bekleyip kapan
                LaunchedEffect(liste.isEmpty()) {
                    if (liste.isEmpty()) {
                        delay(KAPANMA_GECIKMESI_MS)
                        finishAndRemoveTask()
                    }
                }

                // Kulakta ekran kapansın: sadece aktif görüşmede ve ses telefon hoparlöründeyken
                val yakinlik = g?.asama == GorusmeAsamasi.AKTIF && durum.ses.yol == SesYolu.TELEFON && !durum.tusTakimiAcik
                DisposableEffect(yakinlik) {
                    if (yakinlik) yakinlikAc() else yakinlikKapat()
                    onDispose { yakinlikKapat() }
                }

                val gelen = g?.asama == GorusmeAsamasi.CALIYOR
                Box(Modifier.fillMaxSize().background(AramaEkraniRenk.siyah)) {
                    AnimatedContent(
                        targetState = gelen,
                        transitionSpec = { fadeIn(tween(GECIS_MS)) togetherWith fadeOut(tween(GECIS_MS)) },
                        label = "aramaEkrani",
                    ) { caliyor ->
                        if (caliyor) {
                            GelenAramaEkrani(durum, kilitli, vm::olay, onHatirlat = {
                                g?.let {
                                    GorusmeAlicisi.hatirlatKur(this@CallActivity, it.numara, durum.kisi?.ad ?: it.sistemAdi)
                                    GorusmeYoneticisi.reddet(it.id)
                                }
                            }, poster = poster)
                        } else {
                            GorusmeEkrani(durum, vm::olay, onEkle = {
                                startActivity(MainActivity.sekmeIntenti(this@CallActivity, AnaSekme.TUSLAR))
                            }, poster = poster)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        kilitli = getSystemService(KeyguardManager::class.java)?.isKeyguardLocked == true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        bildirimEylemi(intent)
    }

    /** Bildirimdeki "Cevapla" doğrudan bu ekranı açar (Android 12+ trampoline kuralı). */
    private fun bildirimEylemi(intent: Intent?) {
        if (intent?.action == EYLEM_CEVAPLA) {
            intent.getStringExtra(EK_ID)?.let(GorusmeYoneticisi::cevapla)
        }
    }

    /** Çalarken ses tuşları zili susturur. */
    @SuppressLint("MissingPermission") // Varsayılan telefon uygulaması silenceRinger çağırabilir
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val caliyor = GorusmeYoneticisi.oncelikli()?.asama == GorusmeAsamasi.CALIYOR
        if (caliyor && (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            try {
                getSystemService(TelecomManager::class.java)?.silenceRinger()
            } catch (e: SecurityException) {
                HataKaydedici.kaydet("CallActivity.silenceRinger", e)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("WakelockTimeout") // Görüşme süresince tutulur, görüşme bitince/ekran kapanınca bırakılır
    private fun yakinlikAc() {
        if (yakinlikKilidi?.isHeld == true) return
        val pm = getSystemService(PowerManager::class.java) ?: return
        if (!pm.isWakeLockLevelSupported(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK)) return
        yakinlikKilidi = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "EymenTelefon:yakinlik").also { it.acquire() }
    }

    private fun yakinlikKapat() {
        yakinlikKilidi?.let { if (it.isHeld) it.release() }
        yakinlikKilidi = null
    }

    override fun onDestroy() {
        yakinlikKapat()
        super.onDestroy()
    }

    companion object {
        const val EYLEM_CEVAPLA = "com.atasoy.eymentelefon.CEVAPLA"
        const val EK_ID = "id"
        private const val KAPANMA_GECIKMESI_MS = 800L
        private const val GECIS_MS = 250

        fun ac(context: Context) {
            context.startActivity(
                Intent(context, CallActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION),
            )
        }
    }
}
