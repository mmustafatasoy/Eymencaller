package com.atasoy.eymentelefon.ui.ekranlar.rehber

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.yardimci.DisIslemler
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.ui.bilesenler.IosAramaAlani
import com.atasoy.eymentelefon.ui.bilesenler.IosAvatar
import com.atasoy.eymentelefon.ui.bilesenler.IosBolumBasligi
import com.atasoy.eymentelefon.ui.bilesenler.IosBuyukBaslik
import com.atasoy.eymentelefon.ui.bilesenler.IosEylemSayfasi
import com.atasoy.eymentelefon.ui.bilesenler.IosNavCubugu
import com.atasoy.eymentelefon.ui.bilesenler.IosNavIkonu
import com.atasoy.eymentelefon.ui.bilesenler.IosSatir
import com.atasoy.eymentelefon.ui.bilesenler.SayfaSecenegi
import com.atasoy.eymentelefon.ui.bilesenler.rememberKucukBaslikGorunur
import com.atasoy.eymentelefon.ui.ekranlar.kisiAdiMetni
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.Kisi
import kotlinx.coroutines.launch

/** 06 · Kişiler — arama alanı · Kartım · harf başlıkları (yapışkan) · sağda harf dizini · uzun bas: eylem sayfası */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RehberEkrani(
    onKisiDetay: (Long, String?) -> Unit,
    onArama: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onAyarlar: () -> Unit,
    vm: RehberViewModel = viewModel(factory = RehberViewModel.Fabrika),
) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val liste = rememberLazyListState()
    val kapsam = rememberCoroutineScope()
    val kucuk by rememberKucukBaslikGorunur(liste)
    var secili by remember { mutableStateOf<Kisi?>(null) }
    val baslik = stringResource(R.string.kisiler)

    // Liste: 0 büyük başlık, 1 arama alanı, (2 kartım), sonra her harf için 1 başlık + kişiler
    val ustOge = 2 + if (durum.benim != null) 1 else 0
    val harfSirasi = remember(durum) {
        var i = ustOge
        buildMap {
            durum.gruplar.forEach { g ->
                put(g.harf, i)
                i += 1 + g.kisiler.size
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            IosNavCubugu(
                kucukBaslik = baslik,
                baslikGorunur = kucuk,
                // V12-11: ayarlar girişi Son Aramalar'daki dişlide
                sag = { IosNavIkonu(EymenIkon.Arti, stringResource(R.string.ae_ekle)) { DisIslemler.yeniKisi(context) } },
            )
            LazyColumn(state = liste, modifier = Modifier.fillMaxSize()) {
                item { IosBuyukBaslik(baslik) }
                item {
                    IosAramaAlani(
                        stringResource(R.string.arama_ipucu),
                        onArama,
                        Modifier.padding(start = EymenBosluk.kenar, end = EymenBosluk.kenar, bottom = EymenBosluk.m),
                    )
                }
                durum.benim?.let { b ->
                    item(key = "kartim") {
                        IosSatir(
                            baslik = b.ad,
                            altBaslik = stringResource(R.string.kartim),
                            kalin = true,
                            modifier = Modifier.padding(start = EymenBosluk.kenar),
                            minYukseklik = EymenBoyut.avatarKartim + EymenBosluk.l,
                            ayirici = false,
                            sol = { IosAvatar(b.basHarfler, EymenBoyut.avatarKartim, fotoUri = b.fotoUri) },
                            onTikla = { onKisiDetay(b.id, null) },
                        )
                    }
                }
                if (!durum.yukleniyor && durum.gruplar.isEmpty()) {
                    item { IosBolumBasligi(stringResource(R.string.rehber_bos)) }
                }
                durum.gruplar.forEach { g ->
                    stickyHeader(key = "h-${g.harf}") { IosBolumBasligi(g.harf, buyuk = true) }
                    items(g.kisiler, key = { it.id }) { k ->
                        IosSatir(
                            baslik = kisiAdiMetni(k.ad),
                            modifier = Modifier.padding(start = EymenBosluk.kenar, end = EymenBosluk.l),
                            minYukseklik = EymenBoyut.satirKisi,
                            sol = { IosAvatar(k.basHarfler, EymenBoyut.avatarKucuk, fotoUri = k.fotoUri) },
                            ayirici = k != g.kisiler.last(),
                            onTikla = { onKisiDetay(k.id, null) },
                            onUzunBas = { secili = k },
                        )
                    }
                }
                if (durum.sayi > 0) {
                    item(key = "toplam") {
                        Text(
                            stringResource(R.string.kisi_sayisi, durum.sayi),
                            style = EymenYazi.dipnot,
                            color = EymenRenk.ikincilEtiket,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(EymenBosluk.kenar),
                        )
                    }
                }
                item { Box(Modifier.padding(bottom = EymenBosluk.xl)) }
            }
        }
        if (durum.gruplar.isNotEmpty()) {
            HarfDizini(
                onHarf = { h ->
                    val hedef = harfSirasi[h] ?: TurkceMetin.DIZIN.drop(TurkceMetin.DIZIN.indexOf(h)).firstNotNullOfOrNull { harfSirasi[it] }
                    if (hedef != null) kapsam.launch { liste.scrollToItem(hedef) }
                },
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = EymenBosluk.cokKucuk),
            )
        }
    }

    // V12-09: iOS gibi uzun basma önizlemesi
    secili?.let { k -> KisiOnizleme(k, onKapat = { secili = null }, onKart = { onKisiDetay(k.id, null) }) }
}

/** iOS harf dizini: mavi küçük harfler; dokun ya da sürükle → o harfe atla. */
@Composable
private fun HarfDizini(onHarf: (String) -> Unit, modifier: Modifier = Modifier) {
    val harfler = TurkceMetin.DIZIN
    var yukseklik by remember { mutableIntStateOf(1) }
    val guncel by rememberUpdatedState(onHarf)
    fun bul(y: Float) = harfler[(y / yukseklik * harfler.size).toInt().coerceIn(0, harfler.lastIndex)]
    Column(
        modifier
            .width(EymenBosluk.l)
            .onSizeChanged { yukseklik = it.height.coerceAtLeast(1) }
            .pointerInput(Unit) { detectTapGestures { guncel(bul(it.y)) } }
            .pointerInput(Unit) {
                detectVerticalDragGestures { degisim, _ ->
                    degisim.consume()
                    guncel(bul(degisim.position.y))
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        harfler.forEach { h ->
            Text(h, style = EymenYazi.dizin, color = EymenRenk.vurgu, modifier = Modifier.padding(vertical = EymenBosluk.cokKucuk / 2))
        }
    }
}

