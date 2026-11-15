package com.atasoy.eymentelefon.ui.ekranlar.karsilama

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.bilesenler.EymenButon
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenGradyan
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

@Composable
fun KarsilamaEkrani(onBasla: () -> Unit) {
    KarsilamaIcerik(onBasla = onBasla)
}

@Composable
fun KarsilamaIcerik(onBasla: () -> Unit) {
    val renk = EymenRenk
    Column(
        modifier = Modifier.fillMaxSize()
            .background(renk.arkaPlan)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(renk.vurgu.copy(alpha = 0.17f), renk.vurgu.copy(alpha = 0f)),
                        center = Offset(size.width * 0.20f, size.height * 0.33f),
                        radius = size.width * 0.9f,
                    ),
                )
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(renk.vurgu2.copy(alpha = 0.17f), renk.vurgu2.copy(alpha = 0f)),
                        center = Offset(size.width * 0.85f, size.height * 0.72f),
                        radius = size.width,
                    ),
                )
            }
            .padding(horizontal = EymenBosluk.kenar, vertical = EymenBosluk.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.size(EymenBoyut.karsilamaSimge)
                .clip(EymenSekil.kart)
                .background(EymenGradyan.vurgu(renk)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
        Spacer(Modifier.height(EymenBosluk.xxl))
        Text(
            text = stringResource(R.string.uygulama_adi),
            style = EymenYazi.girisBaslik,
            color = renk.metin1,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(EymenBosluk.xl))
        Text(
            text = stringResource(R.string.karsilama_aciklama),
            style = EymenYazi.govde,
            color = renk.metin2,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(EymenBosluk.xxl))
        Row(
            horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(EymenBosluk.xxl, EymenBosluk.s).clip(CircleShape).background(renk.vurgu))
            Box(Modifier.size(EymenBosluk.s).clip(CircleShape).background(renk.yuzey3))
            Box(Modifier.size(EymenBosluk.s).clip(CircleShape).background(renk.yuzey3))
        }
        Spacer(Modifier.weight(1f))
        EymenButon(stringResource(R.string.karsilama_basla), onClick = onBasla)
        Spacer(Modifier.height(EymenBosluk.l))
        Text(
            text = stringResource(R.string.karsilama_gizlilik),
            style = EymenYazi.kucuk,
            color = renk.metin3,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "Koyu", showBackground = true)
@Composable private fun KarsilamaKoyu() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) { KarsilamaIcerik(onBasla = {}) }
}

@Preview(name = "Acik", showBackground = true)
@Composable private fun KarsilamaAcik() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) { KarsilamaIcerik(onBasla = {}) }
}
