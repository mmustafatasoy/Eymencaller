package com.atasoy.eymentelefon.ui.bilesenler

// iOS uyarısı (ortada 270dp kart, butonlar ince çizgiyle ayrılır) ve eylem sayfası (alttan seçenekler + Vazgeç).

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenDolgu
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenYazi

data class UyariButonu(val metin: String, val onTikla: () -> Unit, val kalin: Boolean = false, val yikici: Boolean = false)

/** 2 buton yan yana, 3+ buton alt alta (iOS gibi). [ek] metin alanı vb. için yuva. */
@Composable
fun IosUyari(
    baslik: String,
    mesaj: String?,
    butonlar: List<UyariButonu>,
    onKapat: () -> Unit,
    ek: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val r = EymenRenk
    Dialog(onDismissRequest = onKapat, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        PencereBulanikligi()
        Column(
            // V13-06: cam uyarı kutusu
            Modifier.width(EymenBoyut.uyariGenislik).clip(EymenSekil.uyari).background(r.yuzey.copy(alpha = CAM_YUZEY)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.padding(EymenDolgu.uyariMetni), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(baslik, style = EymenYazi.vurguluGovde, color = r.etiket, textAlign = TextAlign.Center)
                if (mesaj != null) {
                    Text(mesaj, style = EymenYazi.dipnot, color = r.etiket, textAlign = TextAlign.Center, modifier = Modifier.padding(top = EymenBosluk.cokKucuk))
                }
                if (ek != null) {
                    Column(Modifier.padding(top = EymenBosluk.m).fillMaxWidth(), content = ek)
                }
            }
            HorizontalDivider(thickness = EymenBoyut.hairline, color = r.ayirici)
            if (butonlar.size == 2) {
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    UyariButonMetni(butonlar[0], Modifier.weight(1f))
                    VerticalDivider(Modifier.fillMaxHeight(), EymenBoyut.hairline, r.ayirici)
                    UyariButonMetni(butonlar[1], Modifier.weight(1f))
                }
            } else {
                butonlar.forEachIndexed { i, b ->
                    if (i > 0) HorizontalDivider(thickness = EymenBoyut.hairline, color = r.ayirici)
                    UyariButonMetni(b, Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun UyariButonMetni(b: UyariButonu, modifier: Modifier) {
    val r = EymenRenk
    Text(
        b.metin,
        style = if (b.kalin) EymenYazi.vurguluGovde else EymenYazi.govde,
        color = if (b.yikici) r.kirmizi else r.vurgu,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = modifier.sessizTiklama(onTikla = b.onTikla).padding(EymenDolgu.uyariButonu),
    )
}

data class SayfaSecenegi(val metin: String, val onTikla: () -> Unit, val yikici: Boolean = false)

/** iOS eylem sayfası: başlık + seçenekler kutusu, altında ayrı "Vazgeç". */
@Composable
fun IosEylemSayfasi(
    baslik: String?,
    secenekler: List<SayfaSecenegi>,
    vazgecMetni: String,
    onKapat: () -> Unit,
) {
    val r = EymenRenk
    Dialog(onDismissRequest = onKapat, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        PencereBulanikligi()
        Box(Modifier.fillMaxSize().sessizTiklama(onTikla = onKapat), contentAlignment = Alignment.BottomCenter) {
            Column(
                Modifier.fillMaxWidth().padding(EymenBosluk.s).sessizTiklama { },
                verticalArrangement = Arrangement.spacedBy(EymenBosluk.s),
            ) {
                Column(Modifier.fillMaxWidth().clip(EymenSekil.sayfa).background(r.yuzey.copy(alpha = CAM_YUZEY))) {
                    if (baslik != null) {
                        Text(
                            baslik,
                            style = EymenYazi.dipnot,
                            color = r.ikincilEtiket,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(EymenBosluk.m + EymenBosluk.cokKucuk),
                        )
                        HorizontalDivider(thickness = EymenBoyut.hairline, color = r.ayirici)
                    }
                    secenekler.forEachIndexed { i, s ->
                        if (i > 0) HorizontalDivider(thickness = EymenBoyut.hairline, color = r.ayirici)
                        Text(
                            s.metin,
                            style = EymenYazi.baslik3.copy(fontWeight = EymenYazi.govde.fontWeight),
                            color = if (s.yikici) r.kirmizi else r.vurgu,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth().sessizTiklama { s.onTikla() }.padding(EymenDolgu.sayfaSecenegi),
                        )
                    }
                }
                Text(
                    vazgecMetni,
                    style = EymenYazi.baslik3,
                    color = r.vurgu,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().clip(EymenSekil.sayfa).background(r.yuzey).sessizTiklama(onTikla = onKapat).padding(EymenDolgu.sayfaSecenegi),
                )
            }
        }
    }
}

private const val CAM_YUZEY = 0.86f
