package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.yardimci.T9Eslestirici
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

@Composable
fun TusTakimi(onTus: (Char) -> Unit, onUzunBas: (Char) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(EymenBosluk.tusDikey)) {
        T9Eslestirici.TUSLAR.chunked(3).forEach { satir ->
            Row(horizontalArrangement = Arrangement.spacedBy(EymenBosluk.tusYatay)) {
                satir.forEach { (rakam, harfler) -> TusTusu(rakam, harfler, onTus, onUzunBas) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TusTusu(rakam: Char, harfler: String, onTus: (Char) -> Unit, onUzunBas: (Char) -> Unit) {
    val etkilesim = remember { MutableInteractionSource() }
    val basili by etkilesim.collectIsPressedAsState()
    val olcek by animateFloatAsState(if (basili) .94f else 1f, tween(90), label = "tus")
    Column(
        modifier = Modifier.size(EymenBoyut.tusGenislik, EymenBoyut.tusYukseklik)
            .scale(olcek).clip(EymenSekil.hap).background(EymenRenk.yuzey1)
            .combinedClickable(interactionSource = etkilesim, indication = null,
                onClick = { onTus(rakam) }, onLongClick = { onUzunBas(rakam) }),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(rakam.toString(), color = EymenRenk.metin1, style = EymenYazi.tus, maxLines = 1)
        if (harfler.isNotEmpty()) Text(harfler, color = EymenRenk.metin3, style = EymenYazi.tusHarf, maxLines = 1)
    }
}

@Preview(name = "Koyu")
@Composable private fun TusTakimiKoyu() { EymenTelefonTemasi(tema = TemaSecimi.KOYU) { TusTakimi({}, {}) } }
@Preview(name = "Açık")
@Composable private fun TusTakimiAcik() { EymenTelefonTemasi(tema = TemaSecimi.ACIK) { TusTakimi({}, {}) } }
