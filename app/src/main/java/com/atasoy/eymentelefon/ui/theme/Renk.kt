package com.atasoy.eymentelefon.ui.theme

// ✅ v1.1 iOS tarzı renkler. Kaynak: docs/01_TASARIM_SISTEMI.md §1 · Kod içinde çıplak Color(0xFF…) YASAK.

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class EymenRenkleri(
    val arkaPlan: Color,
    val grupluArkaPlan: Color,
    val yuzey: Color,
    val tusZemini: Color,
    val tusBasili: Color,
    val etiket: Color,
    val ikincilEtiket: Color,
    val ucunculEtiket: Color,
    val ayirici: Color,
    val aramaAlani: Color,
    val sekmeCubugu: Color,
    val segmentSecili: Color,
    val anahtarKapali: Color,
    val vurgu: Color,
    val yesil: Color,
    val kirmizi: Color,
    val turuncu: Color,
    val gri: Color,
    val koyuMu: Boolean,
)

val AcikRenkler = EymenRenkleri(
    arkaPlan = Color(0xFFFFFFFF),
    grupluArkaPlan = Color(0xFFF2F2F7),
    yuzey = Color(0xFFFFFFFF),
    tusZemini = Color(0xFFE5E5EA),
    tusBasili = Color(0xFFBDBDC2),
    etiket = Color(0xFF000000),
    ikincilEtiket = Color(0xFF8A8A8E),
    ucunculEtiket = Color(0xFFC7C7CC),
    ayirici = Color(0xFFC6C6C8),
    aramaAlani = Color(0x1F767680),
    sekmeCubugu = Color(0xF0F9F9F9),
    segmentSecili = Color(0xFFFFFFFF),
    anahtarKapali = Color(0xFFE9E9EA),
    vurgu = Color(0xFF007AFF),
    yesil = Color(0xFF34C759),
    kirmizi = Color(0xFFFF3B30),
    turuncu = Color(0xFFFF9500),
    gri = Color(0xFF8E8E93),
    koyuMu = false,
)

val KoyuRenkler = EymenRenkleri(
    arkaPlan = Color(0xFF000000),
    grupluArkaPlan = Color(0xFF000000),
    yuzey = Color(0xFF1C1C1E),
    tusZemini = Color(0xFF333333),
    tusBasili = Color(0xFF737373),
    etiket = Color(0xFFFFFFFF),
    ikincilEtiket = Color(0xFF8D8D93),
    ucunculEtiket = Color(0xFF48484A),
    ayirici = Color(0xFF38383A),
    aramaAlani = Color(0x3D767680),
    sekmeCubugu = Color(0xF0161616),
    segmentSecili = Color(0xFF636366),
    anahtarKapali = Color(0xFF39393D),
    vurgu = Color(0xFF0A84FF),
    yesil = Color(0xFF30D158),
    kirmizi = Color(0xFFFF453A),
    turuncu = Color(0xFFFF9F0A),
    gri = Color(0xFF8E8E93),
    koyuMu = true,
)

/** Ayarlar › Vurgu Rengi (0 = mavi, varsayılan). Koyu temada aynı tonların parlak hali. */
val VurguSecenekleri: List<Pair<Color, Color>> = listOf(
    Color(0xFF007AFF) to Color(0xFF0A84FF),
    Color(0xFF34C759) to Color(0xFF30D158),
    Color(0xFFAF52DE) to Color(0xFFBF5AF2),
    Color(0xFFFF9500) to Color(0xFFFF9F0A),
    Color(0xFFFF2D55) to Color(0xFFFF375F),
)

/** Ayarlar satırlarındaki renkli ikon kareleri (iki temada aynı). */
object KareRenk {
    val mavi = Color(0xFF007AFF)
    val yesil = Color(0xFF34C759)
    val kirmizi = Color(0xFFFF3B30)
    val turuncu = Color(0xFFFF9500)
    val pembe = Color(0xFFFF2D55)
    val mor = Color(0xFF5856D6)
    val gri = Color(0xFF8E8E93)
}

/** Arama ekranları her temada koyu çizilir. */
object AramaEkraniRenk {
    val beyaz = Color(0xFFFFFFFF)
    val siyah = Color(0xFF000000)
    val ikincil = Color(0x9EFFFFFF) // %62
    val butonZemini = Color(0x2EFFFFFF) // %18
    val butonBasili = Color(0x59FFFFFF)
    val kaydirZemini = Color(0x29FFFFFF)
    val karartma = Color(0x66000000)
    val bitir = Color(0xFFFF3B30)
    val kabul = Color(0xFF34C759)
    val sayfaZemini = Color(0xF0FAFAFA)
    val sayfaMetni = Color(0xFF007AFF)
    val sayfaBaslik = Color(0xFF8A8A8E)
    val sayfaAyirici = Color(0xFFC6C6C8)

    /** Kişiye göre değişen koyu zemin (ad hash'i % 6) — üstte açık ton, altta koyu. */
    private val paletler = listOf(
        listOf(Color(0xFF5B5F73), Color(0xFF23252F), Color(0xFF101116)),
        listOf(Color(0xFF6E5A64), Color(0xFF2B2328), Color(0xFF121012)),
        listOf(Color(0xFF4F6A73), Color(0xFF1F2A2F), Color(0xFF0D1214)),
        listOf(Color(0xFF5F6B52), Color(0xFF262B21), Color(0xFF101310)),
        listOf(Color(0xFF6B5E4E), Color(0xFF2C261F), Color(0xFF13110D)),
        listOf(Color(0xFF55577A), Color(0xFF222333), Color(0xFF0E0E16)),
    )

    fun palet(ad: String): List<Color> = paletler[Math.floorMod(ad.hashCode(), paletler.size)]
}

/** iOS monogram avatarı: gri gradyan. */
object AvatarRenk {
    val ust = Color(0xFFA5ABB9)
    val alt = Color(0xFF858994)
    val metin = Color(0xFFFFFFFF)
    val firca: Brush = Brush.verticalGradient(listOf(ust, alt))
}

internal val LocalEymenRenk = staticCompositionLocalOf { AcikRenkler }

/** Kullanım: EymenRenk.vurgu */
val EymenRenk: EymenRenkleri
    @Composable @ReadOnlyComposable get() = LocalEymenRenk.current
