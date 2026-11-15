package com.atasoy.eymentelefon.ui.ekranlar.sonaramalar

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.AltSayfa
import com.atasoy.eymentelefon.ui.bilesenler.AramaSatiri
import com.atasoy.eymentelefon.ui.bilesenler.EymenAramaCubugu
import com.atasoy.eymentelefon.ui.bilesenler.EymenCip
import com.atasoy.eymentelefon.ui.bilesenler.EymenCipTuru
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenGradyan
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.AramaGrubu
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import com.atasoy.eymentelefon.veri.model.AramaTuru
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

private enum class KayitMenuEylemi { KOPYALA, MESAJ, KISIYE_EKLE, ENGELLE, SIL }

@Composable
fun SonAramalarEkrani(
    onAramaEkrani: () -> Unit,
    onDetay: (AramaKaydi) -> Unit,
    onTuslar: () -> Unit,
    numaraFiltresi: String = "",
    vm: SonAramalarViewModel = viewModel(factory = SonAramalarViewModel.Fabrika),
) {
    val context = LocalContext.current
    val durum by vm.durum.collectAsStateWithLifecycle()
    val kopyaEtiketi = stringResource(R.string.kayit_kopyala)
    LaunchedEffect(Unit) { vm.gorulduYap() }
    LaunchedEffect(numaraFiltresi) { vm.numaraFiltrele(numaraFiltresi) }

    fun etkinlikAc(intent: Intent, etiket: String) {
        try {
            context.startActivity(intent)
        } catch (hata: RuntimeException) {
            HataKaydedici.kaydet(etiket, hata)
        }
    }

    SonAramalarIcerik(
        durum = durum,
        onOlay = vm::olay,
        onAramaEkrani = onAramaEkrani,
        onDetay = onDetay,
        onTuslar = onTuslar,
        onAra = { numara ->
            if (numara.isNotBlank()) context.konteyner().aramaBaslatici.ara(numara)
        },
        onMenuEylem = { eylem, grup ->
            val numara = grup.ilk.numara
            when (eylem) {
                KayitMenuEylemi.KOPYALA -> {
                    try {
                        val pano = context.getSystemService(ClipboardManager::class.java)
                        pano?.setPrimaryClip(ClipData.newPlainText(kopyaEtiketi, numara))
                    } catch (hata: RuntimeException) {
                        HataKaydedici.kaydet("SonAramalar.kopyala", hata)
                    }
                }
                KayitMenuEylemi.MESAJ -> if (numara.isNotBlank()) {
                    etkinlikAc(
                        Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", numara, null)),
                        "SonAramalar.mesaj",
                    )
                }
                KayitMenuEylemi.KISIYE_EKLE -> if (numara.isNotBlank()) {
                    etkinlikAc(
                        Intent(Intent.ACTION_INSERT).apply {
                            type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
                            putExtra(ContactsContract.Intents.Insert.PHONE, numara)
                        },
                        "SonAramalar.kisiyeEkle",
                    )
                }
                KayitMenuEylemi.ENGELLE -> vm.olay(SonAramalarOlay.Engelle(numara))
                KayitMenuEylemi.SIL -> vm.olay(SonAramalarOlay.Sil(grup))
            }
            vm.olay(SonAramalarOlay.MenuKapat)
        },
    )
}

@Composable
private fun SonAramalarIcerik(
    durum: SonAramalarUiState,
    onOlay: (SonAramalarOlay) -> Unit,
    onAramaEkrani: () -> Unit,
    onDetay: (AramaKaydi) -> Unit,
    onTuslar: () -> Unit,
    onAra: (String) -> Unit,
    onMenuEylem: (KayitMenuEylemi, AramaGrubu) -> Unit,
) {
    val r = EymenRenk
    val saatBicimi = DateTimeFormatter.ofPattern("HH:mm", TurkceMetin.TR)
    val bolumler = tarihBolumlerineAyir(durum.gruplar)
    val aramaAciklamasi = stringResource(R.string.ae_ara)

    Box(Modifier.fillMaxSize().background(r.arkaPlan)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = EymenBosluk.kenar,
                end = EymenBosluk.kenar,
                top = EymenBosluk.l,
                bottom = EymenBoyut.fab + EymenBosluk.xxl,
            ),
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.kartArasi),
        ) {
            item(key = "arama") {
                Box(Modifier.fillMaxWidth()) {
                    EymenAramaCubugu(
                        deger = "",
                        onDegerDegisti = { onAramaEkrani() },
                        profilAdi = durum.profilAdi,
                    )
                    Box(
                        Modifier.matchParentSize()
                            .clickable(onClickLabel = aramaAciklamasi, onClick = onAramaEkrani),
                    )
                }
            }

            item(key = "filtre") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s),
                    contentPadding = PaddingValues(vertical = EymenBosluk.s),
                ) {
                    item {
                        EymenCip(
                            metin = stringResource(R.string.filtre_tumu),
                            tur = EymenCipTuru.FILTRE,
                            secili = durum.filtre == SonAramaFiltresi.TUMU,
                            onClick = { onOlay(SonAramalarOlay.FiltreSec(SonAramaFiltresi.TUMU)) },
                        )
                    }
                    item {
                        CevapsizFiltreCipi(
                            secili = durum.filtre == SonAramaFiltresi.CEVAPSIZ,
                            sayi = durum.cevapsizSayisi,
                            onClick = { onOlay(SonAramalarOlay.FiltreSec(SonAramaFiltresi.CEVAPSIZ)) },
                        )
                    }
                    item {
                        EymenCip(
                            metin = stringResource(R.string.filtre_gelen),
                            tur = EymenCipTuru.FILTRE,
                            secili = durum.filtre == SonAramaFiltresi.GELEN,
                            onClick = { onOlay(SonAramalarOlay.FiltreSec(SonAramaFiltresi.GELEN)) },
                        )
                    }
                    item {
                        EymenCip(
                            metin = stringResource(R.string.filtre_giden),
                            tur = EymenCipTuru.FILTRE,
                            secili = durum.filtre == SonAramaFiltresi.GIDEN,
                            onClick = { onOlay(SonAramalarOlay.FiltreSec(SonAramaFiltresi.GIDEN)) },
                        )
                    }
                }
            }

            if (bolumler.isEmpty()) {
                item(key = "bos") {
                    Box(
                        Modifier.fillMaxWidth().height(EymenBoyut.anaButon),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            stringResource(R.string.son_bos),
                            color = r.metin3,
                            style = EymenYazi.govde,
                        )
                    }
                }
            }

            bolumler.forEach { bolum ->
                item(key = "baslik_${bolum.baslik}") {
                    Text(
                        text = stringResource(bolum.baslik).uppercase(TurkceMetin.TR),
                        modifier = Modifier.padding(top = EymenBosluk.l, bottom = EymenBosluk.s),
                        color = r.metin3,
                        style = EymenYazi.bolum,
                    )
                }
                items(
                    items = bolum.gruplar,
                    key = { "kayit_${it.ilk.id}" },
                ) { grup ->
                    val kayit = grup.ilk
                    val gorunenAd = kayit.ad?.takeIf(String::isNotBlank)
                        ?: kayit.numara.ifBlank { stringResource(R.string.gorusme_bilinmeyen) }
                    val altBilgi = kayit.altBilgi?.takeIf(String::isNotBlank)
                        ?: stringResource(
                            when (kayit.tur) {
                                AramaTuru.GELEN -> R.string.arama_gelen
                                AramaTuru.GIDEN -> R.string.arama_giden
                                AramaTuru.CEVAPSIZ -> R.string.arama_cevapsiz
                                AramaTuru.REDDEDILEN -> R.string.arama_reddedilen
                                AramaTuru.ENGELLENEN -> R.string.kayit_engellendi
                                AramaTuru.SESLI_MESAJ -> R.string.arama_sesli_mesaj
                            }
                        )
                    val saat = Instant.ofEpochMilli(kayit.tarih)
                        .atZone(ZoneId.systemDefault()).format(saatBicimi)
                    AramaSatiri(
                        ad = gorunenAd,
                        altBilgi = altBilgi,
                        saat = saat,
                        tur = kayit.tur,
                        tekrar = grup.sayi,
                        onAc = { onDetay(kayit) },
                        onAra = { onAra(kayit.numara) },
                        onUzunBas = { onOlay(SonAramalarOlay.MenuAc(grup)) },
                    )
                }
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(EymenBosluk.kenar)
                .size(EymenBoyut.fab)
                .clip(EymenSekil.fab)
                .background(EymenGradyan.vurgu(r))
                .clickable(onClickLabel = stringResource(R.string.ae_tus_takimi), onClick = onTuslar),
            contentAlignment = Alignment.Center,
        ) {
            EymenIkonu(
                ikon = EymenIkon.Tuslar,
                aciklama = stringResource(R.string.ae_tus_takimi),
                renk = r.vurguUstu,
                boyut = EymenBoyut.ikonBuyuk,
            )
        }
    }

    val menu = durum.acikMenu
    if (menu != null) {
        AltSayfa(onKapat = { onOlay(SonAramalarOlay.MenuKapat) }) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = EymenBosluk.kenar),
                verticalArrangement = Arrangement.spacedBy(EymenBosluk.xs),
            ) {
                listOf(
                    Triple(KayitMenuEylemi.KOPYALA, R.string.kayit_kopyala, EymenIkon.Dosya),
                    Triple(KayitMenuEylemi.MESAJ, R.string.kayit_mesaj, EymenIkon.Mesaj),
                    Triple(KayitMenuEylemi.KISIYE_EKLE, R.string.kayit_kisiye_ekle, EymenIkon.KisiEkle),
                    Triple(KayitMenuEylemi.ENGELLE, R.string.kayit_engelle, EymenIkon.Engelle),
                    Triple(KayitMenuEylemi.SIL, R.string.kayit_sil, EymenIkon.Sil),
                ).forEach { (eylem, baslik, ikon) ->
                    val aciklama = stringResource(baslik)
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = EymenBoyut.dokunmaMin)
                            .clickable { onMenuEylem(eylem, menu) }
                            .padding(horizontal = EymenBosluk.s),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                    ) {
                        EymenIkonu(ikon, aciklama, if (eylem == KayitMenuEylemi.SIL) r.cevapsiz else r.metin2)
                        Text(
                            aciklama,
                            color = if (eylem == KayitMenuEylemi.SIL) r.cevapsiz else r.metin1,
                            style = EymenYazi.govde,
                        )
                    }
                }
                Spacer(Modifier.height(EymenBosluk.xl))
            }
        }
    }
}

@Composable
private fun CevapsizFiltreCipi(secili: Boolean, sayi: Int, onClick: () -> Unit) {
    val r = EymenRenk
    Row(
        modifier = Modifier
            .heightIn(min = EymenBoyut.dokunmaMin)
            .clip(EymenSekil.cip)
            .background(if (secili) r.metin1 else r.yuzey2)
            .clickable(onClick = onClick)
            .padding(horizontal = EymenBosluk.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs),
    ) {
        Text(
            stringResource(R.string.filtre_cevapsiz),
            color = if (secili) r.arkaPlan else r.metin2,
            style = EymenYazi.cip,
        )
        if (sayi > 0) {
            Text(sayi.toString(), color = r.cevapsiz, style = EymenYazi.cip)
        }
    }
}

private data class TarihBolumu(val baslik: Int, val gruplar: List<AramaGrubu>)

/** Cihazın yerel saat dilimine ve Türkçe haftanın pazartesi başlangıcına göre başlıklar. */
private fun tarihBolumlerineAyir(gruplar: List<AramaGrubu>): List<TarihBolumu> {
    val bolumler = linkedMapOf<Int, MutableList<AramaGrubu>>()
    val zone = ZoneId.systemDefault()
    val bugun = LocalDate.now(zone)
    val dun = bugun.minusDays(1)
    val haftaBasi = bugun.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    for (grup in gruplar) {
        val gun = Instant.ofEpochMilli(grup.ilk.tarih).atZone(zone).toLocalDate()
        val baslik = when {
            gun == bugun -> R.string.grup_bugun
            gun == dun -> R.string.grup_dun
            !gun.isBefore(haftaBasi) && !gun.isAfter(bugun) -> R.string.grup_bu_hafta
            else -> R.string.grup_eski
        }
        bolumler.getOrPut(baslik) { mutableListOf() }.add(grup)
    }
    return bolumler.map { (baslik, kayitlar) -> TarihBolumu(baslik, kayitlar) }
}

@Preview(name = "Koyu", showBackground = true)
@Composable
private fun SonAramalarKoyuOnizleme() {
    val simdi = System.currentTimeMillis()
    val kayit = AramaKaydi(
        id = 1L, numara = "05321234567", ad = "Ayşe Demir",
        kisiId = null, tur = AramaTuru.GELEN, tarih = simdi,
        sureSn = 62L, altBilgi = "Mobil", yeni = false, hesapId = null,
    )
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        SonAramalarIcerik(
            durum = SonAramalarUiState(
                gruplar = listOf(AramaGrubu(kayit, listOf(1L))),
                cevapsizSayisi = 2,
            ),
            onOlay = {}, onAramaEkrani = {}, onDetay = {}, onTuslar = {},
            onAra = {}, onMenuEylem = { _, _ -> },
        )
    }
}

@Preview(name = "Acik", showBackground = true)
@Composable
private fun SonAramalarAcikOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        SonAramalarIcerik(
            durum = SonAramalarUiState(),
            onOlay = {}, onAramaEkrani = {}, onDetay = {}, onTuslar = {},
            onAra = {}, onMenuEylem = { _, _ -> },
        )
    }
}
