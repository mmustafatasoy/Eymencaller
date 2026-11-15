package com.atasoy.eymentelefon.ui.ekranlar.arama

import android.content.Intent
import android.provider.ContactsContract
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.KisiAvatari
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import com.atasoy.eymentelefon.veri.model.AramaTuru
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.model.Numara
import com.atasoy.eymentelefon.veri.model.NumaraTuru
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** 07: Odaklı arama kutusu, gerçek rehber/geçmiş ve ilk sonuca arama eylemi. */
@Composable
fun AramaEkrani(
    onGeri: () -> Unit,
    onKisiDetay: (Kisi) -> Unit,
    onGecmisDetay: (AramaKaydi) -> Unit,
    vm: AramaViewModel = viewModel(factory = AramaViewModel.Fabrika),
) {
    val context = LocalContext.current
    val durum by vm.durum.collectAsStateWithLifecycle()
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    fun ara(numara: String?) {
        if (!numara.isNullOrBlank()) {
            keyboard?.hide()
            context.konteyner().aramaBaslatici.ara(numara)
        }
    }

    AramaIcerik(
        durum = durum,
        focusRequester = focusRequester,
        onGeri = onGeri,
        onSorgu = { vm.olay(AramaOlay.SorguDegisti(it)) },
        onTemizle = { vm.olay(AramaOlay.Temizle) },
        onAra = { ara(vm.ilkAranacakNumara()) },
        onKisiDetay = onKisiDetay,
        onGecmisDetay = onGecmisDetay,
        onNumaraAra = ::ara,
        onKisiEkle = {
            try {
                context.startActivity(
                    Intent(Intent.ACTION_INSERT).apply {
                        type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
                    },
                )
            } catch (hata: RuntimeException) {
                HataKaydedici.kaydet("AramaEkrani.kisiEkle", hata)
            }
        },
    )
}

@Composable
private fun AramaIcerik(
    durum: AramaUiState,
    focusRequester: FocusRequester?,
    onGeri: () -> Unit,
    onSorgu: (String) -> Unit,
    onTemizle: () -> Unit,
    onAra: () -> Unit,
    onKisiDetay: (Kisi) -> Unit,
    onGecmisDetay: (AramaKaydi) -> Unit,
    onNumaraAra: (String) -> Unit,
    onKisiEkle: () -> Unit,
) {
    val renk = EymenRenk
    val sorgu = durum.sorgu
    val sayisalMi = sorgu.isNotBlank() && sorgu.trim().all(Char::isDigit)
    val hazir = sorgu == durum.sonucSorgusu && !durum.yukleniyor

    Column(
        modifier = Modifier.fillMaxSize().background(renk.arkaPlan)
            .safeDrawingPadding().imePadding()
            .padding(horizontal = EymenBosluk.kenar),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = EymenBosluk.l),
            horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(EymenBoyut.dokunmaMin).clickable(onClick = onGeri),
                contentAlignment = Alignment.Center,
            ) {
                EymenIkonu(
                    EymenIkon.Geri, stringResource(R.string.ae_geri), renk.metin1,
                )
            }
            Row(
                modifier = Modifier.weight(1f).heightIn(min = EymenBoyut.aramaCubugu)
                    .clip(EymenSekil.hap)
                    .background(renk.yuzey1)
                    .border(EymenBoyut.odakCercevesi, renk.vurgu, EymenSekil.hap)
                    .padding(horizontal = EymenBosluk.m),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s),
            ) {
                EymenIkonu(
                    EymenIkon.Buyutec, aciklama = null,
                    renk = renk.metin3, boyut = EymenBoyut.ikonKucuk,
                )
                BasicTextField(
                    value = sorgu,
                    onValueChange = onSorgu,
                    modifier = Modifier.weight(1f).then(
                        if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier
                    ),
                    singleLine = true,
                    textStyle = EymenYazi.govde.copy(color = renk.metin1),
                    cursorBrush = SolidColor(renk.vurgu),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Search,
                    ),
                    keyboardActions = KeyboardActions(onSearch = { onAra() }),
                    decorationBox = { inner ->
                        Box {
                            if (sorgu.isBlank()) {
                                Text(
                                    stringResource(R.string.ara_ipucu),
                                    style = EymenYazi.govde, color = renk.metin3,
                                )
                            }
                            inner()
                        }
                    },
                )
                if (sorgu.isNotEmpty()) {
                    Box(
                        modifier = Modifier.size(EymenBoyut.dokunmaMin)
                            .clickable(onClick = onTemizle),
                        contentAlignment = Alignment.Center,
                    ) {
                        EymenIkonu(
                            EymenIkon.Sil, stringResource(R.string.ae_temizle), renk.metin3,
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.size(EymenBoyut.dokunmaMin)
                            .clickable(onClick = onKisiEkle),
                        contentAlignment = Alignment.Center,
                    ) {
                        EymenIkonu(
                            EymenIkon.Arti, stringResource(R.string.ae_kisi_ekle), renk.metin3,
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.kartArasi),
        ) {
            if (sayisalMi) {
                item(key = "numarayi_ara") {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = EymenBoyut.anaButon)
                            .clickable { onNumaraAra(sorgu.trim()) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                    ) {
                        EymenIkonu(EymenIkon.Telefon, stringResource(R.string.ae_ara), renk.vurgu)
                        Text(
                            stringResource(R.string.arama_numarayi_ara, NumaraBicimleyici.bicimle(sorgu.trim())),
                            style = EymenYazi.isim, color = renk.vurgu,
                        )
                    }
                }
            }

            if (hazir && durum.kisiler.isNotEmpty()) {
                item(key = "kisi_baslik") {
                    Text(
                        stringResource(R.string.arama_kisiler, durum.kisiler.size)
                            .uppercase(TurkceMetin.TR),
                        modifier = Modifier.padding(top = EymenBosluk.xl),
                        style = EymenYazi.bolum,
                        color = renk.metin3,
                    )
                }
                items(durum.kisiler, key = { "kisi_" + it.id }) { kisi ->
                    val varsayilan = kisi.varsayilanNumara
                    val notEslesmesi = !kisi.not.isNullOrBlank() &&
                        TurkceMetin.iceriyorMu(kisi.not, sorgu)
                    val alt = if (notEslesmesi) {
                        stringResource(R.string.arama_not_etiket) + kisi.not.orEmpty()
                    } else {
                        val etiket = when (varsayilan?.tur) {
                            NumaraTuru.MOBIL -> R.string.numara_mobil
                            NumaraTuru.EV -> R.string.numara_ev
                            NumaraTuru.IS -> R.string.numara_is
                            else -> R.string.numara_diger
                        }
                        stringResource(etiket) + " · " +
                            NumaraBicimleyici.bicimle(varsayilan?.numara.orEmpty())
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = EymenBoyut.anaButon)
                            .clickable { onKisiDetay(kisi) }
                            .padding(vertical = EymenBosluk.s),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                    ) {
                        KisiAvatari(kisi.ad, fotoUri = kisi.fotoUri, boyut = EymenBoyut.avatarAramaListe)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(EymenBosluk.xs),
                        ) {
                            VurguluMetin(kisi.ad, sorgu, EymenYazi.isim, renk.metin1)
                            VurguluMetin(alt, sorgu, EymenYazi.kucuk, renk.metin3)
                        }
                        if (varsayilan != null) {
                            Box(
                                modifier = Modifier.size(EymenBoyut.dokunmaMin)
                                    .clickable { onNumaraAra(varsayilan.numara) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Box(
                                    modifier = Modifier.size(EymenBoyut.listeAraButonu)
                                        .background(renk.araYesil.copy(alpha = .18f), CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    EymenIkonu(
                                        EymenIkon.Telefon, stringResource(R.string.ae_ara),
                                        renk.araYesil, boyut = EymenBoyut.ikonKucuk,
                                    )
                                }
                            }
                        } else {
                            Spacer(Modifier.width(EymenBoyut.dokunmaMin))
                        }
                    }
                }
            }

            if (hazir && durum.sonAramalar.isNotEmpty()) {
                item(key = "gecmis_baslik") {
                    Text(
                        stringResource(R.string.arama_son_aramalarda).uppercase(TurkceMetin.TR),
                        modifier = Modifier.padding(top = EymenBosluk.xl),
                        style = EymenYazi.bolum,
                        color = renk.metin3,
                    )
                }
                items(durum.sonAramalar, key = { "gecmis_" + it.id }) { kayit ->
                    val ad = kayit.ad?.takeIf(String::isNotBlank) ?: kayit.numara
                    val zaman = Instant.ofEpochMilli(kayit.tarih).atZone(ZoneId.systemDefault())
                    val tarih = if (zaman.toLocalDate() == LocalDate.now()) {
                        stringResource(R.string.grup_bugun)
                    } else {
                        zaman.format(DateTimeFormatter.ofPattern("dd.MM", TurkceMetin.TR))
                    }
                    val tur = stringResource(when (kayit.tur) {
                        AramaTuru.GELEN -> R.string.arama_gelen
                        AramaTuru.GIDEN -> R.string.arama_giden
                        AramaTuru.CEVAPSIZ -> R.string.arama_cevapsiz
                        AramaTuru.REDDEDILEN -> R.string.arama_reddedilen
                        AramaTuru.ENGELLENEN -> R.string.kayit_engellendi
                        AramaTuru.SESLI_MESAJ -> R.string.arama_sesli_mesaj
                    })
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = EymenBoyut.anaButon)
                            .clickable { onGecmisDetay(kayit) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                    ) {
                        Box(
                            modifier = Modifier.size(EymenBoyut.dokunmaMin),
                            contentAlignment = Alignment.Center,
                        ) {
                            EymenIkonu(
                                EymenIkon.Saat, null, renk.metin3,
                                boyut = EymenBoyut.ikonBuyuk,
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(EymenBosluk.xs),
                        ) {
                            VurguluMetin(ad, sorgu, EymenYazi.isim, renk.metin1)
                            Text(
                                tarih + " " + zaman.format(DateTimeFormatter.ofPattern("HH:mm")) +
                                    " · " + tur,
                                style = EymenYazi.kucuk, color = renk.metin3,
                            )
                        }
                    }
                }
            }

            if (hazir && sorgu.isNotBlank() &&
                durum.kisiler.isEmpty() && durum.sonAramalar.isEmpty() && !sayisalMi
            ) {
                item(key = "bos_sonuc") {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = EymenBoyut.anaButon),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            stringResource(R.string.arama_sonuc_yok),
                            style = EymenYazi.govde, color = renk.metin3,
                        )
                    }
                }
            }
        }
    }
}

/** Türkçe 1:1 karşılaştırma aralığı; numarada boşlukları yok sayan vurgu. */
@Composable
private fun VurguluMetin(
    metin: String,
    sorgu: String,
    stil: androidx.compose.ui.text.TextStyle,
    normalRenk: androidx.compose.ui.graphics.Color,
) {
    val renk = EymenRenk
    val aralik = if (sorgu.isNotBlank() && sorgu.all(Char::isDigit)) {
        val indeksler = metin.indices.filter { metin[it].isDigit() }
        val rakamlar = indeksler.map(metin::get).joinToString("")
        val baslangic = rakamlar.indexOf(sorgu)
        if (baslangic >= 0 && baslangic + sorgu.length <= indeksler.size) {
            indeksler[baslangic]..indeksler[baslangic + sorgu.length - 1]
        } else null
    } else TurkceMetin.eslesmeAraligi(metin, sorgu)

    val yazi = remember(metin, sorgu, renk.vurgu) {
        buildAnnotatedString {
            if (aralik == null || aralik.first < 0 || aralik.last >= metin.length) {
                append(metin)
            } else {
                append(metin.substring(0, aralik.first))
                withStyle(SpanStyle(
                    color = renk.vurgu, background = renk.vurgu.copy(alpha = 0.22f),
                )) {
                    append(metin.substring(aralik.first, aralik.last + 1))
                }
                append(metin.substring(aralik.last + 1))
            }
        }
    }
    Text(
        text = yazi,
        style = stil,
        color = normalRenk,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview(name = "Koyu", showBackground = true)
@Composable
private fun AramaKoyuOnizleme() {
    val ornekNumara = Numara("05339876543", NumaraTuru.MOBIL)
    val kisiler = listOf(
        Kisi(1L, "ayse", "Ayşe Demir", listOf(ornekNumara)),
        Kisi(2L, "deniz", "Deniz Aydın", listOf(ornekNumara)),
        Kisi(3L, "ahmet", "Ahmet Yılmaz", listOf(ornekNumara), not = "Ay sonu ödeme"),
    )
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        AramaIcerik(
            AramaUiState(sorgu = "Ay", sonucSorgusu = "Ay", kisiler = kisiler, yukleniyor = false),
            null, {}, {}, {}, {}, {}, {}, {}, {},
        )
    }
}

@Preview(name = "Açık", showBackground = true)
@Composable
private fun AramaAcikOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        AramaIcerik(
            AramaUiState(yukleniyor = false),
            null, {}, {}, {}, {}, {}, {}, {}, {},
        )
    }
}
