package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

enum class EymenCipTuru { DURUM, FILTRE, ETIKET }

@Composable
fun EymenCip(
    metin: String,
    modifier: Modifier = Modifier,
    tur: EymenCipTuru = EymenCipTuru.DURUM,
    secili: Boolean = false,
    olumlu: Boolean = false,
    uyari: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val renk = EymenRenk
    val zemin = when {
        olumlu -> renk.vurgu.copy(alpha = .16f)
        uyari -> renk.cevapsiz.copy(alpha = .16f)
        secili -> renk.metin1
        else -> renk.yuzey2
    }
    val metinRengi = when {
        olumlu -> renk.vurgu
        uyari -> renk.cevapsiz
        secili -> renk.arkaPlan
        else -> renk.metin2
    }
    val bicim = if (tur == EymenCipTuru.ETIKET) EymenSekil.etiket else EymenSekil.cip
    Box(
        modifier = modifier.clip(bicim).background(zemin)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = EymenBosluk.m, vertical = EymenBosluk.s),
        contentAlignment = Alignment.Center,
    ) {
        Text(metin, color = metinRengi, style = if (tur == EymenCipTuru.ETIKET) EymenYazi.etiket else EymenYazi.cip)
    }
}

@Preview(name = "Koyu")
@Composable private fun EymenCipKoyu() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) { EymenCip(stringResource(R.string.izin_verildi), olumlu = true) }
}
@Preview(name = "Açık")
@Composable private fun EymenCipAcik() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) { EymenCip(stringResource(R.string.filtre_tumu), tur = EymenCipTuru.FILTRE, secili = true) }
}
