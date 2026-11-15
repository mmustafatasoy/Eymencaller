package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

@Composable
fun EymenAramaCubugu(
    deger: String,
    onDegerDegisti: (String) -> Unit,
    modifier: Modifier = Modifier,
    ipucu: String = stringResource(R.string.ara_ipucu),
    profilAdi: String? = null,
    onProfil: (() -> Unit)? = null,
) {
    var odak by remember { mutableStateOf(false) }
    val r = EymenRenk
    Row(
        modifier = modifier.fillMaxWidth().height(EymenBoyut.aramaCubugu)
            .background(r.yuzey1, EymenSekil.hap)
            .then(if (odak) Modifier.border(EymenBoyut.odakCercevesi, r.vurgu, EymenSekil.hap) else Modifier)
            .padding(horizontal = EymenBosluk.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s),
    ) {
        EymenIkonu(EymenIkon.Buyutec, aciklama = null, renk = r.metin3, boyut = EymenBoyut.ikonKucuk)
        BasicTextField(
            value = deger, onValueChange = onDegerDegisti, modifier = Modifier.weight(1f).onFocusChanged { odak = it.isFocused },
            singleLine = true, textStyle = EymenYazi.govde.copy(color = r.metin1), cursorBrush = SolidColor(r.vurgu),
            decorationBox = { inner ->
                Box {
                    if (deger.isEmpty()) Text(ipucu, color = r.metin3, style = EymenYazi.kucuk)
                    inner()
                }
            },
        )
        if (profilAdi != null) {
            // Avatar 30dp görünür, isteğe bağlı profil eyleminin hedefi en az 48dp.
            Box(
                modifier = Modifier.size(EymenBoyut.dokunmaMin)
                    .then(if (onProfil == null) Modifier else Modifier.clickable(onClick = onProfil)),
                contentAlignment = Alignment.Center,
            ) {
                KisiAvatari(ad = profilAdi, boyut = EymenBoyut.avatarProfil)
            }
        }
    }
}

@Preview(name = "Koyu")
@Composable private fun EymenAramaCubuguKoyu() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) { EymenAramaCubugu("", {}, profilAdi = "Muhammet Atasoy") }
}
@Preview(name = "Açık")
@Composable private fun EymenAramaCubuguAcik() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) { EymenAramaCubugu("Ay", {}, profilAdi = "Muhammet Atasoy") }
}
