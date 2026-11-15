package com.atasoy.eymentelefon.ui.navigasyon

// ✅ HAZIR İSKELET — Sadece "GOREV-NN" işaretli satırlar, o görevde gerçek ekranla değiştirilir.
// Rota, argüman ve geçiş animasyonları DEĞİŞTİRİLMEZ.

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.ui.ekranlar.karsilama.KarsilamaEkrani
import com.atasoy.eymentelefon.ui.ekranlar.izinler.IzinlerEkrani
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi

private const val GECIS_MS = 280

@Composable
fun EymenNavHost(
    nav: NavHostController,
    baslangic: String,
    baslangicSekme: AnaSekme,
    baslangicNumara: String,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = nav,
        startDestination = baslangic,
        modifier = modifier.fillMaxSize().background(EymenRenk.arkaPlan),
        enterTransition = {
            slideInHorizontally(tween(GECIS_MS, easing = FastOutSlowInEasing)) { it / 6 } + fadeIn(tween(GECIS_MS))
        },
        exitTransition = { fadeOut(tween(GECIS_MS)) },
        popEnterTransition = { fadeIn(tween(GECIS_MS)) },
        popExitTransition = {
            slideOutHorizontally(tween(GECIS_MS, easing = FastOutSlowInEasing)) { it / 6 } + fadeOut(tween(GECIS_MS))
        },
    ) {
        composable(Rotalar.KARSILAMA) {
            KarsilamaEkrani(onBasla = { nav.navigate(Rotalar.izinler()) })
        }

        composable(
            Rotalar.IZINLER,
            arguments = listOf(navArgument(Rotalar.Arguman.AYAR_MODU) { type = NavType.BoolType; defaultValue = false }),
        ) { giris ->
            val ayarModu = giris.arguments?.getBoolean(Rotalar.Arguman.AYAR_MODU) ?: false
            IzinlerEkrani(
                ayarModu = ayarModu,
                onDevam = {
                    nav.navigate(Rotalar.ana()) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onGeri = { nav.popBackStack() },
            )
        }

        composable(
            Rotalar.ANA,
            arguments = listOf(
                navArgument(Rotalar.Arguman.SEKME) { type = NavType.StringType; defaultValue = baslangicSekme.name },
                navArgument(Rotalar.Arguman.NUMARA) { type = NavType.StringType; defaultValue = baslangicNumara },
            ),
        ) {
            GeciciEkran("Ana ekran (Favoriler · Son · Rehber · Tuşlar)", "GOREV-04")
            // GOREV-04: AnaEkran(baslangicSekme = ..., baslangicNumara = ..., nav = nav)
        }

        composable(
            Rotalar.KISI_DETAY,
            arguments = listOf(
                navArgument(Rotalar.Arguman.KISI_ID) { type = NavType.LongType },
                navArgument(Rotalar.Arguman.NUMARA) { type = NavType.StringType; defaultValue = "" },
            ),
        ) {
            GeciciEkran("06 Kişi detayı", "GOREV-08")
        }

        composable(Rotalar.ARAMA) { GeciciEkran("07 Arama", "GOREV-09") }
        composable(Rotalar.AYARLAR) { GeciciEkran("11 Ayarlar", "GOREV-13") }
        composable(Rotalar.HATA_KAYITLARI) { GeciciEkran("Hata kayıtları", "GOREV-13") }
        composable(Rotalar.ENGELLENENLER) { GeciciEkran("Engellenen numaralar", "GOREV-12") }
        composable(Rotalar.HIZLI_YANITLAR) { GeciciEkran("Hızlı yanıt mesajları", "GOREV-13") }
    }

    // GOREV-10: 12 numaralı alt sayfa her ekranın üstünde açılır:
    // val istek by konteyner.aramaBaslatici.izinEksik.collectAsStateWithLifecycle()
    // istek?.let { IzinEksikAltSayfa(istek = it, onTamam = ..., onVazgec = ...) }
}

/** Görevler tamamlandıkça kullanımı kalkar; GOREV-14'te SİLİNİR. */
@Composable
private fun GeciciEkran(ad: String, gorev: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.gecici_ekran, ad, gorev),
            style = EymenYazi.baslik2,
            color = EymenRenk.metin1,
            textAlign = TextAlign.Center,
        )
    }
}
