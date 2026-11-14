package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenGradyan
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi

@Composable
fun KapatButonu(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val renk = EymenRenk
    Box(modifier = modifier.size(EymenBoyut.kapatButonu).background(EymenGradyan.kapat(renk), CircleShape).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        EymenIkonu(EymenIkon.Telefon, aciklama = stringResource(R.string.kontrol_kapat), renk = AramaEkraniRenk.beyaz, boyut = EymenBoyut.ikonBuyuk, kapatma = true)
    }
}

@Preview(name = "Koyu")
@Composable private fun KapatButonuKoyu() { EymenTelefonTemasi(tema = TemaSecimi.KOYU) { KapatButonu({}) } }
@Preview(name = "Açık")
@Composable private fun KapatButonuAcik() { EymenTelefonTemasi(tema = TemaSecimi.ACIK) { KapatButonu({}) } }
