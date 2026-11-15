package com.atasoy.eymentelefon.ui.bilesenler

// V13 · iOS "Liquid Glass" cam yüzeyler (Haze) + yaylı basma (docs/08_V13_PLANI.md)
// Kaynak (arkadaki içerik) camKaynak() ile işaretlenir; cam yüzey camYuzey() ile arkasını bulanık gösterir.
// Android 12 altında bulanıklık yok: yarı saydam ton (fallback) çizilir.

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

/** Ekranın cam kaynağı (null → cam yok, düz ton). */
val LocalCamDurumu = staticCompositionLocalOf<HazeState?> { null }

/** Fotoğraftan çıkan baskın renk; cam yüzeyler bu renge hafifçe bürünür. */
val LocalCamTonu = compositionLocalOf { Color.Unspecified }

/** Ana ekranda listelerin sonuna eklenecek boşluk (cam sekme çubuğunun altına kayan içerik görünür kalsın). */
val LocalAltCamBoslugu = compositionLocalOf { EymenBoyut.sifir }

@Composable
fun AltCamBoslugu() {
    Spacer(Modifier.height(LocalAltCamBoslugu.current))
}

fun Modifier.camKaynak(durum: HazeState?): Modifier = if (durum == null) this else this.hazeSource(durum)

/**
 * Buzlu cam yüzey: arkası bulanık + ton + ince ışık kenarı.
 * @param ton camın rengi (saydamlığı içinde)
 */
fun Modifier.camYuzey(
    sekil: Shape,
    ton: Color,
    bulaniklik: Dp = EymenBoyut.camBulanik,
    kenar: Boolean = true,
): Modifier = composed {
    val durum = LocalCamDurumu.current
    val vurgu = LocalCamTonu.current
    val renk = if (vurgu != Color.Unspecified) lerpRenk(ton, vurgu.copy(alpha = ton.alpha), TON_KARISIM) else ton
    var m = this.clip(sekil)
    m = if (durum != null) {
        m.hazeEffect(state = durum) {
            blurRadius = bulaniklik
            tints = listOf(HazeTint(renk))
            noiseFactor = GURULTU
            fallbackTint = HazeTint(renk.copy(alpha = (renk.alpha + YEDEK_EK).coerceAtMost(1f)))
        }
    } else {
        m.drawBehind { drawRect(renk) }
    }
    if (kenar) {
        m = m.border(
            EymenBoyut.hairline,
            Brush.verticalGradient(listOf(AramaEkraniRenk.beyaz.copy(alpha = KENAR_UST), AramaEkraniRenk.beyaz.copy(alpha = KENAR_ALT))),
            sekil,
        )
    }
    m
}

private fun lerpRenk(a: Color, b: Color, t: Float): Color = lerp(a, b, t)

/** V13-06: Dialog penceresinin arkasını (Android 12+) gerçek bulanıklaştırır. Dialog içeriğinin başında çağrılır. */
@Composable
fun PencereBulanikligi(karartma: Float = PENCERE_KARARTMA) {
    val pencere = (androidx.compose.ui.platform.LocalView.current.parent as? androidx.compose.ui.window.DialogWindowProvider)?.window ?: return
    val yaricap = with(androidx.compose.ui.platform.LocalDensity.current) { EymenBoyut.camBulanik.roundToPx() }
    androidx.compose.runtime.SideEffect {
        pencere.setDimAmount(karartma)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pencere.addFlags(android.view.WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            pencere.attributes = pencere.attributes.apply { blurBehindRadius = yaricap }
        }
    }
}

/** iOS gibi yaylı basma: basılıyken küçülür, bırakınca yayla geri gelir; basışta hafif titreşim. */
fun Modifier.yayliBasma(etkilesim: InteractionSource, titresim: Boolean = true): Modifier = composed {
    val basili by etkilesim.collectIsPressedAsState()
    val olcek by animateFloatAsState(
        if (basili) BASILI_OLCEK else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "yay",
    )
    val dokunus = LocalHapticFeedback.current
    LaunchedEffect(basili) { if (basili && titresim) dokunus.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
    graphicsLayer {
        scaleX = olcek
        scaleY = olcek
    }
}

private const val BASILI_OLCEK = 0.9f
private const val PENCERE_KARARTMA = 0.25f
private const val GURULTU = 0.06f
private const val YEDEK_EK = 0.35f
private const val KENAR_UST = 0.45f
private const val KENAR_ALT = 0.08f
private const val TON_KARISIM = 0.35f
