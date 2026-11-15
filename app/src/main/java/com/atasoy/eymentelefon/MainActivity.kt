package com.atasoy.eymentelefon

// ✅ HAZIR.
// Açılış kararı (docs/04_IZINLER.md §2): Karşılama → İzinler → Ana ekran.
// İzinler tamam değilse ana ekran HİÇ gösterilmez; karar verilene kadar splash ekranı tutulur.

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.navigasyon.AnaSekme
import com.atasoy.eymentelefon.ui.navigasyon.EymenNavHost
import com.atasoy.eymentelefon.ui.navigasyon.Rotalar
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.temaKoyuMu

class MainActivity : ComponentActivity() {

    /** Uygulama açıkken gelen hedef: tel: numarası ya da sekme (arama ekranından "Kişiler" / "Arama ekle", bildirimden "Son") */
    private var yeniHedef by mutableStateOf<Pair<AnaSekme, String?>?>(null)

    /** V12-12: sistemden gelen "kişiyi göster" isteği (rehber kişi kimliği) */
    private var yeniKisi by mutableStateOf<Long?>(null)

    /** V12-10/12: kişi ekle (-1, numara) / düzenle (id) isteği */
    private var duzenleHedefi by mutableStateOf<Pair<Long, String?>?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val k = konteyner()
        val ilkNumara = numaraCikar(intent)
        val ilkSekme = sekmeCikar(intent) ?: if (intent?.action == Intent.ACTION_CALL_BUTTON) AnaSekme.TUSLAR else null
        yeniKisi = kisiCikar(intent)
        duzenleHedefi = duzenleCikar(intent)
        var hazir = false
        splash.setKeepOnScreenCondition { !hazir }

        setContent {
            val ayarlar: Ayarlar? by k.ayarDeposu.ayarlar.collectAsStateWithLifecycle(initialValue = null)
            val a = ayarlar
            if (a != null) {
                SideEffect { hazir = true }
                // Durum çubuğu ikonları seçilen temaya uysun (sistem koyu, uygulama açık olabilir)
                val koyu = temaKoyuMu(TemaSecimi.SISTEM) // V12-02: tema her zaman sistemden
                LaunchedEffect(koyu) {
                    val stil = if (koyu) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                    enableEdgeToEdge(statusBarStyle = stil, navigationBarStyle = stil)
                }
                EymenTelefonTemasi(tema = TemaSecimi.SISTEM) {
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
                        when {
                            ilkNumara != null -> AnaSekme.TUSLAR
                            ilkSekme != null -> ilkSekme
                            else -> AnaSekme.entries.firstOrNull { it.name == a.sonSekme } ?: AnaSekme.SON
                        }
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
                            nav.navigate(Rotalar.izinler()) { popUpTo(nav.graph.id) { inclusive = true } }
                        }
                    }

                    // V12-12: başka uygulamadan "kişiyi aç" → kişi kartı
                    LaunchedEffect(yeniKisi) {
                        val id = yeniKisi ?: return@LaunchedEffect
                        if (k.izinYoneticisi.zorunlularTamam() && a.ilkAcilisTamam) nav.navigate(Rotalar.kisiDetay(id))
                        yeniKisi = null
                    }

                    // V12-10: ekle / düzenle → uygulama içi form
                    LaunchedEffect(duzenleHedefi) {
                        val (id, numara) = duzenleHedefi ?: return@LaunchedEffect
                        if (k.izinYoneticisi.zorunlularTamam() && a.ilkAcilisTamam) nav.navigate(Rotalar.kisiDuzenle(id, numara))
                        duzenleHedefi = null
                    }

                    // Uygulama açıkken gelen hedef → ilgili sekme (numara varsa Tuşlar'da dolu)
                    LaunchedEffect(yeniHedef) {
                        val (s, n) = yeniHedef ?: return@LaunchedEffect
                        if (k.izinYoneticisi.zorunlularTamam() && a.ilkAcilisTamam) {
                            nav.navigate(Rotalar.ana(s, n)) {
                                popUpTo(nav.graph.id) { inclusive = true }
                            }
                        }
                        yeniHedef = null
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val numara = numaraCikar(intent)
        val sekme = sekmeCikar(intent)
        yeniKisi = kisiCikar(intent)
        duzenleHedefi = duzenleCikar(intent)
        yeniHedef = when {
            numara != null -> AnaSekme.TUSLAR to numara
            sekme != null -> sekme to null
            intent.action == Intent.ACTION_CALL_BUTTON -> AnaSekme.TUSLAR to null
            else -> null
        }
    }

    /** V12-10/12: ACTION_INSERT (+ phone ekstrası) → (-1, numara) · ACTION_EDIT kişi URI → (id, null) */
    private fun duzenleCikar(intent: Intent?): Pair<Long, String?>? = when (intent?.action) {
        Intent.ACTION_INSERT -> -1L to intent.getStringExtra(android.provider.ContactsContract.Intents.Insert.PHONE)
        Intent.ACTION_EDIT -> intent.data?.let { veri ->
            runCatching {
                val kisiUri = android.provider.ContactsContract.Contacts.lookupContact(contentResolver, veri) ?: veri
                android.content.ContentUris.parseId(kisiUri).takeIf { it > 0 }
            }.getOrNull()
        }?.let { it to null }
        else -> null
    }

    /** V12-12: content://com.android.contacts/contacts/... (lookup ya da id) → kişi kimliği */
    private fun kisiCikar(intent: Intent?): Long? {
        val veri = intent?.data ?: return null
        if (intent.action != Intent.ACTION_VIEW || veri.authority != android.provider.ContactsContract.AUTHORITY) return null
        return runCatching {
            val kisiUri = android.provider.ContactsContract.Contacts.lookupContact(contentResolver, veri) ?: veri
            android.content.ContentUris.parseId(kisiUri).takeIf { it > 0 }
        }.getOrNull()
    }

    private fun sekmeCikar(intent: Intent?): AnaSekme? =
        intent?.getStringExtra(EK_SEKME)?.let { ad -> AnaSekme.entries.firstOrNull { it.name == ad } }

    companion object {
        /** Intent ekstrası: açılacak ana sekme (AnaSekme.name) */
        const val EK_SEKME = "eymen.sekme"

        fun sekmeIntenti(context: android.content.Context, sekme: AnaSekme): Intent =
            Intent(context, MainActivity::class.java)
                .putExtra(EK_SEKME, sekme.name)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

    private fun numaraCikar(intent: Intent?): String? {
        val veri = intent?.data ?: return null
        if (veri.scheme != "tel") return null
        return veri.schemeSpecificPart?.takeIf { it.isNotBlank() }
    }
}
