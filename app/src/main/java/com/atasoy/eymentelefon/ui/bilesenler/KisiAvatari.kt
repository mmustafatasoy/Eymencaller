package com.atasoy.eymentelefon.ui.bilesenler

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenGradyan
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun KisiAvatari(ad: String, modifier: Modifier = Modifier, boyut: Dp = EymenBoyut.avatarListe, fotoUri: Uri? = null) {
    val context = LocalContext.current
    val bitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, fotoUri) {
        value = if (fotoUri == null) null else withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(fotoUri)?.use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
            } catch (hata: Exception) {
                HataKaydedici.kaydet("KisiAvatari", hata)
                null
            }
        }
    }
    Box(modifier = modifier.size(boyut).clip(CircleShape).background(EymenGradyan.avatar(ad)), contentAlignment = Alignment.Center) {
        if (bitmap != null) {
            Image(bitmap = bitmap!!, contentDescription = ad, modifier = Modifier.matchParentSize(), contentScale = ContentScale.Crop)
        } else {
            Text(TurkceMetin.basHarfler(ad), color = EymenRenk.vurguUstu, style = EymenYazi.isim, textAlign = TextAlign.Center)
        }
    }
}

@Preview(name = "Koyu")
@Composable private fun KisiAvatariKoyu() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) { KisiAvatari("Ayşe Demir", boyut = EymenBoyut.avatarDetay) }
}
@Preview(name = "Açık")
@Composable private fun KisiAvatariAcik() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) { KisiAvatari("Mehmet", boyut = EymenBoyut.avatarFavori) }
}
