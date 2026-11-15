package com.atasoy.eymentelefon.ui.ekranlar.rehber

import android.content.Intent
import android.provider.ContactsContract
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.EymenAramaCubugu
import com.atasoy.eymentelefon.ui.bilesenler.HarfDizini
import com.atasoy.eymentelefon.ui.bilesenler.KisiAvatari
import com.atasoy.eymentelefon.ui.bilesenler.KisiSatiri
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenGradyan
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.model.Numara
import com.atasoy.eymentelefon.veri.model.NumaraTuru
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RehberEkrani(
    onAramaAc: () -> Unit,
    onKisiDetay: (Kisi) -> Unit,
    vm: RehberViewModel = viewModel(factory = RehberViewModel.Fabrika),
) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.olay(RehberOlay.ProfilYenile) }
    RehberIcerik(
        durum = durum,
        onAramaAc = onAramaAc,
        onKisiDetay = onKisiDetay,
        onFavoriAra = { kisi ->
            val numara = kisi.varsayilanNumara?.numara
            if (numara.isNullOrBlank()) onKisiDetay(kisi)
            else context.konteyner().aramaBaslatici.ara(numara)
        },
        onFavoriCikar = { id -> vm.olay(RehberOlay.FavoridenCikar(id)) },
        onYeniKisi = {
            try {
                context.startActivity(
                    Intent(Intent.ACTION_INSERT).apply {
                        type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
                    },
                )
            } catch (hata: RuntimeException) {
                HataKaydedici.kaydet("RehberEkrani.yeniKisi", hata)
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RehberIcerik(
    durum: RehberUiState,
    onAramaAc: () -> Unit,
    onKisiDetay: (Kisi) -> Unit,
    onFavoriAra: (Kisi) -> Unit,
    onFavoriCikar: (Long) -> Unit,
    onYeniKisi: () -> Unit,
) {
    val renk = EymenRenk
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dar by remember { derivedStateOf {
        listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
    } }
    val harfGruplari = remember(durum.kisiler) {
        val grouped = durum.kisiler.groupBy { it.grupHarfi }
        (TurkceMetin.HARFLER + "#").mapNotNull { harf ->
            grouped[harf]?.takeIf(List<Kisi>::isNotEmpty)?.let { harf to it }
        }
    }
    val harfIndexleri = remember(durum.favoriler, durum.benimKartim, harfGruplari) {
        val sonuc = mutableMapOf<String, Int>()
        var index = 1 // Arama satırı
        if (durum.favoriler.isNotEmpty()) index++
        if (durum.benimKartim != null) index++
        if (durum.kisiler.isEmpty()) index++
        harfGruplari.forEach { (harf, kisiler) ->
            sonuc[harf] = index++
            index += kisiler.size
        }
        sonuc.toMap()
    }
    var balonHarfi by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(balonHarfi) {
        if (balonHarfi != null) {
            delay(650L)
            balonHarfi = null
        }
    }

    Box(Modifier.fillMaxSize().background(renk.arkaPlan)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = EymenBosluk.kenar, end = EymenBosluk.kenar,
                top = EymenBosluk.l, bottom = EymenBoyut.fab + EymenBosluk.xxl,
            ),
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.kartArasi),
        ) {
            item(key = "arama") {
                Box(Modifier.fillMaxWidth()) {
                    EymenAramaCubugu(
                        deger = "",
                        onDegerDegisti = { onAramaAc() },
                        ipucu = stringResource(R.string.ara_ipucu_rehber, durum.kisiler.size),
                        profilAdi = durum.benimKartim?.ad,
                    )
                    Box(Modifier.matchParentSize().clickable(onClick = onAramaAc))
                }
            }

            if (durum.favoriler.isNotEmpty()) {
                item(key = "favoriler_serit") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                    ) {
                        Text(
                            stringResource(R.string.rehber_favoriler).uppercase(TurkceMetin.TR),
                            color = renk.metin3, style = EymenYazi.bolum,
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s)) {
                            items(durum.favoriler, key = Kisi::id) { kisi ->
                                Column(
                                    modifier = Modifier.width(EymenBoyut.avatarFavori + EymenBosluk.xl)
                                        .combinedClickable(
                                            onClick = { onFavoriAra(kisi) },
                                            onLongClick = { onFavoriCikar(kisi.id) },
                                        ),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(EymenBosluk.s),
                                ) {
                                    KisiAvatari(
                                        ad = kisi.ad, fotoUri = kisi.fotoUri,
                                        boyut = EymenBoyut.avatarFavori,
                                    )
                                    Text(
                                        text = kisi.ad.substringBefore(' '),
                                        style = EymenYazi.kucuk, color = renk.metin2,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            durum.benimKartim?.let { profil ->
                item(key = "benim_kartim") {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(EymenSekil.kart)
                            .background(renk.yuzey1)
                            .heightIn(min = EymenBoyut.kaydirRay)
                            .clickable { onKisiDetay(profil) }
                            .padding(EymenBosluk.m),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                    ) {
                        KisiAvatari(
                            ad = profil.ad, fotoUri = profil.fotoUri,
                            boyut = EymenBoyut.avatarFavori,
                        )
                        Column(Modifier.weight(1f)) {
                            Text(profil.ad, style = EymenYazi.isim, color = renk.metin1)
                            Text(
                                stringResource(R.string.rehber_benim_kartim),
                                style = EymenYazi.govde, color = renk.metin3,
                            )
                        }
                        EymenIkonu(
                            EymenIkon.Ileri,
                            stringResource(R.string.rehber_benim_kartim),
                            renk.metin3,
                        )
                    }
                }
            }

            if (durum.kisiler.isEmpty() && !durum.yukleniyor) {
                item(key = "bos") {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(EymenBoyut.anaButon),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            stringResource(R.string.rehber_bos),
                            color = renk.metin3, style = EymenYazi.govde,
                        )
                    }
                }
            }

            harfGruplari.forEach { (harf, kisiler) ->
                stickyHeader(key = "harf_$harf") {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(renk.arkaPlan)
                            .padding(top = EymenBosluk.l, bottom = EymenBosluk.s),
                    ) {
                        Text(harf, color = renk.vurgu, style = EymenYazi.harfBasligi)
                    }
                }
                items(kisiler, key = { "kisi_${it.id}" }) { kisi ->
                    KisiSatiri(
                        ad = kisi.ad,
                        numara = kisi.varsayilanNumara?.numara
                            ?.let(NumaraBicimleyici::bicimle).orEmpty(),
                        onClick = { onKisiDetay(kisi) },
                        modifier = Modifier.padding(end = EymenBoyut.dokunmaMin),
                        fotoUri = kisi.fotoUri,
                    )
                }
            }
        }

        if (harfGruplari.isNotEmpty()) {
            HarfDizini(
                onHarf = { harf ->
                    balonHarfi = harf
                    harfIndexleri[harf]?.let { hedef ->
                        scope.launch { listState.animateScrollToItem(hedef) }
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd)
                    .padding(top = EymenBoyut.aramaCubugu + EymenBosluk.xl)
                    .width(EymenBoyut.dokunmaMin)
                    .fillMaxHeight(),
            )
        }

        if (balonHarfi != null) {
            Box(
                Modifier.align(Alignment.CenterEnd)
                    .padding(end = EymenBoyut.dokunmaMin + EymenBosluk.m)
                    .size(EymenBoyut.araButonu)
                    .clip(EymenSekil.kart)
                    .background(renk.yuzey2),
                contentAlignment = Alignment.Center,
            ) {
                Text(balonHarfi.orEmpty(), color = renk.vurgu, style = EymenYazi.baslik2)
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(EymenBosluk.kenar)
                .heightIn(min = EymenBoyut.fab)
                .clip(EymenSekil.fab)
                .background(EymenGradyan.vurgu(renk))
                .clickable(onClick = onYeniKisi)
                .animateContentSize()
                .padding(horizontal = EymenBosluk.l),
            horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EymenIkonu(
                EymenIkon.KisiEkle,
                stringResource(R.string.rehber_yeni_kisi),
                renk.vurguUstu,
                boyut = EymenBoyut.ikonBuyuk,
            )
            if (!dar) {
                Text(
                    stringResource(R.string.rehber_yeni_kisi),
                    style = EymenYazi.buton, color = renk.vurguUstu,
                )
            }
        }
    }
}

@Preview(name = "Koyu", showBackground = true)
@Composable
private fun RehberKoyuOnizleme() {
    val kisiler = listOf(
        Kisi(1L, "ayse", "Ayşe Demir", listOf(Numara("05339876543", NumaraTuru.MOBIL)), favori = true),
        Kisi(2L, "cagla", "Çağla Yılmaz", listOf(Numara("05321234567", NumaraTuru.MOBIL))),
    )
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        RehberIcerik(
            RehberUiState(kisiler = kisiler, favoriler = kisiler.take(1), yukleniyor = false),
            onAramaAc = {}, onKisiDetay = {}, onFavoriAra = {},
            onFavoriCikar = {}, onYeniKisi = {},
        )
    }
}

@Preview(name = "Açık", showBackground = true)
@Composable
private fun RehberAcikOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        RehberIcerik(
            RehberUiState(yukleniyor = false),
            onAramaAc = {}, onKisiDetay = {}, onFavoriAra = {},
            onFavoriCikar = {}, onYeniKisi = {},
        )
    }
}
