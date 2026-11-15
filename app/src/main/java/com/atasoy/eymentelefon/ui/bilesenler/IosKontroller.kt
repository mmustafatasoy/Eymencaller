package com.atasoy.eymentelefon.ui.bilesenler

// iOS kontrolleri: arama alanı (pasif / aktif), segment, anahtar (switch), monogram avatar.

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.AvatarRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenDolgu
import com.atasoy.eymentelefon.ui.theme.EymenOpaklik
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Pasif arama alanı ("Ara") — dokununca arama ekranı açılır. */
@Composable
fun IosAramaAlani(ipucu: String, onTikla: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(EymenBoyut.aramaAlani)
            .clip(EymenSekil.aramaAlani)
            .background(EymenRenk.aramaAlani)
            .sessizTiklama(onTikla = onTikla)
            .padding(horizontal = EymenBosluk.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs + EymenBosluk.cokKucuk),
    ) {
        EymenIkonu(EymenIkon.Buyutec, null, EymenRenk.ikincilEtiket, boyut = EymenBoyut.ikonKucuk)
        Text(ipucu, style = EymenYazi.govde, color = EymenRenk.ikincilEtiket, maxLines = 1)
    }
}

/** Aktif arama alanı + "Vazgeç". Açılınca klavye gelir. */
@Composable
fun IosAramaAlaniAktif(
    metin: String,
    onMetin: (String) -> Unit,
    ipucu: String,
    vazgecMetni: String,
    onVazgec: () -> Unit,
    onAra: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val odak = remember { FocusRequester() }
    LaunchedEffect(Unit) { odak.requestFocus() }
    Row(
        modifier.fillMaxWidth().padding(start = EymenBosluk.kenar, end = EymenBosluk.s, top = EymenBosluk.s, bottom = EymenBosluk.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(EymenBoyut.aramaAlani)
                .clip(EymenSekil.aramaAlani)
                .background(EymenRenk.aramaAlani)
                .padding(horizontal = EymenBosluk.s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs + EymenBosluk.cokKucuk),
        ) {
            EymenIkonu(EymenIkon.Buyutec, null, EymenRenk.ikincilEtiket, boyut = EymenBoyut.ikonKucuk)
            Box(Modifier.weight(1f)) {
                if (metin.isEmpty()) Text(ipucu, style = EymenYazi.govde, color = EymenRenk.ikincilEtiket, maxLines = 1)
                BasicTextField(
                    value = metin,
                    onValueChange = onMetin,
                    singleLine = true,
                    textStyle = EymenYazi.govde.copy(color = EymenRenk.etiket),
                    cursorBrush = SolidColor(EymenRenk.vurgu),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onAra() }),
                    modifier = Modifier.fillMaxWidth().focusRequester(odak),
                )
            }
            if (metin.isNotEmpty()) {
                Box(
                    Modifier.size(EymenBoyut.ikonKucuk).clip(CircleShape).background(EymenRenk.ikincilEtiket).sessizTiklama { onMetin("") },
                    contentAlignment = Alignment.Center,
                ) {
                    EymenIkonu(EymenIkon.KapatX, null, EymenRenk.yuzey, boyut = EymenBoyut.ikonMini - EymenBosluk.cokKucuk)
                }
            }
        }
        IosMetinButonu(vazgecMetni, onVazgec)
    }
}

/** iOS segment kontrolü (Tümü | Cevapsız, Sistem | Açık | Koyu). */
@Composable
fun IosSegment(secenekler: List<String>, secili: Int, onSec: (Int) -> Unit, modifier: Modifier = Modifier, kucuk: Boolean = false) {
    val r = EymenRenk
    Row(
        modifier
            .clip(EymenSekil.segmentDis)
            .background(r.aramaAlani)
            .padding(EymenBosluk.cokKucuk),
    ) {
        secenekler.forEachIndexed { i, metin ->
            val aktif = i == secili
            Text(
                metin,
                style = (if (kucuk) EymenYazi.etiket1 else EymenYazi.dipnot).copy(
                    fontWeight = if (aktif) EymenYazi.vurguluGovde.fontWeight else EymenYazi.sekme.fontWeight,
                ),
                color = r.etiket,
                maxLines = 1,
                modifier = Modifier
                    .then(if (aktif) Modifier.shadow(EymenBosluk.cokKucuk, EymenSekil.segmentIc) else Modifier)
                    .clip(EymenSekil.segmentIc)
                    .background(if (aktif) r.segmentSecili else r.aramaAlani.copy(alpha = 0f))
                    .sessizTiklama { onSec(i) }
                    .padding(if (kucuk) EymenDolgu.segmentKucuk else EymenDolgu.segment),
            )
        }
    }
}

/** iOS anahtarı (51×31, açıkken yeşil). */
@Composable
fun IosAnahtar(acik: Boolean, onDegis: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val r = EymenRenk
    val bosluk = (EymenBoyut.anahtarY - EymenBoyut.anahtarTop) / 2
    val x by animateDpAsState(if (acik) EymenBoyut.anahtarG - EymenBoyut.anahtarTop - bosluk * 2 else Dp(0f), label = "anahtar")
    val zemin by animateColorAsState(if (acik) r.yesil else r.anahtarKapali, label = "anahtarZemin")
    Box(
        modifier = modifier
            .size(EymenBoyut.anahtarG, EymenBoyut.anahtarY)
            .clip(CircleShape)
            .background(zemin)
            .toggleable(value = acik, role = Role.Switch, onValueChange = onDegis)
            .padding(bosluk),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset(x = x)
                .shadow(EymenBosluk.xs, CircleShape)
                .size(EymenBoyut.anahtarTop)
                .clip(CircleShape)
                .background(AramaEkraniRenk.beyaz),
        )
    }
}

/** iOS monogram avatarı (gri gradyan + beyaz baş harfler); fotoğraf varsa fotoğraf. */
@Composable
fun IosAvatar(basHarfler: String, boyut: Dp, modifier: Modifier = Modifier, fotoUri: Uri? = null) {
    val context = LocalContext.current
    val foto by produceState<ImageBitmap?>(initialValue = null, fotoUri) {
        value = fotoUri?.let { uri ->
            withContext(Dispatchers.IO) {
                runCatching { context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it)?.asImageBitmap() } }.getOrNull()
            }
        }
    }
    val yazi = with(LocalDensity.current) { (boyut * HARF_ORANI).toSp() }
    Box(
        // V13-03: monogram ada göre renkli; "#" ve simge gri kalır
        modifier.size(boyut).clip(CircleShape).background(
            if (basHarfler.any { it.isLetter() }) AvatarRenk.renkli(basHarfler) else AvatarRenk.firca,
        ),
        contentAlignment = Alignment.Center,
    ) {
        val f = foto
        if (f != null) {
            Image(f, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else if (basHarfler == "#" || basHarfler.isBlank()) {
            EymenIkonu(EymenIkon.KisiDolu, null, AvatarRenk.metin.copy(alpha = 1f - EymenOpaklik.pasif), boyut = boyut * IKON_ORANI)
        } else {
            Text(basHarfler, style = EymenYazi.monogram.copy(fontSize = yazi), color = AvatarRenk.metin, maxLines = 1)
        }
    }
}

private const val HARF_ORANI = 0.4f
private const val IKON_ORANI = 0.62f
