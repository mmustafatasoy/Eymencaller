package com.atasoy.eymentelefon.gorusme.ui

// 11–16 · iOS tarzı arama ekranları. Her temada koyu; zemin kişiye göre renk alır.
// Giden arama ve görüşme AYNI düzendir (iOS gibi): üstte ad + durum, altta 3×2 buton.

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.ZamanBicimleyici
import com.atasoy.eymentelefon.gorusme.GorusmeAsamasi
import com.atasoy.eymentelefon.gorusme.GorusmeDurumu
import com.atasoy.eymentelefon.gorusme.SesYolu
import com.atasoy.eymentelefon.ui.bilesenler.IosCagriButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosEylemSayfasi
import com.atasoy.eymentelefon.ui.bilesenler.IosKaydirarakCevapla
import com.atasoy.eymentelefon.ui.bilesenler.IosRenkliCagriButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosTusTakimi
import com.atasoy.eymentelefon.ui.bilesenler.SayfaSecenegi
import com.atasoy.eymentelefon.ui.bilesenler.sessizTiklama
import com.atasoy.eymentelefon.ui.bilesenler.altGuvenli
import com.atasoy.eymentelefon.ui.bilesenler.ustGuvenli
import com.atasoy.eymentelefon.ui.ekranlar.numaraTuruMetni
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.Kisi

/** Kişiye göre koyu radyal zemin; içerik güvenli alanda (kamera deliği / gezinme tuşları). */
@Composable
fun AramaZemini(ad: String, icerik: @Composable BoxScope.() -> Unit) {
    val (ust, orta, alt) = AramaEkraniRenk.palet(ad)
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(alt)
                drawRect(
                    Brush.radialGradient(
                        listOf(ust, orta, alt),
                        center = Offset(size.width / 2, size.height * UST_ISIK),
                        radius = size.height * ISIK_YARICAP,
                    ),
                )
            }
            .ustGuvenli()
            .altGuvenli(),
        content = icerik,
    )
}

private const val UST_ISIK = 0.2f
private const val ISIK_YARICAP = 0.65f

@Composable
fun gorunenAd(g: GorusmeDurumu?, kisi: Kisi?): String =
    kisi?.ad ?: g?.sistemAdi ?: g?.numara?.takeIf { it.isNotBlank() }?.let(NumaraBicimleyici::bicimle)
        ?: stringResource(R.string.gorusme_bilinmeyen)

/** Ad altındaki satır: rehberde varsa "mobil", yoksa numara. */
@Composable
fun numaraSatiri(g: GorusmeDurumu?, kisi: Kisi?): String {
    val n = g?.numara.orEmpty()
    val tur = kisi?.numaralar?.firstOrNull { NumaraBicimleyici.ayniMi(it.numara, n) }?.let { numaraTuruMetni(it) }
    return tur ?: if (kisi == null && g?.sistemAdi != null) NumaraBicimleyici.bicimle(n) else ""
}

@Composable
private fun UstBilgi(ad: String, durum: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(horizontal = EymenBosluk.xl), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(ad, style = EymenYazi.aramaIsim, color = AramaEkraniRenk.beyaz, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        if (durum.isNotBlank()) {
            Text(durum, style = EymenYazi.aramaDurum, color = AramaEkraniRenk.ikincil, maxLines = 1, modifier = Modifier.padding(top = EymenBosluk.xs + EymenBosluk.cokKucuk))
        }
    }
}

/** 11 · Giden arama  ·  12 · Görüşme  ·  13 · Görüşmede tuş takımı */
@Composable
fun GorusmeEkrani(durum: GorusmeUiState, onOlay: (GorusmeOlay) -> Unit, onEkle: () -> Unit) {
    val g = durum.gorusme
    val ad = gorunenAd(g, durum.kisi)
    val durumMetni = when (g?.asama) {
        GorusmeAsamasi.ARANIYOR -> stringResource(R.string.gorusme_araniyor)
        GorusmeAsamasi.BAGLANIYOR -> stringResource(R.string.gorusme_baglaniyor)
        GorusmeAsamasi.BEKLEMEDE -> stringResource(R.string.gorusme_beklemede)
        GorusmeAsamasi.BITTI -> stringResource(R.string.gorusme_bitti)
        else -> listOfNotNull(
            ZamanBicimleyici.sayac(durum.sure),
            if (g?.hd == true) stringResource(R.string.gorusme_hd) else null,
            g?.simSirasi?.let { stringResource(R.string.gorusme_sim, it) },
        ).joinToString(" · ")
    }
    AramaZemini(ad) {
        AnimatedContent(durum.tusTakimiAcik, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "tuslar") { tuslar ->
            if (tuslar) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(EymenBosluk.xxl))
                    UstBilgi(durum.dtmf.ifEmpty { " " }, "$ad · $durumMetni")
                    Spacer(Modifier.weight(1f))
                    IosTusTakimi(
                        onTus = { onOlay(GorusmeOlay.Dtmf(it)) },
                        onUzunBas = { onOlay(GorusmeOlay.Dtmf(it)) },
                        zemin = AramaEkraniRenk.butonZemini,
                        basili = AramaEkraniRenk.butonBasili,
                        yazi = AramaEkraniRenk.beyaz,
                    )
                    Spacer(Modifier.height(EymenBosluk.xl))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(EymenBosluk.tusYatay)) {
                        Spacer(Modifier.size(EymenBoyut.tus))
                        IosRenkliCagriButonu(AramaEkraniRenk.bitir, stringResource(R.string.kontrol_bitir), { onOlay(GorusmeOlay.Kapat) })
                        Box(Modifier.size(EymenBoyut.tus), contentAlignment = Alignment.Center) {
                            Text(
                                stringResource(R.string.kontrol_gizle),
                                style = EymenYazi.govde,
                                color = AramaEkraniRenk.beyaz,
                                modifier = Modifier.sessizTiklama { onOlay(GorusmeOlay.TusTakimi) }.padding(EymenBosluk.s),
                            )
                        }
                    }
                    Spacer(Modifier.height(EymenBosluk.xxl))
                }
            } else {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(EymenBosluk.dev + EymenBosluk.xl))
                    UstBilgi(ad, durumMetni)
                    Spacer(Modifier.weight(1f))
                    KontrolIzgarasi(durum, onOlay, onEkle)
                    Spacer(Modifier.height(EymenBosluk.dev + EymenBosluk.s))
                }
            }
        }
    }

    if (durum.sesMenusuAcik) {
        val secenekler = buildList {
            add(SayfaSecenegi(stringResource(R.string.ses_telefon), { onOlay(GorusmeOlay.SesYoluSec(SesYolu.TELEFON)) }))
            add(SayfaSecenegi(stringResource(R.string.ses_hoparlor), { onOlay(GorusmeOlay.SesYoluSec(SesYolu.HOPARLOR)) }))
            if (durum.ses.bluetoothVar) add(SayfaSecenegi(stringResource(R.string.ses_bluetooth), { onOlay(GorusmeOlay.SesYoluSec(SesYolu.BLUETOOTH)) }))
            if (durum.ses.kulaklikVar) add(SayfaSecenegi(stringResource(R.string.ses_kulaklik), { onOlay(GorusmeOlay.SesYoluSec(SesYolu.KULAKLIK)) }))
        }
        IosEylemSayfasi(stringResource(R.string.ses_kaynagi), secenekler, stringResource(R.string.vazgec)) { onOlay(GorusmeOlay.SesMenusu(false)) }
    }
}

@Composable
private fun KontrolIzgarasi(durum: GorusmeUiState, onOlay: (GorusmeOlay) -> Unit, onEkle: () -> Unit) {
    val g = durum.gorusme
    val aktifGorusme = g?.asama == GorusmeAsamasi.AKTIF || g?.asama == GorusmeAsamasi.BEKLEMEDE
    Column(verticalArrangement = Arrangement.spacedBy(EymenBosluk.cagriDikey)) {
        Row(horizontalArrangement = Arrangement.spacedBy(EymenBosluk.cagriYatay)) {
            Hucre {
                if (durum.ses.bluetoothVar || durum.ses.kulaklikVar) {
                    IosCagriButonu(EymenIkon.Bluetooth, stringResource(R.string.kontrol_ses), { onOlay(GorusmeOlay.SesMenusu(true)) }, aktif = durum.ses.yol != SesYolu.TELEFON)
                } else {
                    IosCagriButonu(EymenIkon.Hoparlor, stringResource(R.string.kontrol_hoparlor), { onOlay(GorusmeOlay.Hoparlor) }, aktif = durum.ses.yol == SesYolu.HOPARLOR)
                }
            }
            Hucre {
                IosCagriButonu(
                    EymenIkon.Beklet, stringResource(R.string.kontrol_beklet), { onOlay(GorusmeOlay.Beklet) },
                    aktif = g?.asama == GorusmeAsamasi.BEKLEMEDE, etkin = aktifGorusme && g?.beklemeDesteklenir == true,
                )
            }
            Hucre { IosCagriButonu(EymenIkon.Sessiz, stringResource(R.string.kontrol_sessiz), { onOlay(GorusmeOlay.Sessiz) }, aktif = durum.ses.sessiz) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(EymenBosluk.cagriYatay)) {
            Hucre { IosCagriButonu(EymenIkon.Arti, stringResource(R.string.kontrol_ekle), onEkle, etkin = aktifGorusme) }
            Hucre { IosRenkliCagriButonu(AramaEkraniRenk.bitir, stringResource(R.string.kontrol_bitir), { onOlay(GorusmeOlay.Kapat) }, etiket = stringResource(R.string.kontrol_bitir)) }
            Hucre { IosCagriButonu(EymenIkon.Tuslar, stringResource(R.string.kontrol_tuslar), { onOlay(GorusmeOlay.TusTakimi) }) }
        }
    }
}

@Composable
private fun Hucre(icerik: @Composable () -> Unit) {
    Box(Modifier.width(EymenBoyut.cagriButonu), contentAlignment = Alignment.TopCenter) { icerik() }
}

/** 14 · Gelen arama (kilit açık: Reddet / Kabul Et)  ·  15 · kilitli: kaydırarak cevapla  ·  16 · mesajla yanıtla */
@Composable
fun GelenAramaEkrani(durum: GorusmeUiState, kilitli: Boolean, onOlay: (GorusmeOlay) -> Unit, onHatirlat: () -> Unit) {
    val g = durum.gorusme
    val ad = gorunenAd(g, durum.kisi)
    val alt = listOfNotNull(numaraSatiri(g, durum.kisi).takeIf { it.isNotBlank() }, g?.simSirasi?.let { stringResource(R.string.gorusme_sim, it) })
        .joinToString(" · ")
    AramaZemini(ad) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(EymenBosluk.dev + EymenBosluk.xl))
            UstBilgi(ad, alt)
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth().padding(horizontal = EymenBosluk.dev), horizontalArrangement = Arrangement.SpaceBetween) {
                KucukEylem(EymenIkon.Alarm, stringResource(R.string.gelen_hatirlat), onHatirlat)
                KucukEylem(EymenIkon.MesajDolu, stringResource(R.string.gelen_mesaj)) { onOlay(GorusmeOlay.YanitMenusu(true)) }
            }
            Spacer(Modifier.height(EymenBosluk.xxl))
            if (kilitli) {
                IosKaydirarakCevapla(
                    stringResource(R.string.gelen_kaydir),
                    stringResource(R.string.gelen_kabul),
                    { onOlay(GorusmeOlay.Cevapla) },
                    Modifier.padding(horizontal = EymenBosluk.xl),
                )
            } else {
                Row(Modifier.fillMaxWidth().padding(horizontal = EymenBosluk.dev - EymenBosluk.cokKucuk), horizontalArrangement = Arrangement.SpaceBetween) {
                    IosRenkliCagriButonu(AramaEkraniRenk.bitir, stringResource(R.string.gelen_reddet), { onOlay(GorusmeOlay.Reddet) }, etiket = stringResource(R.string.gelen_reddet), boyut = EymenBoyut.cevapButonu)
                    IosRenkliCagriButonu(AramaEkraniRenk.kabul, stringResource(R.string.gelen_kabul), { onOlay(GorusmeOlay.Cevapla) }, kapatma = false, etiket = stringResource(R.string.gelen_kabul), boyut = EymenBoyut.cevapButonu)
                }
            }
            Spacer(Modifier.height(EymenBosluk.dev + EymenBosluk.xs))
        }
    }

    if (durum.yanitMenusuAcik) {
        IosEylemSayfasi(
            baslik = stringResource(R.string.mesajla_yanitla),
            secenekler = durum.hizliYanitlar.map { m -> SayfaSecenegi(m, { onOlay(GorusmeOlay.MesajlaReddet(m)) }) },
            vazgecMetni = stringResource(R.string.vazgec),
            onKapat = { onOlay(GorusmeOlay.YanitMenusu(false)) },
        )
    }
}

@Composable
private fun KucukEylem(ikon: EymenIkon, metin: String, onTikla: () -> Unit) {
    Column(
        Modifier.sessizTiklama(onTikla = onTikla).padding(EymenBosluk.s),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EymenBosluk.xs + EymenBosluk.cokKucuk),
    ) {
        EymenIkonu(ikon, metin, AramaEkraniRenk.beyaz, boyut = EymenBoyut.sekmeIkonu)
        Text(metin, style = EymenYazi.dipnot, color = AramaEkraniRenk.beyaz)
    }
}
