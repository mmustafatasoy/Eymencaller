package com.atasoy.eymentelefon

// ✅ HAZIR — DEĞİŞTİRME.
// Açılış kararı (docs/04_IZINLER.md §2): Karşılama → İzinler → Ana ekran.
// İzinler tamam değilse ana ekran HİÇ gösterilmez; karar verilene kadar splash ekranı tutulur.

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.atasoy.eymentelefon.cekirdek.ayar.Ayarlar
import com.atasoy.eymentelefon.ui.navigasyon.AnaSekme
import com.atasoy.eymentelefon.ui.navigasyon.EymenNavHost
import com.atasoy.eymentelefon.ui.navigasyon.Rotalar
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi

class MainActivity : ComponentActivity() {

    /** Uygulama açıkken başka uygulamadan gelen tel: numarası */
    private var yeniNumara by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val k = konteyner()
        val ilkNumara = numaraCikar(intent)
        var hazir = false
        splash.setKeepOnScreenCondition { !hazir }

        setContent {
            val ayarlar: Ayarlar? by k.ayarDeposu.ayarlar.collectAsStateWithLifecycle(initialValue = null)
            val a = ayarlar
            if (a != null) {
                SideEffect { hazir = true }
                EymenTelefonTemasi(tema = a.tema, vurguIndex = a.vurguIndex) {
                    val nav = rememberNavController()
                    // Başlangıç bir kez hesaplanır
                    val baslangic = remember {
                        when {
                            !a.ilkAcilisTamam -> Rotalar.KARSILAMA
                            !k.izinYoneticisi.zorunlularTamam() -> Rotalar.IZINLER
                            else -> Rotalar.ANA
                        }
                    }
                    val sekme = remember {
                        if (ilkNumara != null) AnaSekme.TUSLAR
                        else AnaSekme.entries.firstOrNull { it.name == a.sonSekme } ?: AnaSekme.SON
                    }
                    EymenNavHost(
                        nav = nav,
                        baslangic = baslangic,
                        baslangicSekme = sekme,
                        baslangicNumara = ilkNumara.orEmpty(),
                    )

                    // Ayarlardan izin geri alındıysa → İzinler ekranına dön
                    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                        val rota = nav.currentDestination?.route
                        val izinEkranindaDegil = rota != Rotalar.IZINLER && rota != Rotalar.KARSILAMA
                        if (a.ilkAcilisTamam && izinEkranindaDegil && !k.izinYoneticisi.zorunlularTamam()) {
                            nav.navigate(Rotalar.izinler()) { popUpTo(0) { inclusive = true } }
                        }
                    }

                    // Uygulama açıkken gelen tel: bağlantısı → Tuşlar sekmesi, numara dolu
                    LaunchedEffect(yeniNumara) {
                        val n = yeniNumara ?: return@LaunchedEffect
                        if (nav.currentDestination?.route == Rotalar.ANA) {
                            nav.navigate(Rotalar.ana(AnaSekme.TUSLAR, n)) {
                                popUpTo(Rotalar.ANA) { inclusive = true }
                            }
                        }
                        yeniNumara = null
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        yeniNumara = numaraCikar(intent)
    }

    private fun numaraCikar(intent: Intent?): String? {
        val veri = intent?.data ?: return null
        if (veri.scheme != "tel") return null
        return veri.schemeSpecificPart?.takeIf { it.isNotBlank() }
    }
}
