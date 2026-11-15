package com.atasoy.eymentelefon.ui.ekranlar.kisidetay

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.KisiAvatari
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenGradyan
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Mockup 06. Sistem eylemleri burada; ViewModel Android Context tutmaz. */
@Composable
fun KisiDetayEkrani(
    kisiId: Long,
    baslangicNumara: String,
    onGeri: () -> Unit,
    onTumAramalar: () -> Unit,
    vm: KisiDetayViewModel = viewModel(factory = KisiDetayViewModel.Fabrika),
) {
    val context = LocalContext.current
    val durum by vm.durum.collectAsStateWithLifecycle()
    val paylasBaslik = stringResource(R.string.detay_paylas)
    val kopyaBaslik = stringResource(R.string.kayit_kopyala)
    LaunchedEffect(kisiId, baslangicNumara) { vm.yukle(kisiId, baslangicNumara) }
    LaunchedEffect(durum.silindi) { if (durum.silindi) onGeri() }

    fun ac(intent: Intent, etiket: String) {
        try {
            context.startActivity(intent)
        } catch (hata: RuntimeException) {
            HataKaydedici.kaydet(etiket, hata)
        }
    }

    fun duzenle(kisi: Kisi) {
        val uri = ContactsContract.Contacts.getLookupUri(kisi.id, kisi.lookupKey)
        ac(
            Intent(Intent.ACTION_EDIT).apply {
                setDataAndType(uri, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
            },
            "KisiDetay.duzenle",
        )
    }

    KisiDetayIcerik(
        durum = durum,
        onGeri = onGeri,
        onFavori = { vm.olay(KisiDetayOlay.FavoriDegistir) },
        onDuzenle = { durum.kisi?.let(::duzenle) },
        onSil = { vm.olay(KisiDetayOlay.KisiSil) },
        onAra = { numara ->
            if (numara.isNotBlank()) context.konteyner().aramaBaslatici.ara(numara)
        },
        onMesaj = { numara ->
            if (numara.isNotBlank()) ac(
                Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", numara, null)),
                "KisiDetay.mesaj",
            )
        },
        onEposta = { eposta ->
            if (eposta.isNotBlank()) ac(
                Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", eposta, null)),
                "KisiDetay.eposta",
            )
        },
        onPaylas = {
            durum.kisi?.takeIf { it.lookupKey.isNotBlank() }?.let { kisi ->
                val uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_VCARD_URI,
                    kisi.lookupKey,
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/x-vcard"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                ac(Intent.createChooser(intent, paylasBaslik), "KisiDetay.paylas")
            }
        },
        onNumaraKopyala = { numara ->
            try {
                context.getSystemService(ClipboardManager::class.java)?.setPrimaryClip(
                    ClipData.newPlainText(kopyaBaslik, numara)
                )
            } catch (hata: RuntimeException) {
                HataKaydedici.kaydet("KisiDetay.kopyala", hata)
            }
        },
        onVarsayilanDuzenle = { durum.kisi?.let(::duzenle) },
        onEngelle = { vm.olay(KisiDetayOlay.EngeliDegistir) },
        onTumAramalar = onTumAramalar,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KisiDetayIcerik(
    durum: KisiDetayUiState,
    onGeri: () -> Unit,
    onFavori: () -> Unit,
    onDuzenle: () -> Unit,
    onSil: () -> Unit,
    onAra: (String) -> Unit,
    onMesaj: (String) -> Unit,
    onEposta: (String) -> Unit,
    onPaylas: () -> Unit,
    onNumaraKopyala: (String) -> Unit,
    onVarsayilanDuzenle: () -> Unit,
    onEngelle: () -> Unit,
    onTumAramalar: () -> Unit,
) {
    val renk = EymenRenk
    val kisi = durum.kisi
    val numara = durum.numara
    val ad = kisi?.ad ?: numara.ifBlank { stringResource(R.string.gorusme_bilinmeyen) }
    val numaralar = kisi?.numaralar?.takeIf { it.isNotEmpty() }
        ?: numara.takeIf(String::isNotBlank)?.let {
            listOf(Numara(it, NumaraTuru.DIGER))
        }.orEmpty()
    var ustMenu by remember { mutableStateOf(false) }
    var seciliNumara by remember { mutableStateOf<Numara?>(null) }
    var silOnayi by remember { mutableStateOf(false) }
    val saat = remember { DateTimeFormatter.ofPattern("dd.MM HH:mm") }
    val vurguRengi = remember(ad) { EymenGradyan.avatarRenkleri(ad).first() }

    Column(
        modifier = Modifier.fillMaxSize().background(renk.arkaPlan)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = EymenBosluk.kenar),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EymenBosluk.l),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = EymenBosluk.s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs),
        ) {
            IkonEylem(EymenIkon.Geri, stringResource(R.string.ae_geri), onGeri)
            Spacer(Modifier.weight(1f))
            if (kisi != null) {
                IkonEylem(
                    if (kisi.favori) EymenIkon.YildizDolu else EymenIkon.Yildiz,
                    stringResource(R.string.ae_favori), onFavori,
                    if (kisi.favori) renk.uyari else renk.metin1,
                )
                IkonEylem(EymenIkon.Duzenle, stringResource(R.string.ae_duzenle), onDuzenle)
                Box {
                    IkonEylem(
                        EymenIkon.DahaFazla, stringResource(R.string.ae_daha_fazla),
                        { ustMenu = true },
                    )
                    DropdownMenu(expanded = ustMenu, onDismissRequest = { ustMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.detay_kisi_sil)) },
                            onClick = { ustMenu = false; silOnayi = true },
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth()
                .background(
                    Brush.radialGradient(listOf(vurguRengi.copy(alpha = .24f), renk.arkaPlan))
                ).padding(vertical = EymenBosluk.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.m),
        ) {
            KisiAvatari(
                ad = ad, fotoUri = kisi?.fotoUri, boyut = EymenBoyut.avatarDetay,
            )
            Text(ad, color = renk.metin1, style = EymenYazi.aramaIsim, textAlign = TextAlign.Center)
            val ek = listOfNotNull(kisi?.sirket, kisi?.not).filter(String::isNotBlank).joinToString(" · ")
            if (ek.isNotBlank()) {
                Text(ek, color = renk.metin2, style = EymenYazi.govde, textAlign = TextAlign.Center)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
        ) {
            val anaNumara = kisi?.varsayilanNumara?.numara ?: numara
            if (anaNumara.isNotBlank()) {
                EylemKutusu(
                    ikon = EymenIkon.Telefon,
                    baslik = stringResource(R.string.detay_ara),
                    birincil = true,
                    modifier = Modifier.weight(1f),
                    onClick = { onAra(anaNumara) },
                )
                EylemKutusu(
                    ikon = EymenIkon.Mesaj,
                    baslik = stringResource(R.string.detay_mesaj),
                    modifier = Modifier.weight(1f),
                    onClick = { onMesaj(anaNumara) },
                )
            }
            // Görüntülü arama sağlayıcısı doğrulanana kadar düğme gizlidir.
            if (!kisi?.eposta.isNullOrBlank()) {
                EylemKutusu(
                    ikon = EymenIkon.Eposta,
                    baslik = stringResource(R.string.detay_eposta),
                    modifier = Modifier.weight(1f),
                    onClick = { onEposta(kisi!!.eposta!!) },
                )
            }
        }

        if (numaralar.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().clip(EymenSekil.kart)
                    .background(renk.yuzey1).padding(EymenBosluk.m),
                verticalArrangement = Arrangement.spacedBy(EymenBosluk.s),
            ) {
                numaralar.forEach { telefon ->
                    Row(
                        modifier = Modifier.fillMaxWidth().heightIn(min = EymenBoyut.anaButon)
                            .combinedClickable(
                                onClick = { onAra(telefon.numara) },
                                onLongClick = { seciliNumara = telefon },
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                    ) {
                        EymenIkonu(
                            EymenIkon.Telefon, stringResource(R.string.detay_ara), renk.vurgu,
                        )
                        Column(Modifier.weight(1f)) {
                            Text(
                                NumaraBicimleyici.bicimle(telefon.numara),
                                color = renk.metin1, style = EymenYazi.isim,
                            )
                            val tur = when (telefon.tur) {
                                NumaraTuru.MOBIL -> R.string.numara_mobil
                                NumaraTuru.EV -> R.string.numara_ev
                                NumaraTuru.IS -> R.string.numara_is
                                NumaraTuru.DIGER -> R.string.numara_diger
                            }
                            Text(
                                stringResource(tur) + if (telefon.varsayilan) " · " +
                                    stringResource(R.string.detay_varsayilan) else "",
                                color = renk.metin3, style = EymenYazi.kucuk,
                            )
                        }
                        IkonEylem(
                            EymenIkon.Mesaj, stringResource(R.string.ae_mesaj),
                            { onMesaj(telefon.numara) }, renk.metin3,
                        )
                    }
                }
            }
        }

        if (durum.aramalar.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().clip(EymenSekil.kart)
                    .background(renk.yuzey1).padding(EymenBosluk.m),
                verticalArrangement = Arrangement.spacedBy(EymenBosluk.m),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.detay_son_aramalar),
                        style = EymenYazi.baslik2, color = renk.metin1,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        stringResource(R.string.detay_tumu),
                        color = renk.metin3, style = EymenYazi.govde,
                        modifier = Modifier.heightIn(min = EymenBoyut.dokunmaMin)
                            .clickable(onClick = onTumAramalar)
                            .padding(EymenBosluk.s),
                    )
                }
                durum.aramalar.forEach { kayit ->
                    Row(
                        modifier = Modifier.fillMaxWidth().heightIn(min = EymenBoyut.dokunmaMin),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                    ) {
                        val giden = kayit.tur == AramaTuru.GIDEN
                        EymenIkonu(
                            if (giden) EymenIkon.Giden else EymenIkon.Gelen,
                            null, if (giden) renk.vurgu2 else if (kayit.tur == AramaTuru.CEVAPSIZ)
                                renk.cevapsiz else renk.vurgu,
                        )
                        Text(
                            stringResource(
                                when (kayit.tur) {
                                    AramaTuru.GELEN -> R.string.arama_gelen
                                    AramaTuru.GIDEN -> R.string.arama_giden
                                    AramaTuru.CEVAPSIZ -> R.string.arama_cevapsiz
                                    AramaTuru.REDDEDILEN -> R.string.arama_reddedilen
                                    AramaTuru.ENGELLENEN -> R.string.kayit_engellendi
                                    AramaTuru.SESLI_MESAJ -> R.string.arama_sesli_mesaj
                                }
                            ),
                            style = EymenYazi.govde, color = renk.metin2,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            Instant.ofEpochMilli(kayit.tarih).atZone(ZoneId.systemDefault()).format(saat),
                            style = EymenYazi.kucuk, color = renk.metin3,
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().clip(EymenSekil.kart)
                .background(renk.yuzey1).padding(EymenBosluk.s),
        ) {
            if (kisi != null && kisi.lookupKey.isNotBlank()) {
                EylemSatiri(
                    ikon = EymenIkon.Paylas,
                    baslik = stringResource(R.string.detay_paylas),
                    renk = renk.metin1,
                    onClick = onPaylas,
                )
            }
            if (numara.isNotBlank()) {
                EylemSatiri(
                    ikon = EymenIkon.Engelle,
                    baslik = stringResource(
                        if (durum.engelli) R.string.detay_engeli_kaldir else R.string.detay_engelle
                    ),
                    renk = renk.cevapsiz,
                    onClick = onEngelle,
                )
            }
        }
        Spacer(Modifier.size(EymenBosluk.l))
    }

    if (seciliNumara != null) {
        val telefon = seciliNumara!!
        AlertDialog(
            onDismissRequest = { seciliNumara = null },
            title = { Text(NumaraBicimleyici.bicimle(telefon.numara)) },
            text = {
                Column {
                    Text(
                        stringResource(R.string.kayit_kopyala),
                        modifier = Modifier.fillMaxWidth().heightIn(min = EymenBoyut.dokunmaMin)
                            .clickable { onNumaraKopyala(telefon.numara); seciliNumara = null },
                    )
                    if (kisi != null && !telefon.varsayilan) {
                        Text(
                            stringResource(R.string.detay_varsayilan),
                            modifier = Modifier.fillMaxWidth().heightIn(min = EymenBoyut.dokunmaMin)
                                .clickable { onVarsayilanDuzenle(); seciliNumara = null },
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { seciliNumara = null }) {
                    Text(stringResource(R.string.eksik_simdi_degil))
                }
            },
        )
    }

    if (silOnayi) {
        AlertDialog(
            onDismissRequest = { silOnayi = false },
            title = { Text(stringResource(R.string.detay_kisi_sil)) },
            text = { Text(ad) },
            confirmButton = {
                TextButton(onClick = { silOnayi = false; onSil() }) {
                    Text(stringResource(R.string.detay_kisi_sil))
                }
            },
            dismissButton = {
                TextButton(onClick = { silOnayi = false }) {
                    Text(stringResource(R.string.eksik_simdi_degil))
                }
            },
        )
    }
}

@Composable
private fun IkonEylem(
    ikon: EymenIkon,
    aciklama: String,
    onClick: () -> Unit,
    ikonRengi: androidx.compose.ui.graphics.Color = EymenRenk.metin1,
) {
    Box(
        modifier = Modifier.size(EymenBoyut.dokunmaMin)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        EymenIkonu(ikon, aciklama, ikonRengi)
    }
}

@Composable
private fun RowScope.EylemKutusu(
    ikon: EymenIkon,
    baslik: String,
    modifier: Modifier,
    onClick: () -> Unit,
    birincil: Boolean = false,
) {
    val renk = EymenRenk
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EymenBosluk.s),
    ) {
        Box(
            modifier = Modifier.size(EymenBoyut.aksiyonKutusu).clip(EymenSekil.aksiyon)
                .then(
                    if (birincil) Modifier.background(EymenGradyan.vurgu(renk))
                    else Modifier.background(renk.yuzey1)
                ),
            contentAlignment = Alignment.Center,
        ) {
            EymenIkonu(ikon, baslik, if (birincil) renk.vurguUstu else renk.metin1)
        }
        Text(baslik, style = EymenYazi.kucuk, color = renk.metin2, maxLines = 1)
    }
}

@Composable
private fun EylemSatiri(
    ikon: EymenIkon,
    baslik: String,
    renk: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = EymenBoyut.anaButon)
            .clickable(onClick = onClick).padding(horizontal = EymenBosluk.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
    ) {
        EymenIkonu(ikon, baslik, renk)
        Text(baslik, style = EymenYazi.isim, color = renk)
    }
}

@Preview(name = "Koyu", showBackground = true)
@Composable
private fun KisiDetayKoyuOnizleme() {
    val kisi = Kisi(
        id = 1L, lookupKey = "ornek", ad = "Ayşe Demir",
        numaralar = listOf(
            Numara("05339876543", NumaraTuru.MOBIL, varsayilan = true),
            Numara("02124441020", NumaraTuru.IS),
        ),
        favori = true, sirket = "Atasoy Yazılım", eposta = "ornek@example.com",
    )
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        KisiDetayIcerik(
            KisiDetayUiState(kisi = kisi, numara = "05339876543", yukleniyor = false),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
        )
    }
}

@Preview(name = "Açık", showBackground = true)
@Composable
private fun KisiDetayAcikOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        KisiDetayIcerik(
            KisiDetayUiState(numara = "05321234567", yukleniyor = false),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
        )
    }
}
