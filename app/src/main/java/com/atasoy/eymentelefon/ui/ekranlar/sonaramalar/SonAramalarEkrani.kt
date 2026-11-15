package com.atasoy.eymentelefon.ui.ekranlar.sonaramalar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import com.atasoy.eymentelefon.ui.bilesenler.LocalAltCamBoslugu
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.yardimci.DisIslemler
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.cekirdek.yardimci.ZamanBicimleyici
import com.atasoy.eymentelefon.ui.bilesenler.IosAvatar
import com.atasoy.eymentelefon.ui.bilesenler.IosBilgiButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosBuyukBaslik
import com.atasoy.eymentelefon.ui.bilesenler.IosEylemSayfasi
import com.atasoy.eymentelefon.ui.bilesenler.IosMetinButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosNavCubugu
import com.atasoy.eymentelefon.ui.bilesenler.IosNavIkonu
import com.atasoy.eymentelefon.ui.bilesenler.IosSatir
import com.atasoy.eymentelefon.ui.bilesenler.IosSegment
import com.atasoy.eymentelefon.ui.bilesenler.SayfaSecenegi
import com.atasoy.eymentelefon.ui.bilesenler.rememberKucukBaslikGorunur
import com.atasoy.eymentelefon.ui.bilesenler.sessizTiklama
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.AramaGrubu
import com.atasoy.eymentelefon.veri.model.AramaTuru

/**
 * 04 · Son Aramalar — dokun: ara · ⓘ: kişi kartı · sola kaydır: sil · uzun bas: eylem sayfası
 * Düzenle: satır başında kırmızı (–) ile tek tek silme, solda "Temizle" ile tümünü silme.
 */
@Composable
fun SonAramalarEkrani(
    onKisiDetay: (Long, String?) -> Unit,
    onAyarlar: () -> Unit,
    vm: SonAramalarViewModel = viewModel(factory = SonAramalarViewModel.Fabrika),
) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val liste = rememberLazyListState()
    val kucuk by rememberKucukBaslikGorunur(liste)
    var duzenle by rememberSaveable { mutableStateOf(false) }
    var temizleSor by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { vm.gorulduYap() }

    val tumu = stringResource(R.string.filtre_tumu)
    val cevapsiz = stringResource(R.string.filtre_cevapsiz)

    Column(Modifier.fillMaxSize()) {
        IosNavCubugu(
            baslikGorunur = kucuk,
            sol = {
                if (duzenle) IosMetinButonu(stringResource(R.string.temizle), { temizleSor = true })
                else IosNavIkonu(EymenIkon.Disli, stringResource(R.string.ae_ayarlar), onAyarlar)
            },
            orta = {
                IosSegment(
                    listOf(tumu, cevapsiz),
                    if (durum.filtre == AramaFiltresi.CEVAPSIZ) 1 else 0,
                    { vm.filtreSec(if (it == 1) AramaFiltresi.CEVAPSIZ else AramaFiltresi.TUMU) },
                )
            },
            sag = {
                IosMetinButonu(
                    stringResource(if (duzenle) R.string.bitti else R.string.duzenle),
                    { duzenle = !duzenle },
                    kalin = duzenle,
                )
            },
        )
        LazyColumn(state = liste, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = LocalAltCamBoslugu.current)) {
            item { IosBuyukBaslik(stringResource(R.string.son_aramalar)) }
            if (!durum.yukleniyor && durum.bolumler.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = EymenBosluk.dev * 2), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.son_bos), style = EymenYazi.baslik3, color = EymenRenk.ikincilEtiket)
                    }
                }
            }
            val tum = durum.bolumler.flatMap { it.satirlar }
            items(tum, key = { it.ilk.id }) { grup ->
                SilinebilirSatir(
                    grup = grup,
                    duzenle = duzenle,
                    sonSatir = grup == tum.lastOrNull(),
                    onSil = { vm.sil(grup) },
                    onTikla = { if (!duzenle) vm.ara(grup.ilk.numara) },
                    onBilgi = { onKisiDetay(-1L, grup.ilk.numara) },
                    onUzunBas = { vm.menuAc(grup) },
                )
            }
            item { Box(Modifier.padding(bottom = EymenBosluk.xl)) }
        }
    }

    durum.secili?.let { grup ->
        val n = grup.ilk.numara
        IosEylemSayfasi(
            baslik = grup.ilk.ad ?: NumaraBicimleyici.bicimle(n),
            secenekler = buildList {
                add(SayfaSecenegi(context.getString(R.string.kayit_kopyala), { DisIslemler.kopyala(context, "numara", n); vm.menuAc(null) }))
                add(SayfaSecenegi(context.getString(R.string.kayit_mesaj), { DisIslemler.mesajGonder(context, n); vm.menuAc(null) }))
                if (grup.ilk.ad == null) add(SayfaSecenegi(context.getString(R.string.kayit_kisiye_ekle), { DisIslemler.kisiyeEkle(context, n); vm.menuAc(null) }))
                add(SayfaSecenegi(context.getString(R.string.kayit_engelle), { vm.engelle(n) }, yikici = true))
                add(SayfaSecenegi(context.getString(R.string.kayit_sil), { vm.sil(grup) }, yikici = true))
            },
            vazgecMetni = stringResource(R.string.vazgec),
            onKapat = { vm.menuAc(null) },
        )
    }
    if (temizleSor) {
        IosEylemSayfasi(
            baslik = stringResource(R.string.gecmisi_temizle_baslik),
            secenekler = listOf(
                SayfaSecenegi(stringResource(R.string.gecmisi_temizle), {
                    vm.tumunuSil(); temizleSor = false; duzenle = false
                }, yikici = true),
            ),
            vazgecMetni = stringResource(R.string.vazgec),
            onKapat = { temizleSor = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SilinebilirSatir(
    grup: AramaGrubu,
    duzenle: Boolean,
    sonSatir: Boolean,
    onSil: () -> Unit,
    onTikla: () -> Unit,
    onBilgi: () -> Unit,
    onUzunBas: () -> Unit,
) {
    val r = EymenRenk
    val context = LocalContext.current
    val kaydir = rememberSwipeToDismissBoxState(confirmValueChange = { v ->
        if (v == SwipeToDismissBoxValue.EndToStart) {
            onSil()
            true
        } else {
            false
        }
    })
    val k = grup.ilk
    val ad = k.ad ?: NumaraBicimleyici.bicimle(k.numara)
    val kirmizi = k.tur == AramaTuru.CEVAPSIZ || k.tur == AramaTuru.ENGELLENEN
    val tekrar = if (grup.sayi > 1) " " + stringResource(R.string.tekrar_sayisi, grup.sayi) else ""
    val icerik: @Composable () -> Unit = {
        Row(Modifier.fillMaxWidth().background(r.arkaPlan).padding(start = EymenBosluk.kenar), verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(duzenle) {
                Box(
                    Modifier.padding(end = EymenBosluk.m).size(EymenBoyut.ikon).clip(CircleShape).background(r.kirmizi).sessizTiklama(onTikla = onSil),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(Modifier.size(EymenBosluk.m, EymenBosluk.cokKucuk).background(AramaEkraniRenk.beyaz))
                }
            }
            IosSatir(
                baslik = buildAnnotatedString {
                    append(ad)
                    if (tekrar.isNotEmpty()) {
                        withStyle(SpanStyle(color = r.ikincilEtiket, fontWeight = EymenYazi.govde.fontWeight)) { append(tekrar) }
                    }
                },
                modifier = Modifier.weight(1f),
                kalin = true,
                baslikRengi = if (kirmizi) r.kirmizi else r.etiket,
                minYukseklik = EymenBoyut.satirAvatarli,
                ayirici = !sonSatir,
                sol = { IosAvatar(if (k.ad == null) "#" else TurkceMetin.basHarfler(ad), EymenBoyut.avatarListe) },
                altBaslik = @Composable {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs)) {
                        EymenIkonu(
                            if (k.tur == AramaTuru.ENGELLENEN) EymenIkon.Engelle else EymenIkon.Telefon,
                            null, r.ikincilEtiket, boyut = EymenBoyut.ikonMini,
                        )
                        Text(
                            if (k.tur == AramaTuru.ENGELLENEN) stringResource(R.string.kayit_engellendi) else k.altBilgi ?: stringResource(R.string.numara_diger),
                            style = EymenYazi.altBaslik,
                            color = r.ikincilEtiket,
                            maxLines = 1,
                        )
                    }
                },
                sag = {
                    Text(ZamanBicimleyici.kisa(context, k.tarih), style = EymenYazi.altBaslik, color = r.ikincilEtiket, maxLines = 1)
                    if (!duzenle) IosBilgiButonu(stringResource(R.string.ae_bilgi), onBilgi)
                },
                onTikla = onTikla,
                onUzunBas = if (duzenle) null else onUzunBas,
            )
        }
    }
    if (duzenle) {
        icerik()
    } else {
        SwipeToDismissBox(
            state = kaydir,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                Box(Modifier.fillMaxSize().background(r.kirmizi), contentAlignment = Alignment.CenterEnd) {
                    Text(stringResource(R.string.sil), style = EymenYazi.govde, color = AramaEkraniRenk.beyaz, modifier = Modifier.padding(end = EymenBosluk.l))
                }
            },
        ) { icerik() }
    }
}
