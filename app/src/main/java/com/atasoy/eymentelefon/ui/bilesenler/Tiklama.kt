package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/** Dokun + uzun bas. */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.uzunBasilabilir(onTikla: () -> Unit, onUzunBas: (() -> Unit)?): Modifier =
    combinedClickable(onClick = onTikla, onLongClick = onUzunBas)

/** Dalga efekti olmadan dokunma (iOS metin butonları gibi). */
fun Modifier.sessizTiklama(etkin: Boolean = true, onTikla: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = etkin,
        onClick = onTikla,
    )
}
