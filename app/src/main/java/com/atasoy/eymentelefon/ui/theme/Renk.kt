package com.atasoy.eymentelefon.ui.theme

// ✅ HAZIR — DEĞİŞTİRME. Kaynak: docs/01_TASARIM_SISTEMI.md §1
// Kod içinde çıplak Color(0xFF...) YASAK; her zaman EymenRenk.xxx kullan.

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class EymenRenkleri(
    val arkaPlan: Color,
    val yuzey1: Color,
    val yuzey2: Color,
    val yuzey3: Color,
    val metin1: Color,
    val metin2: Color,
    val metin3: Color,
    val vurgu: Color,
    val vurgu2: Color,
    val araYesil: Color,
    val kapatKirmizi: Color,
    val cevapsiz: Color,
    val uyari: Color,
    val aramaZemini: Color,
    /** Vurgu zeminli öğelerin üstündeki ikon/yazı rengi (seçili sekme ikonu, Ara aksiyonu). */
    val vurguUstu: Color,
    val koyuMu: Boolean,
)

val KoyuRenkler = EymenRenkleri(
    arkaPlan = Color(0xFF0A0F1C),
    yuzey1 = Color(0xFF121A2B),
    yuzey2 = Color(0xFF1A2438),
    yuzey3 = Color(0xFF24304A),
    metin1 = Color(0xFFEAF0FF),
    metin2 = Color(0xFFA9B4CC),
    metin3 = Color(0xFF7C89A6),
    vurgu = Color(0xFF19D3A2),
    vurgu2 = Color(0xFF3D7BFF),
    araYesil = Color(0xFF22C55E),
    kapatKirmizi = Color(0xFFE11D48),
    cevapsiz = Color(0xFFFF5A5F),
    uyari = Color(0xFFFFB547),
    aramaZemini = Color(0xFF080C18),
    vurguUstu = Color(0xFF0B1220),
    koyuMu = true,
)

val AcikRenkler = EymenRenkleri(
    arkaPlan = Color(0xFFF5F7FB),
    yuzey1 = Color(0xFFFFFFFF),
    yuzey2 = Color(0xFFEEF2F8),
    yuzey3 = Color(0xFFDDE3EE),
    metin1 = Color(0xFF0B1220),
    metin2 = Color(0xFF46536E),
    metin3 = Color(0xFF6B7894),
    vurgu = Color(0xFF0FB58A),
    vurgu2 = Color(0xFF2F6BFF),
    araYesil = Color(0xFF16A34A),
    kapatKirmizi = Color(0xFFE11D48),
    cevapsiz = Color(0xFFE5484D),
    uyari = Color(0xFFB7791F),
    aramaZemini = Color(0xFF080C18),
    vurguUstu = Color(0xFFFFFFFF),
    koyuMu = false,
)

/** Ayarlar › Vurgu rengi seçenekleri (sıra sabit, 0 = varsayılan). */
val VurguSecenekleri: List<Color> = listOf(
    Color(0xFF19D3A2),
    Color(0xFF3D7BFF),
    Color(0xFF8B5CF6),
    Color(0xFFFF8A5B),
    Color(0xFFF857A6),
)

/** Arama ekranları (08/09/10) her temada koyu renklerle çizilir. Sabit beyaz/şeffaflar. */
object AramaEkraniRenk {
    val kontrolZemini = Color(0x14FFFFFF) // %8 beyaz
    val rayZemini = Color(0x14FFFFFF)
    val rayCerceve = Color(0x14FFFFFF)
    val beyaz = Color(0xFFFFFFFF)
    val isikMor = Color(0x738B5CF6)
    val isikPembe = Color(0x6BFF4F8B)
    val isikYesil = Color(0x4019D3A2)
    val isikMavi = Color(0x4D3D7BFF)
}

/** Kişi avatarları: ad hash'i % 8 ile seçilir → EymenGradyan.avatar(ad) */
val AvatarGradyanlari: List<List<Color>> = listOf(
    listOf(Color(0xFF19D3A2), Color(0xFF3D7BFF)),
    listOf(Color(0xFFFF8A5B), Color(0xFFFF4F8B)),
    listOf(Color(0xFF8B5CF6), Color(0xFF3D7BFF)),
    listOf(Color(0xFFF7B733), Color(0xFFFC4A1A)),
    listOf(Color(0xFF22C1C3), Color(0xFF2D9CDB)),
    listOf(Color(0xFFF857A6), Color(0xFF7B61FF)),
    listOf(Color(0xFF43E97B), Color(0xFF38F9D7)),
    listOf(Color(0xFF5EE7DF), Color(0xFFB490CA)),
)

/** 135° gradyanlar (sol üst → sağ alt). */
object EymenGradyan {
    private fun capraz(renkler: List<Color>) =
        Brush.linearGradient(renkler, start = Offset.Zero, end = Offset.Infinite)

    fun vurgu(r: EymenRenkleri) = capraz(listOf(r.vurgu, r.vurgu2))
    fun ara(r: EymenRenkleri) = capraz(listOf(r.araYesil, r.vurgu))
    fun kapat(r: EymenRenkleri) = capraz(listOf(r.cevapsiz, r.kapatKirmizi))
    fun avatarRenkleri(ad: String): List<Color> =
        AvatarGradyanlari[Math.floorMod(ad.hashCode(), AvatarGradyanlari.size)]
    fun avatar(ad: String) = capraz(avatarRenkleri(ad))
}

internal val LocalEymenRenk = staticCompositionLocalOf { KoyuRenkler }

/** Kullanım: EymenRenk.vurgu */
val EymenRenk: EymenRenkleri
    @Composable @ReadOnlyComposable get() = LocalEymenRenk.current
