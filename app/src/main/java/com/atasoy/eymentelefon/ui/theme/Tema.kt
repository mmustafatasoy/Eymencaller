package com.atasoy.eymentelefon.ui.theme

// ✅ v1.1 Tek tema girişi. Açık / koyu / sistem + vurgu rengi.

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi

@Composable
fun temaKoyuMu(tema: TemaSecimi): Boolean {
    val sistem = isSystemInDarkTheme()
    return when (tema) {
        TemaSecimi.SISTEM -> sistem
        TemaSecimi.KOYU -> true
        TemaSecimi.ACIK -> false
    }
}

@Composable
fun EymenTelefonTemasi(
    tema: TemaSecimi = TemaSecimi.SISTEM,
    vurguIndex: Int = 0,
    zorlaKoyu: Boolean = false,
    icerik: @Composable () -> Unit,
) {
    val koyu = zorlaKoyu || temaKoyuMu(tema)
    val renkler = remember(koyu, vurguIndex) {
        val temel = if (koyu) KoyuRenkler else AcikRenkler
        val secim = VurguSecenekleri[vurguIndex.coerceIn(VurguSecenekleri.indices)]
        temel.copy(vurgu = if (koyu) secim.second else secim.first)
    }
    val m3 = if (koyu) {
        darkColorScheme(
            primary = renkler.vurgu, background = renkler.arkaPlan, onBackground = renkler.etiket,
            surface = renkler.yuzey, onSurface = renkler.etiket, error = renkler.kirmizi,
        )
    } else {
        lightColorScheme(
            primary = renkler.vurgu, background = renkler.arkaPlan, onBackground = renkler.etiket,
            surface = renkler.yuzey, onSurface = renkler.etiket, error = renkler.kirmizi,
        )
    }
    CompositionLocalProvider(LocalEymenRenk provides renkler) {
        MaterialTheme(colorScheme = m3, content = icerik)
    }
}
