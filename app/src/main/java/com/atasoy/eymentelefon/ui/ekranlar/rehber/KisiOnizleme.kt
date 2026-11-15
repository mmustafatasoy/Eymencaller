package com.atasoy.eymentelefon.ui.ekranlar.rehber

// V12-09 · iOS gibi uzun basma önizlemesi (docs/07_V12_PLANI.md, görsel v12_06)
// Bulanık arka plan + büyüyerek açılan posterli kart + bağlam menüsü. Kişiler ve Favoriler kullanır.

import android.graphics.BitmapFactory
import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.DisIslemler
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.gorusme.ui.PosterFotografi
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.Ayirici
import com.atasoy.eymentelefon.ui.bilesenler.IosAvatar
import com.atasoy.eymentelefon.ui.bilesenler.IosEylemSayfasi
import com.atasoy.eymentelefon.ui.bilesenler.SayfaSecenegi
import com.atasoy.eymentelefon.ui.bilesenler.sessizTiklama
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.Kisi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun KisiOnizleme(kisi: Kisi, onKapat: () -> Unit, onKart: () -> Unit) {
    val context = LocalContext.current
    val k = remember { context.konteyner() }
    val kapsam = rememberCoroutineScope()
    val titresim = LocalHapticFeedback.current
    var silOnay by remember { mutableStateOf(false) }
    val numara = kisi.varsayilanNumara?.numara

    LaunchedEffect(Unit) { titresim.performHapticFeedback(HapticFeedbackType.LongPress) }

    val poster by produceState<ImageBitmap?>(null, kisi.lookupKey) {
        value = withContext(Dispatchers.IO) {
            runCatching { k.posterDeposu.oku(kisi.lookupKey)?.dosya?.let { BitmapFactory.decodeFile(it.path)?.asImageBitmap() } }
                .onFailure { HataKaydedici.kaydet("KisiOnizleme.poster", it) }
                .getOrNull()
        }
    }
    val acilis = remember { Animatable(ACILIS_OLCEK) }
    LaunchedEffect(Unit) { acilis.animateTo(1f, tween(ACILIS_MS, easing = FastOutSlowInEasing)) }

    fun kapatVe(islem: () -> Unit) {
        onKapat()
        islem()
    }

    Dialog(onDismissRequest = onKapat, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        ArkaPlaniBulaniklastir()
        Box(
            Modifier
                .fillMaxSize()
                .background(EymenRenk.arkaPlan.copy(alpha = ORTU))
                .sessizTiklama(onTikla = onKapat),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier
                    .padding(horizontal = EymenBosluk.xl)
                    .graphicsLayer {
                        scaleX = acilis.value
                        scaleY = acilis.value
                        alpha = (acilis.value - ACILIS_OLCEK) / (1f - ACILIS_OLCEK)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(EymenBosluk.m),
            ) {
                // Posterli kart → dokun: kişi kartı
                Box(
                    Modifier
                        .width(EymenBoyut.onizlemeKart)
                        .height(EymenBoyut.onizlemeKartBoy)
                        .clip(EymenSekil.sayfa)
                        .background(Brush.verticalGradient(AramaEkraniRenk.palet(kisi.ad)))
                        .sessizTiklama { kapatVe(onKart) },
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    val p = poster
                    if (p != null) {
                        PosterFotografi(p, canli = false, bulanik = false)
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            IosAvatar(kisi.basHarfler, EymenBoyut.avatarKart, fotoUri = kisi.fotoUri)
                        }
                    }
                    Column(Modifier.padding(EymenBosluk.m), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(kisi.ad, style = EymenYazi.baslik1, color = AramaEkraniRenk.beyaz, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                        numara?.let { Text(NumaraBicimleyici.bicimle(it), style = EymenYazi.altBaslik, color = AramaEkraniRenk.ikincil, maxLines = 1) }
                    }
                }

                // Bağlam menüsü
                Column(Modifier.width(EymenBoyut.onizlemeMenu).clip(EymenSekil.sayfa).background(EymenRenk.yuzey)) {
                    if (numara != null) {
                        MenuSatiri(stringResource(R.string.ara), EymenIkon.Telefon) { kapatVe { k.aramaBaslatici.ara(numara) } }
                        Ayirici()
                        MenuSatiri(stringResource(R.string.kayit_mesaj), EymenIkon.MesajDolu) { kapatVe { DisIslemler.mesajGonder(context, numara) } }
                        if (k.aramaBaslatici.goruntuluDestekleniyor()) {
                            Ayirici()
                            MenuSatiri(stringResource(R.string.onizleme_video), EymenIkon.VideoDolu) { kapatVe { k.aramaBaslatici.ara(numara, goruntulu = true) } }
                        }
                    }
                    MenuAyraci()
                    MenuSatiri(stringResource(R.string.kart_paylas), EymenIkon.Paylas) { kapatVe { DisIslemler.vcardPaylas(context, k.rehberDeposu.vcardUri(kisi)) } }
                    Ayirici()
                    MenuSatiri(
                        stringResource(if (kisi.favori) R.string.favorilerden_cikar else R.string.favorilere_ekle),
                        if (kisi.favori) EymenIkon.YildizDolu else EymenIkon.Yildiz,
                    ) { kapatVe { kapsam.launch { k.rehberDeposu.favoriYap(kisi.id, !kisi.favori) } } }
                    Ayirici()
                    MenuSatiri(stringResource(R.string.duzenle), EymenIkon.Duzenle) { kapatVe { DisIslemler.kisiDuzenle(context, k.rehberDeposu.kisiUri(kisi)) } }
                    MenuAyraci()
                    MenuSatiri(stringResource(R.string.kart_kisiyi_sil), EymenIkon.Sil, yikici = true) { silOnay = true }
                }
            }
        }
    }

    if (silOnay) {
        IosEylemSayfasi(
            baslik = stringResource(R.string.kart_sil_onay),
            secenekler = listOf(
                SayfaSecenegi(stringResource(R.string.kart_kisiyi_sil), {
                    silOnay = false
                    kapatVe { kapsam.launch { k.rehberDeposu.sil(kisi.id) } }
                }, yikici = true),
            ),
            vazgecMetni = stringResource(R.string.vazgec),
            onKapat = { silOnay = false },
        )
    }
}

/** Android 12+: pencerenin arkasını gerçek bulanıklaştırma; altında sadece örtü. */
@Composable
private fun ArkaPlaniBulaniklastir() {
    val pencere = (LocalView.current.parent as? DialogWindowProvider)?.window ?: return
    val yaricap = with(LocalDensity.current) { EymenBoyut.posterBulanik.roundToPx() }
    SideEffect {
        pencere.setDimAmount(0f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pencere.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            pencere.attributes = pencere.attributes.apply { blurBehindRadius = yaricap }
        }
    }
}

@Composable
private fun MenuSatiri(metin: String, ikon: EymenIkon, yikici: Boolean = false, onTikla: () -> Unit) {
    val renk = if (yikici) EymenRenk.kirmizi else EymenRenk.etiket
    Row(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = EymenBoyut.satir)
            .sessizTiklama(onTikla = onTikla)
            .padding(horizontal = EymenBosluk.kenar),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(metin, style = EymenYazi.govde, color = renk, modifier = Modifier.weight(1f), maxLines = 1)
        EymenIkonu(ikon, null, renk)
    }
}

/** iOS menülerindeki kalın bölüm ayracı */
@Composable
private fun MenuAyraci() {
    Box(Modifier.fillMaxWidth().height(EymenBosluk.s).background(EymenRenk.grupluArkaPlan))
}

private const val ACILIS_OLCEK = 0.9f
private const val ACILIS_MS = 220
private const val ORTU = 0.4f
