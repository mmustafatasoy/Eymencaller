package com.atasoy.eymentelefon.ui.ekranlar.arama

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.cekirdek.yardimci.ZamanBicimleyici
import com.atasoy.eymentelefon.ui.bilesenler.IosAramaAlaniAktif
import com.atasoy.eymentelefon.ui.bilesenler.IosAvatar
import com.atasoy.eymentelefon.ui.bilesenler.IosBilgiButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosBolumBasligi
import com.atasoy.eymentelefon.ui.bilesenler.IosSayfa
import com.atasoy.eymentelefon.ui.bilesenler.IosSatir
import com.atasoy.eymentelefon.ui.ekranlar.aramaTuruMetni
import com.atasoy.eymentelefon.ui.ekranlar.numaraTuruMetni
import com.atasoy.eymentelefon.ui.ekranlar.vurgula
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi

/** 10 · Arama — klavye açık gelir; kişiler + son aramalar, eşleşen kısım kalın. Satır: ara · ⓘ: kart. */
@Composable
fun AramaEkrani(
    onGeri: () -> Unit,
    onKisiDetay: (Long, String?) -> Unit,
    vm: AramaViewModel = viewModel(factory = AramaViewModel.Fabrika),
) {
    val sorgu by vm.sorgu.collectAsStateWithLifecycle()
    val sonuc by vm.sonuc.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val r = EymenRenk
    IosSayfa {
        IosAramaAlaniAktif(
            metin = sorgu,
            onMetin = vm::sorguDegisti,
            ipucu = stringResource(R.string.arama_ipucu),
            vazgecMetni = stringResource(R.string.vazgec),
            onVazgec = onGeri,
            onAra = vm::ilkSonucuAra,
        )
        val bos = sonuc.sorgu.isNotEmpty() && sonuc.kisiler.isEmpty() && sonuc.aramalar.isEmpty() && sonuc.aranabilirNumara == null
        if (bos) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.arama_sonuc_yok), style = EymenYazi.baslik3, color = r.ikincilEtiket)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                sonuc.aranabilirNumara?.let { n ->
                    item(key = "numara") {
                        IosSatir(
                            baslik = stringResource(R.string.arama_numarayi_ara, NumaraBicimleyici.bicimle(n)),
                            baslikRengi = r.vurgu,
                            modifier = Modifier.padding(start = EymenBosluk.kenar),
                            ayirici = false,
                            onTikla = { vm.ara(n) },
                        )
                    }
                }
                if (sonuc.kisiler.isNotEmpty()) {
                    item(key = "kh") { IosBolumBasligi(stringResource(R.string.arama_kisiler).uppercase(TurkceMetin.TR)) }
                    items(sonuc.kisiler, key = { "k${it.id}" }) { k ->
                        val n = k.varsayilanNumara
                        IosSatir(
                            baslik = vurgula(k.ad, sonuc.sorgu),
                            modifier = Modifier.padding(start = EymenBosluk.kenar),
                            minYukseklik = EymenBoyut.satirAvatarli,
                            ayirici = k != sonuc.kisiler.last(),
                            sol = { IosAvatar(k.basHarfler, EymenBoyut.avatarListe, fotoUri = k.fotoUri) },
                            altBaslik = n?.let { num ->
                                val metin = numaraTuruMetni(num) + " " + NumaraBicimleyici.bicimle(num.numara)
                                @Composable { Text(metin, style = EymenYazi.altBaslik, color = r.ikincilEtiket, maxLines = 1) }
                            },
                            sag = { IosBilgiButonu(stringResource(R.string.ae_bilgi)) { onKisiDetay(k.id, null) } },
                            onTikla = { n?.let { vm.ara(it.numara) } },
                        )
                    }
                }
                if (sonuc.aramalar.isNotEmpty()) {
                    item(key = "ah") { IosBolumBasligi(stringResource(R.string.arama_son).uppercase(TurkceMetin.TR)) }
                    items(sonuc.aramalar, key = { "a${it.id}" }) { a ->
                        val alt = aramaTuruMetni(a.tur) + " · " + ZamanBicimleyici.detay(context, a.tarih)
                        IosSatir(
                            baslik = vurgula(a.ad ?: NumaraBicimleyici.bicimle(a.numara), sonuc.sorgu),
                            modifier = Modifier.padding(start = EymenBosluk.kenar),
                            minYukseklik = EymenBoyut.satirAvatarli,
                            ayirici = a != sonuc.aramalar.last(),
                            sol = { IosAvatar(if (a.ad == null) "#" else TurkceMetin.basHarfler(a.ad), EymenBoyut.avatarListe) },
                            altBaslik = @Composable { Text(alt, style = EymenYazi.altBaslik, color = r.ikincilEtiket, maxLines = 1) },
                            sag = { IosBilgiButonu(stringResource(R.string.ae_bilgi)) { onKisiDetay(-1L, a.numara) } },
                            onTikla = { vm.ara(a.numara) },
                        )
                    }
                }
            }
        }
    }
}

