package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
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
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

@Composable
fun AramaKontrolButonu(ikon: EymenIkon, metin: String, aktif: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(EymenBosluk.s)) {
        Box(Modifier.size(EymenBoyut.kontrolButonu)
            .background(if (aktif) AramaEkraniRenk.beyaz else AramaEkraniRenk.kontrolZemini, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick), contentAlignment = Alignment.Center) {
            EymenIkonu(ikon, aciklama = metin, renk = if (aktif) EymenRenk.aramaZemini else AramaEkraniRenk.beyaz, boyut = EymenBoyut.ikonBuyuk)
        }
        Text(metin, style = EymenYazi.kucuk, color = AramaEkraniRenk.beyaz)
    }
}

@Preview(name = "Koyu")
@Composable private fun AramaKontrolButonuKoyu() { EymenTelefonTemasi(tema = TemaSecimi.KOYU) { AramaKontrolButonu(EymenIkon.Sessiz, stringResource(R.string.kontrol_sessiz), false, {}) } }
@Preview(name = "Açık")
@Composable private fun AramaKontrolButonuAcik() { EymenTelefonTemasi(tema = TemaSecimi.ACIK) { AramaKontrolButonu(EymenIkon.Hoparlor, stringResource(R.string.kontrol_hoparlor), true, {}) } }
