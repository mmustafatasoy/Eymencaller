package com.atasoy.eymentelefon.ui.bilesenler

// V13-08 · Ortak öğe geçişi: listedeki avatar kişi kartına büyüyerek uçar (docs/08_V13_PLANI.md)

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalPaylasimKapsami = staticCompositionLocalOf<SharedTransitionScope?> { null }

val LocalGecisKapsami = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

/** Aynı kişi kimliğine sahip avatarlar ekranlar arası tek parça gibi hareket eder. */
@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.paylasilanAvatar(kisiId: Long?): Modifier = composed {
    val paylasim = LocalPaylasimKapsami.current
    val gecis = LocalGecisKapsami.current
    if (kisiId == null || kisiId <= 0 || paylasim == null || gecis == null) {
        Modifier
    } else {
        with(paylasim) { Modifier.sharedElement(rememberSharedContentState("avatar-$kisiId"), gecis) }
    }
}
