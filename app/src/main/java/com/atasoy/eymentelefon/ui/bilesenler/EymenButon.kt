package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenGradyan
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

enum class EymenButonTuru { BIRINCIL, IKINCIL }

@Composable
fun EymenButon(
    metin: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    etkin: Boolean = true,
    tur: EymenButonTuru = EymenButonTuru.BIRINCIL,
) {
    val renk = EymenRenk
    val sekil = EymenSekil.buton
    val zemin = if (tur == EymenButonTuru.BIRINCIL) EymenGradyan.vurgu(renk) else Brush.linearGradient(listOf(renk.arkaPlan, renk.arkaPlan))
    Box(
        modifier = modifier.fillMaxWidth().height(EymenBoyut.anaButon)
            .alpha(if (etkin) 1f else 0.45f)
            .clip(sekil).background(zemin)
            .then(if (etkin) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = metin,
            color = if (tur == EymenButonTuru.BIRINCIL) renk.vurguUstu else renk.vurgu,
            style = EymenYazi.buton,
        )
    }
}

@Preview(name = "Koyu", showBackground = true)
@Composable private fun EymenButonKoyu() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        EymenButon(stringResource(R.string.karsilama_basla), onClick = {})
    }
}

@Preview(name = "Açık", showBackground = true)
@Composable private fun EymenButonAcik() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        EymenButon(stringResource(R.string.izin_devam), onClick = {}, etkin = false)
    }
}
