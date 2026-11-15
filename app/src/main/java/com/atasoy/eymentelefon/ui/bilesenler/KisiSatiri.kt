package com.atasoy.eymentelefon.ui.bilesenler

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

@Composable
fun KisiSatiri(ad: String, numara: String, onClick: () -> Unit, modifier: Modifier = Modifier, fotoUri: Uri? = null) {
    Row(modifier = modifier.fillMaxWidth().heightIn(min = EymenBoyut.dokunmaMin).clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m)) {
        KisiAvatari(ad, boyut = EymenBoyut.avatarListe, fotoUri = fotoUri)
        Column(verticalArrangement = Arrangement.spacedBy(EymenBosluk.xs)) {
            Text(ad, color = EymenRenk.metin1, style = EymenYazi.isim, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(numara, color = EymenRenk.metin3, style = EymenYazi.kucuk, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Preview(name = "Koyu")
@Composable private fun KisiSatiriKoyu() { EymenTelefonTemasi(tema = TemaSecimi.KOYU) { KisiSatiri("Ayşe Demir", "0533 987 65 43", {}) } }
@Preview(name = "Açık")
@Composable private fun KisiSatiriAcik() { EymenTelefonTemasi(tema = TemaSecimi.ACIK) { KisiSatiri("Ahmet Yılmaz", "0532 123 45 67", {}) } }
