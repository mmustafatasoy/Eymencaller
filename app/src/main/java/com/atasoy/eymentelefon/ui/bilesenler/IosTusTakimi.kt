package com.atasoy.eymentelefon.ui.bilesenler

// iOS tuş takımı: 78dp yuvarlak tuşlar, rakam + harfler. Basınca renk koyulaşır (iOS gibi), titreşim/ses ekran verir.

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.atasoy.eymentelefon.cekirdek.yardimci.T9Eslestirici
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi

private const val BASMA_MS = 120

@Composable
fun IosTusTakimi(
    onTus: (Char) -> Unit,
    onUzunBas: (Char) -> Unit,
    modifier: Modifier = Modifier,
    zemin: Color = EymenRenk.tusZemini,
    basili: Color = EymenRenk.tusBasili,
    yazi: Color = EymenRenk.etiket,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(EymenBosluk.tusDikey), horizontalAlignment = Alignment.CenterHorizontally) {
        T9Eslestirici.TUSLAR.chunked(3).forEach { satir ->
            Row(horizontalArrangement = Arrangement.spacedBy(EymenBosluk.tusYatay)) {
                satir.forEach { (rakam, harfler) ->
                    Tus(rakam, harfler, zemin, basili, yazi, { onTus(rakam) }, { onUzunBas(rakam) })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Tus(rakam: Char, harfler: String, zemin: Color, basiliRenk: Color, yazi: Color, onTikla: () -> Unit, onUzun: () -> Unit) {
    val etkilesim = remember { MutableInteractionSource() }
    val basili by etkilesim.collectIsPressedAsState()
    val renk by animateColorAsState(if (basili) basiliRenk else zemin, tween(BASMA_MS), label = "tus")
    Column(
        modifier = Modifier
            .yayliBasma(etkilesim, titresim = false)
            .size(EymenBoyut.tus)
            .clip(CircleShape)
            .background(renk)
            .combinedClickable(interactionSource = etkilesim, indication = null, onClick = onTikla, onLongClick = onUzun)
            .semantics { contentDescription = rakam.toString() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(rakam.toString(), style = EymenYazi.tusRakam, color = yazi, maxLines = 1)
        Text(harfler, style = EymenYazi.tusHarf, color = yazi, maxLines = 1)
    }
}
