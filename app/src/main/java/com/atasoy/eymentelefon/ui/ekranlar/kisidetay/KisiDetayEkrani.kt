package com.atasoy.eymentelefon.ui.ekranlar.kisidetay

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.atasoy.eymentelefon.konteyner
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.produceState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.MainActivity
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.yardimci.DisIslemler
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.ZamanBicimleyici
import com.atasoy.eymentelefon.ui.bilesenler.IosAvatar
import com.atasoy.eymentelefon.ui.bilesenler.IosEylemSatiri
import com.atasoy.eymentelefon.ui.bilesenler.IosEylemSayfasi
import com.atasoy.eymentelefon.ui.bilesenler.IosGeriButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosGrup
import com.atasoy.eymentelefon.ui.bilesenler.IosMetinButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosNavCubugu
import com.atasoy.eymentelefon.ui.bilesenler.IosSayfa
import com.atasoy.eymentelefon.ui.bilesenler.IosSatir
import com.atasoy.eymentelefon.ui.bilesenler.SayfaSecenegi
import com.atasoy.eymentelefon.ui.bilesenler.sessizTiklama
import com.atasoy.eymentelefon.ui.ekranlar.aramaTuruMetni
import com.atasoy.eymentelefon.ui.ekranlar.numaraTuruMetni
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenOpaklik
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.model.Numara
import com.atasoy.eymentelefon.veri.model.NumaraTuru

private const val ILK_ARAMA = 3

/** 07 · Kişi kartı (rehberde olmayan numara için de). */
@Composable
fun KisiDetayEkrani(onGeri: () -> Unit, onPoster: (String, String) -> Unit = { _, _ -> }, vm: KisiDetayViewModel = viewModel(factory = KisiDetayViewModel.Fabrika)) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val r = EymenRenk
    var ilkResume by remember { mutableStateOf(true) }
    var onay by rememberSaveable { mutableStateOf<String?>(null) } // "engel" | "sil"
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { if (ilkResume) ilkResume = false else vm.yukle() }
    LaunchedEffect(durum.silindi) { if (durum.silindi) onGeri() }

    val kisi = durum.kisi
    val ad = kisi?.ad ?: NumaraBicimleyici.bicimle(durum.numara)

    // V12-07: kişi posteri varsa kartın üstü poster
    val posterDeposu = remember { context.konteyner().posterDeposu }
    val posterSurumu by posterDeposu.degisim.collectAsStateWithLifecycle()
    val poster by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, kisi?.lookupKey, posterSurumu) {
        value = kisi?.lookupKey?.let { anahtar ->
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                runCatching { posterDeposu.oku(anahtar)?.dosya?.let { android.graphics.BitmapFactory.decodeFile(it.path)?.asImageBitmap() } }
                    .onFailure { com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici.kaydet("KisiDetay.poster", it) }
                    .getOrNull()
            }
        }
    }

    IosSayfa(zemin = r.grupluArkaPlan) {
        IosNavCubugu(
            zemin = r.grupluArkaPlan,
            sol = { IosGeriButonu(stringResource(R.string.geri), onGeri) },
            sag = {
                if (kisi != null) IosMetinButonu(stringResource(R.string.duzenle), { DisIslemler.kisiDuzenle(context, vm.kisiUri(kisi)) })
            },
        )
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            val p = poster
            if (p != null) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(EymenBoyut.kartPoster)
                        .clip(EymenSekil.sayfa)
                        .sessizTiklama { kisi?.let { onPoster(it.lookupKey, it.ad) } },
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    com.atasoy.eymentelefon.gorusme.ui.PosterFotografi(p, canli = false, bulanik = false)
                    Column(Modifier.padding(EymenBosluk.l), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(ad, style = EymenYazi.baslik1, color = com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk.beyaz, textAlign = TextAlign.Center)
                        kisi?.sirket?.let { Text(it, style = EymenYazi.dipnot, color = com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk.ikincil) }
                    }
                }
            } else Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                IosAvatar(kisi?.basHarfler ?: "#", EymenBoyut.avatarKart, fotoUri = kisi?.fotoUri)
                Text(ad, style = EymenYazi.baslik1, color = r.etiket, textAlign = TextAlign.Center, modifier = Modifier.padding(top = EymenBosluk.m, start = EymenBosluk.kenar, end = EymenBosluk.kenar))
                kisi?.sirket?.let { Text(it, style = EymenYazi.dipnot, color = r.ikincilEtiket) }
            }

            // mesaj · ara · video · posta
            Row(
                Modifier.fillMaxWidth().padding(horizontal = EymenBosluk.kenar, vertical = EymenBosluk.l),
                horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s),
            ) {
                EylemKutusu(EymenIkon.MesajDolu, stringResource(R.string.eylem_mesaj), true, Modifier.weight(1f)) { DisIslemler.mesajGonder(context, durum.numara) }
                EylemKutusu(EymenIkon.Telefon, stringResource(R.string.eylem_ara), true, Modifier.weight(1f)) { vm.ara() }
                EylemKutusu(EymenIkon.VideoDolu, stringResource(R.string.eylem_video), durum.goruntuluVar, Modifier.weight(1f)) { vm.ara(goruntulu = true) }
                EylemKutusu(EymenIkon.EpostaDolu, stringResource(R.string.eylem_posta), kisi?.eposta != null, Modifier.weight(1f)) {
                    kisi?.eposta?.let { DisIslemler.epostaGonder(context, it) }
                }
            }

            // Numaralar
            val numaralar = kisi?.numaralar?.takeIf { it.isNotEmpty() } ?: listOf(Numara(durum.numara, NumaraTuru.DIGER))
            IosGrup {
                numaralar.forEachIndexed { i, n ->
                    val etiket = if (kisi != null) numaraTuruMetni(n) else null
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .sessizTiklama { vm.ara(n.numara) }
                            .padding(top = EymenBosluk.s + EymenBosluk.cokKucuk),
                    ) {
                        if (etiket != null) Text(etiket, style = EymenYazi.dipnot, color = r.etiket)
                        IosSatir(
                            baslik = AnnotatedString(NumaraBicimleyici.bicimle(n.numara)),
                            baslikRengi = r.vurgu,
                            ayirici = i < numaralar.lastIndex,
                            minYukseklik = EymenBoyut.satir - EymenBosluk.m,
                            onTikla = { vm.ara(n.numara) },
                            onUzunBas = { DisIslemler.kopyala(context, "numara", n.numara) },
                        )
                    }
                }
            }

            // Son aramalar
            if (durum.aramalar.isNotEmpty()) {
                val gosterilen = if (durum.tumAramalar) durum.aramalar else durum.aramalar.take(ILK_ARAMA)
                IosGrup(baslik = stringResource(R.string.kart_son_aramalar)) {
                    gosterilen.forEachIndexed { i, a ->
                        val sure = if (a.sureSn > 0) " · " + ZamanBicimleyici.sure(context, a.sureSn) else ""
                        IosSatir(
                            baslik = ZamanBicimleyici.detay(context, a.tarih),
                            altBaslik = aramaTuruMetni(a.tur) + sure,
                            ayirici = i < gosterilen.lastIndex || durum.aramalar.size > ILK_ARAMA,
                        )
                    }
                    if (durum.aramalar.size > ILK_ARAMA) {
                        IosEylemSatiri(stringResource(R.string.kart_tumu), vm::tumAramalar, ayirici = false)
                    }
                }
            }

            // Eylemler
            IosGrup {
                IosEylemSatiri(stringResource(R.string.kayit_mesaj), { DisIslemler.mesajGonder(context, durum.numara) })
                if (kisi != null) {
                    IosEylemSatiri(stringResource(R.string.kart_poster), { onPoster(kisi.lookupKey, kisi.ad) })
                    IosEylemSatiri(stringResource(R.string.kart_paylas), { DisIslemler.vcardPaylas(context, vm.vcardUri(kisi)) })
                    IosEylemSatiri(stringResource(if (kisi.favori) R.string.favorilerden_cikar else R.string.favorilere_ekle), vm::favori)
                    IosEylemSatiri(stringResource(R.string.kart_kisayol), { kisayolEkle(context, kisi, durum.numara) }, ayirici = false)
                } else {
                    IosEylemSatiri(stringResource(R.string.kart_yeni_kisi), { DisIslemler.kisiyeEkle(context, durum.numara) }, ayirici = false)
                }
            }
            IosGrup {
                IosEylemSatiri(
                    stringResource(if (durum.engelli) R.string.kart_engeli_kaldir else R.string.kayit_engelle),
                    { if (durum.engelli) vm.engelDegistir() else onay = "engel" },
                    yikici = !durum.engelli,
                    ayirici = kisi != null,
                )
                if (kisi != null) IosEylemSatiri(stringResource(R.string.kart_kisiyi_sil), { onay = "sil" }, yikici = true, ayirici = false)
            }
            Spacer(Modifier.height(EymenBosluk.xxl))
        }
    }

    when (onay) {
        "engel" -> IosEylemSayfasi(
            baslik = stringResource(R.string.kart_engel_onay),
            secenekler = listOf(SayfaSecenegi(stringResource(R.string.kayit_engelle), { vm.engelDegistir(); onay = null }, yikici = true)),
            vazgecMetni = stringResource(R.string.vazgec),
            onKapat = { onay = null },
        )
        "sil" -> IosEylemSayfasi(
            baslik = stringResource(R.string.kart_sil_onay),
            secenekler = listOf(SayfaSecenegi(stringResource(R.string.kart_kisiyi_sil), { vm.sil(); onay = null }, yikici = true)),
            vazgecMetni = stringResource(R.string.vazgec),
            onKapat = { onay = null },
        )
    }
}

/** mesaj / ara / video / posta kutusu. Kullanılamıyorsa soluk. */
@Composable
private fun EylemKutusu(ikon: EymenIkon, metin: String, etkin: Boolean, modifier: Modifier, onTikla: () -> Unit) {
    val r = EymenRenk
    val renk = if (etkin) r.vurgu else r.ucunculEtiket
    Column(
        modifier
            .clip(EymenSekil.eylemKutusu)
            .background(r.yuzey)
            .sessizTiklama(etkin, onTikla)
            .padding(vertical = EymenBosluk.s + EymenBosluk.cokKucuk)
            .alpha(if (etkin) 1f else EymenOpaklik.basili + EymenOpaklik.pasif),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EymenBosluk.xs),
    ) {
        EymenIkonu(ikon, metin, renk)
        Text(metin, style = EymenYazi.etiket1, color = renk)
    }
}

private fun kisayolEkle(context: Context, kisi: Kisi, numara: String) {
    if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) return
    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", numara, null)).setClass(context, MainActivity::class.java)
    val bilgi = ShortcutInfoCompat.Builder(context, "kisi-${kisi.id}")
        .setShortLabel(kisi.ad)
        .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher))
        .setIntent(intent)
        .build()
    ShortcutManagerCompat.requestPinShortcut(context, bilgi, null)
}
