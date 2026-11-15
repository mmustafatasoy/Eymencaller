package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi

@Composable
fun EymenAnahtar(acik: Boolean, onDegisti: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val r = EymenRenk
    Box(modifier = modifier.size(EymenBoyut.dokunmaMin).clickable { onDegisti(!acik) }, contentAlignment = Alignment.Center) {
        Box(Modifier.size(EymenBoyut.anahtarGenislik, EymenBoyut.anahtarYukseklik).background(if (acik) r.vurgu else r.yuzey3, CircleShape), contentAlignment = Alignment.CenterStart) {
            Box(Modifier.offset(x = if (acik) EymenBoyut.anahtarGenislik - EymenBoyut.anahtarYukseklik else EymenBosluk.xs)
                .size(EymenBoyut.anahtarYukseklik - EymenBosluk.xs).background(AramaEkraniRenk.beyaz, CircleShape))
        }
    }
}

@Preview(name = "Koyu")
@Composable private fun EymenAnahtarKoyu() { EymenTelefonTemasi(tema = TemaSecimi.KOYU) { EymenAnahtar(true, {}) } }
@Preview(name = "Açık")
@Composable private fun EymenAnahtarAcik() { EymenTelefonTemasi(tema = TemaSecimi.ACIK) { EymenAnahtar(false, {}) } }
