package com.atasoy.eymentelefon.ui.bilesenler

// ✅ v1.1 GÜVENLİ ALAN (kamera deliği + durum çubuğu + gezinme tuşları + klavye) — cihaza göre OTOMATİK.
// Kural: Hiçbir ekran statusBarsPadding / navigationBarsPadding kullanmaz; hepsi bu dosyadan geçer.
// WindowInsets.safeDrawing = durum çubuğu ∪ kamera kesiği (displayCutout) ∪ gezinme çubuğu ∪ klavye.
// windowInsetsPadding iç içe kullanıldığında tüketilen kısım ikinci kez uygulanmaz (çift boşluk olmaz).

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import com.atasoy.eymentelefon.ui.theme.EymenRenk

/** Üst (durum çubuğu + kamera) ve yanlar (yatayda kamera / kenar kesiği). */
fun Modifier.ustGuvenli(): Modifier = composed {
    windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
}

/** Alt (gezinme tuşları / hareket çubuğu + açıksa klavye). */
fun Modifier.altGuvenli(): Modifier = composed {
    windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
}

/** Sadece yanlar (sekme çubuğu gibi zemini alta kadar uzanan öğeler için). */
fun Modifier.yatayGuvenli(): Modifier = composed {
    windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
}

/**
 * Tüm ekranların kökü. Zemin rengi kenardan kenara (kameranın arkası ve gezinme çubuğu dahil) boyanır,
 * içerik güvenli alanın içinde kalır.
 * @param altDahil false → alt boşluğu sekme çubuğu verir (ana sekmeler).
 */
@Composable
fun IosSayfa(
    modifier: Modifier = Modifier,
    zemin: Color = EymenRenk.arkaPlan,
    altDahil: Boolean = true,
    icerik: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(zemin)
            .ustGuvenli()
            .then(if (altDahil) Modifier.altGuvenli() else Modifier),
        content = icerik,
    )
}
