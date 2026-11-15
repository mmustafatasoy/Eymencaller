package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AltSayfa(onKapat: () -> Unit, modifier: Modifier = Modifier, icerik: @Composable () -> Unit) {
    ModalBottomSheet(onDismissRequest = onKapat, modifier = modifier, shape = EymenSekil.altSayfa,
        containerColor = EymenRenk.yuzey1,
        dragHandle = { AltSayfaTutamac() },
    ) { icerik() }
}

@Composable
private fun AltSayfaTutamac() {
    Box(Modifier.fillMaxWidth().padding(vertical = EymenBosluk.m), contentAlignment = Alignment.Center) {
        Box(Modifier.size(EymenBoyut.altSayfaTutamacG, EymenBoyut.altSayfaTutamacY).background(EymenRenk.yuzey3, CircleShape))
    }
}

@Preview(name = "Koyu")
@Composable private fun AltSayfaKoyu() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        Column(Modifier.fillMaxWidth().background(EymenRenk.yuzey1, EymenSekil.altSayfa), horizontalAlignment = Alignment.CenterHorizontally) {
            AltSayfaTutamac(); Text(stringResource(R.string.eksik_baslik), color = EymenRenk.metin1, style = EymenYazi.baslik2)
        }
    }
}
@Preview(name = "Açık")
@Composable private fun AltSayfaAcik() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        Column(Modifier.fillMaxWidth().background(EymenRenk.yuzey1, EymenSekil.altSayfa), horizontalAlignment = Alignment.CenterHorizontally) {
            AltSayfaTutamac(); Text(stringResource(R.string.eksik_baslik), color = EymenRenk.metin1, style = EymenYazi.baslik2)
        }
    }
}
