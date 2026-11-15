package com.atasoy.eymentelefon.gorusme.ui

// ✅ HAZIR — DEĞİŞTİRME (sadece "GOREV-11" işaretli satırlar değişir).
// Kilit ekranı üstünde açılan arama penceresi. Hangi ekranın görüneceğine durum karar verir.

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.gorusme.GorusmeAsamasi
import com.atasoy.eymentelefon.gorusme.GorusmeYoneticisi
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import kotlinx.coroutines.delay

class CallActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            EymenTelefonTemasi(zorlaKoyu = true) {
                val liste by GorusmeYoneticisi.gorusmeler.collectAsStateWithLifecycle()
                val gorusme = GorusmeYoneticisi.oncelikli(liste)

                // Tüm aramalar bitince kısa bekleyip kapan
                LaunchedEffect(liste.isEmpty()) {
                    if (liste.isEmpty()) {
                        delay(KAPANMA_GECIKMESI_MS)
                        finishAndRemoveTask()
                    }
                }

                if (gorusme == null) {
                    GeciciAramaEkrani(stringResource(R.string.gorusme_bitti), onKapat = null)
                } else {
                    when (gorusme.asama) {
                        GorusmeAsamasi.ARANIYOR, GorusmeAsamasi.BAGLANIYOR ->
                            GidenAramaEkrani()
                        GorusmeAsamasi.CALIYOR ->
                            GelenAramaEkrani()
                        GorusmeAsamasi.AKTIF, GorusmeAsamasi.BEKLEMEDE, GorusmeAsamasi.BITTI ->
                            GorusmeEkrani()
                    }
                }
            }
        }
    }

    companion object {
        private const val KAPANMA_GECIKMESI_MS = 800L

        fun ac(context: Context) {
            context.startActivity(
                Intent(context, CallActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION),
            )
        }
    }
}

/** GOREV-11 bitince bu fonksiyon SİLİNİR. */
@Composable
private fun GeciciAramaEkrani(metin: String, onKapat: (() -> Unit)?) {
    Column(
        modifier = Modifier.fillMaxSize().background(EymenRenk.aramaZemini),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.gecici_ekran, metin, "GOREV-11"),
            style = EymenYazi.baslik2,
            color = EymenRenk.metin1,
            textAlign = TextAlign.Center,
        )
        if (onKapat != null) {
            Spacer(Modifier.height(EymenBosluk.xxl))
            Button(
                onClick = onKapat,
                colors = ButtonDefaults.buttonColors(containerColor = EymenRenk.kapatKirmizi),
            ) { Text(stringResource(R.string.kontrol_kapat)) }
        }
    }
}
