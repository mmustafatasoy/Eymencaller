package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

@Composable
fun HarfDizini(onHarf: (String) -> Unit, modifier: Modifier = Modifier) {
    var boyut by remember { mutableStateOf(IntSize.Zero) }
    val harfler = TurkceMetin.DIZIN
    fun harfeGit(y: Float) {
        if (boyut.height > 0) {
            val index = ((y / boyut.height) * harfler.size).toInt().coerceIn(harfler.indices)
            onHarf(harfler[index])
        }
    }
    Column(modifier = modifier.onSizeChanged { boyut = it }
        .pointerInput(harfler, boyut) {
            detectDragGestures(onDragStart = { harfeGit(it.y) }, onDrag = { change, _ -> harfeGit(change.position.y); change.consume() })
        }, horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        harfler.forEach { harf ->
            Text(harf, style = EymenYazi.harfDizini, color = EymenRenk.metin3,
                modifier = Modifier.clickable { onHarf(harf) })
        }
    }
}

@Preview(name = "Koyu", heightDp = 550)
@Composable private fun HarfDiziniKoyu() { EymenTelefonTemasi(tema = TemaSecimi.KOYU) { HarfDizini({}) } }
@Preview(name = "Açık", heightDp = 550)
@Composable private fun HarfDiziniAcik() { EymenTelefonTemasi(tema = TemaSecimi.ACIK) { HarfDizini({}) } }
