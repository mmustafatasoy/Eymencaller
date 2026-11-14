package com.atasoy.eymentelefon.ui.theme

// ✅ HAZIR — DEĞİŞTİRME.

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi

/**
 * Uygulamanın tek tema girişi.
 * @param tema Ayarlar'daki seçim
 * @param vurguIndex VurguSecenekleri içindeki sıra (0 = varsayılan yeşil)
 * @param zorlaKoyu Arama ekranları (CallActivity) için true
 */
@Composable
fun EymenTelefonTemasi(
    tema: TemaSecimi = TemaSecimi.SISTEM,
    vurguIndex: Int = 0,
    zorlaKoyu: Boolean = false,
    icerik: @Composable () -> Unit,
) {
    val sistemKoyu = isSystemInDarkTheme()
    val koyu = zorlaKoyu || when (tema) {
        TemaSecimi.SISTEM -> sistemKoyu
        TemaSecimi.KOYU -> true
        TemaSecimi.ACIK -> false
    }
    val renkler = remember(koyu, vurguIndex) {
        val temel = if (koyu) KoyuRenkler else AcikRenkler
        if (vurguIndex == 0) temel
        else temel.copy(vurgu = VurguSecenekleri[vurguIndex.coerceIn(VurguSecenekleri.indices)])
    }
    val m3 = if (koyu) {
        darkColorScheme(
            primary = renkler.vurgu, onPrimary = renkler.vurguUstu,
            secondary = renkler.vurgu2,
            background = renkler.arkaPlan, onBackground = renkler.metin1,
            surface = renkler.yuzey1, onSurface = renkler.metin1,
            surfaceVariant = renkler.yuzey2, onSurfaceVariant = renkler.metin2,
            error = renkler.cevapsiz,
        )
    } else {
        lightColorScheme(
            primary = renkler.vurgu, onPrimary = renkler.vurguUstu,
            secondary = renkler.vurgu2,
            background = renkler.arkaPlan, onBackground = renkler.metin1,
            surface = renkler.yuzey1, onSurface = renkler.metin1,
            surfaceVariant = renkler.yuzey2, onSurfaceVariant = renkler.metin2,
            error = renkler.cevapsiz,
        )
    }
    CompositionLocalProvider(LocalEymenRenk provides renkler) {
        MaterialTheme(colorScheme = m3, content = icerik)
    }
}
