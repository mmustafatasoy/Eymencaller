package com.atasoy.eymentelefon.ui.ekranlar.karsilama

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.ui.bilesenler.IosButon
import com.atasoy.eymentelefon.ui.bilesenler.IosSayfa
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi

/** 01 · Karşılama (iOS "Hoş Geldiniz" sayfası). Sadece ilk açılışta. */
@Composable
fun KarsilamaEkrani(onBasla: () -> Unit) {
    val r = EymenRenk
    IosSayfa {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = EymenBosluk.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(EymenBosluk.dev))
            UygulamaSimgesi(EymenBoyut.karsilamaSimge)
            Spacer(Modifier.height(EymenBosluk.l))
            Text(stringResource(R.string.karsilama_baslik), style = EymenYazi.buyukBaslik, color = r.etiket, textAlign = TextAlign.Center)
            Spacer(Modifier.height(EymenBosluk.xxl + EymenBosluk.xs))
            Ozellik(EymenIkon.Telefon, R.string.ozellik1_baslik, R.string.ozellik1_metin)
            Ozellik(EymenIkon.KisiDolu, R.string.ozellik2_baslik, R.string.ozellik2_metin)
            Ozellik(EymenIkon.Kalkan, R.string.ozellik3_baslik, R.string.ozellik3_metin)
        }
        Column(Modifier.padding(horizontal = EymenBosluk.kenar, vertical = EymenBosluk.kenar)) {
            Text(
                stringResource(R.string.karsilama_gizlilik),
                style = EymenYazi.dipnot,
                color = r.ikincilEtiket,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = EymenBosluk.m + EymenBosluk.cokKucuk),
            )
            IosButon(stringResource(R.string.devam), onBasla)
        }
    }
}

@Composable
private fun Ozellik(ikon: EymenIkon, baslik: Int, metin: Int) {
    Row(Modifier.fillMaxWidth().padding(bottom = EymenBosluk.xl), horizontalArrangement = Arrangement.spacedBy(EymenBosluk.kenar)) {
        EymenIkonu(ikon, null, EymenRenk.vurgu, boyut = EymenBoyut.ozellikIkonu)
        Column {
            Text(stringResource(baslik), style = EymenYazi.vurguluGovde, color = EymenRenk.etiket)
            Text(stringResource(metin), style = EymenYazi.altBaslik, color = EymenRenk.ikincilEtiket)
        }
    }
}

/** Uyarlanabilir simgeyi katmanlarından çizer (painterResource adaptive icon desteklemez). */
@Composable
fun UygulamaSimgesi(boyut: Dp, modifier: Modifier = Modifier) {
    Box(modifier.size(boyut).clip(RoundedCornerShape(percent = SIMGE_KOSE)), contentAlignment = Alignment.Center) {
        Image(painterResource(R.drawable.ic_launcher_background), null, Modifier.requiredSize(boyut * KATMAN), contentScale = ContentScale.FillBounds)
        Image(painterResource(R.drawable.ic_launcher_foreground), null, Modifier.requiredSize(boyut * KATMAN), contentScale = ContentScale.FillBounds)
    }
}

private const val SIMGE_KOSE = 23
private const val KATMAN = 1.5f
