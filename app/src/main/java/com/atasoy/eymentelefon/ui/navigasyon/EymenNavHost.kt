package com.atasoy.eymentelefon.ui.navigasyon

// ✅ HAZIR. Rota, argüman ve geçiş animasyonları DEĞİŞTİRİLMEZ.

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.ekranlar.anaekran.AnaEkran
import com.atasoy.eymentelefon.ui.ekranlar.arama.AramaEkrani
import com.atasoy.eymentelefon.ui.ekranlar.ayarlar.AyarlarEkrani
import com.atasoy.eymentelefon.ui.ekranlar.ayarlar.EngellenenlerEkrani
import com.atasoy.eymentelefon.ui.ekranlar.ayarlar.HataKayitlariEkrani
import com.atasoy.eymentelefon.ui.ekranlar.ayarlar.HizliYanitlarEkrani
import com.atasoy.eymentelefon.ui.ekranlar.izineksik.IzinEksikUyarisi
import com.atasoy.eymentelefon.ui.ekranlar.izinler.IzinlerEkrani
import com.atasoy.eymentelefon.ui.ekranlar.karsilama.KarsilamaEkrani
import com.atasoy.eymentelefon.ui.ekranlar.kisidetay.KisiDetayEkrani
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import kotlinx.coroutines.launch

private const val GECIS_MS = 280

@OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@Composable
fun EymenNavHost(
    nav: NavHostController,
    baslangic: String,
    baslangicSekme: AnaSekme,
    baslangicNumara: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val k = remember { context.konteyner() }
    val kapsam = rememberCoroutineScope()
    val kisiDetay: (Long, String?) -> Unit = { id, numara -> nav.navigate(Rotalar.kisiDetay(id, numara)) }
    val geri: () -> Unit = { nav.popBackStack() }

    // V13-08: ortak öğe geçişleri için NavHost sarılır
    androidx.compose.animation.SharedTransitionLayout {
    androidx.compose.runtime.CompositionLocalProvider(com.atasoy.eymentelefon.ui.bilesenler.LocalPaylasimKapsami provides this) {
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
            KarsilamaEkrani(onBasla = {
                kapsam.launch { k.ayarDeposu.ilkAcilisiTamamla() }
                nav.navigate(Rotalar.izinler()) { popUpTo(Rotalar.KARSILAMA) { inclusive = true } }
            })
        }

        composable(
            Rotalar.IZINLER,
            arguments = listOf(navArgument(Rotalar.Arguman.AYAR_MODU) { type = NavType.BoolType; defaultValue = false }),
        ) { giris ->
            val ayarModu = giris.arguments?.getBoolean(Rotalar.Arguman.AYAR_MODU) ?: false
            IzinlerEkrani(
                ayarModu = ayarModu,
                onDevam = {
                    if (ayarModu) {
                        nav.popBackStack()
                    } else {
                        nav.navigate(Rotalar.ana(baslangicSekme)) { popUpTo(nav.graph.id) { inclusive = true } }
                    }
                },
                onGeri = geri,
            )
        }

        composable(
            Rotalar.ANA,
            arguments = listOf(
                navArgument(Rotalar.Arguman.SEKME) { type = NavType.StringType; defaultValue = baslangicSekme.name },
                navArgument(Rotalar.Arguman.NUMARA) { type = NavType.StringType; defaultValue = baslangicNumara },
            ),
        ) { giris ->
            val sekmeAdi = giris.arguments?.getString(Rotalar.Arguman.SEKME)
            androidx.compose.runtime.CompositionLocalProvider(com.atasoy.eymentelefon.ui.bilesenler.LocalGecisKapsami provides this) {
            AnaEkran(
                baslangicSekme = AnaSekme.entries.firstOrNull { it.name == sekmeAdi } ?: baslangicSekme,
                baslangicNumara = giris.arguments?.getString(Rotalar.Arguman.NUMARA).orEmpty(),
                onKisiDetay = kisiDetay,
                onArama = { nav.navigate(Rotalar.ARAMA) },
                onAyarlar = { nav.navigate(Rotalar.AYARLAR) },
            )
            }
        }

        composable(
            Rotalar.KISI_DETAY,
            arguments = listOf(
                navArgument(Rotalar.Arguman.KISI_ID) { type = NavType.LongType },
                navArgument(Rotalar.Arguman.NUMARA) { type = NavType.StringType; defaultValue = "" },
            ),
        ) {
            androidx.compose.runtime.CompositionLocalProvider(com.atasoy.eymentelefon.ui.bilesenler.LocalGecisKapsami provides this) {
                KisiDetayEkrani(onGeri = geri, onPoster = { anahtar, ad -> nav.navigate(Rotalar.poster(anahtar, ad)) })
            }
        }

        composable(
            Rotalar.KISI_DUZENLE,
            arguments = listOf(
                navArgument(Rotalar.Arguman.KISI_ID) { type = NavType.LongType },
                navArgument(Rotalar.Arguman.NUMARA) { type = NavType.StringType; defaultValue = "" },
            ),
        ) {
            com.atasoy.eymentelefon.ui.ekranlar.kisiduzenle.KisiDuzenleEkrani(
                onVazgec = geri,
                onBitti = { id, yeni ->
                    if (yeni) {
                        nav.navigate(Rotalar.kisiDetay(id)) { popUpTo(Rotalar.KISI_DUZENLE) { inclusive = true } }
                    } else {
                        nav.popBackStack()
                    }
                },
                onPoster = { anahtar, ad -> nav.navigate(Rotalar.poster(anahtar, ad)) },
            )
        }

        composable(
            Rotalar.POSTER,
            arguments = listOf(
                navArgument(Rotalar.Arguman.LOOKUP) { type = NavType.StringType },
                navArgument(Rotalar.Arguman.AD) { type = NavType.StringType; defaultValue = "" },
            ),
        ) { com.atasoy.eymentelefon.ui.ekranlar.poster.PosterEkrani(onGeri = geri) }

        composable(Rotalar.ARAMA) { AramaEkrani(onGeri = geri, onKisiDetay = kisiDetay) }

        composable(Rotalar.AYARLAR) {
            AyarlarEkrani(
                onGeri = geri,
                onHataKayitlari = { nav.navigate(Rotalar.HATA_KAYITLARI) },
                onEngellenenler = { nav.navigate(Rotalar.ENGELLENENLER) },
                onHizliYanitlar = { nav.navigate(Rotalar.HIZLI_YANITLAR) },
            )
        }
        composable(Rotalar.HATA_KAYITLARI) { HataKayitlariEkrani(onGeri = geri) }
        composable(Rotalar.ENGELLENENLER) { EngellenenlerEkrani(onGeri = geri) }
        composable(Rotalar.HIZLI_YANITLAR) { HizliYanitlarEkrani(onGeri = geri) }
    }
    }
    }

    // 17 · İzin uyarısı: herhangi bir ekranda "Ara"ya basılıp izin eksikse açılır
    val istek by k.aramaBaslatici.izinEksik.collectAsStateWithLifecycle()
    if (istek != null) IzinEksikUyarisi()
}
