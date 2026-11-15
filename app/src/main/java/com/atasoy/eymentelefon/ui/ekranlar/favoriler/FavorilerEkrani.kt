package com.atasoy.eymentelefon.ui.ekranlar.favoriler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.ui.bilesenler.IosAvatar
import com.atasoy.eymentelefon.ui.bilesenler.IosBilgiButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosBuyukBaslik
import com.atasoy.eymentelefon.ui.bilesenler.IosNavCubugu
import com.atasoy.eymentelefon.ui.bilesenler.IosNavIkonu
import com.atasoy.eymentelefon.ui.bilesenler.IosSatir
import com.atasoy.eymentelefon.ui.bilesenler.rememberKucukBaslikGorunur
import com.atasoy.eymentelefon.ui.ekranlar.numaraTuruMetni
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi

/** 03 · Favoriler — satıra dokun: ara · ⓘ: kişi kartı · uzun bas: favorilerden çıkar. */
@Composable
fun FavorilerEkrani(
    onKisiDetay: (Long, String?) -> Unit,
    onAyarlar: () -> Unit,
    onKisiler: () -> Unit,
    vm: FavorilerViewModel = viewModel(factory = FavorilerViewModel.Fabrika),
) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    val liste = rememberLazyListState()
    val kucuk by rememberKucukBaslikGorunur(liste)
    val baslik = stringResource(R.string.favoriler)
    Column(Modifier.fillMaxSize()) {
        IosNavCubugu(
            kucukBaslik = baslik,
            baslikGorunur = kucuk,
            sol = { IosNavIkonu(EymenIkon.Disli, stringResource(R.string.ae_ayarlar), onAyarlar) },
            sag = { IosNavIkonu(EymenIkon.Arti, stringResource(R.string.ae_ekle), onKisiler) },
        )
        LazyColumn(state = liste, modifier = Modifier.fillMaxSize()) {
            item { IosBuyukBaslik(baslik) }
            if (!durum.yukleniyor && durum.kisiler.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(EymenBosluk.dev), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.favori_bos), style = EymenYazi.baslik2, color = EymenRenk.etiket)
                        Text(
                            stringResource(R.string.favori_bos_metin),
                            style = EymenYazi.altBaslik,
                            color = EymenRenk.ikincilEtiket,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = EymenBosluk.s),
                        )
                    }
                }
            }
            items(durum.kisiler, key = { it.id }) { k ->
                val n = k.varsayilanNumara
                IosSatir(
                    baslik = AnnotatedString(k.ad),
                    kalin = true,
                    modifier = Modifier.padding(start = EymenBosluk.kenar),
                    minYukseklik = EymenBoyut.satirAvatarli,
                    ayirici = k != durum.kisiler.last(),
                    sol = { IosAvatar(k.basHarfler, EymenBoyut.avatarListe, fotoUri = k.fotoUri) },
                    altBaslik = n?.let { num ->
                        @Composable {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs)) {
                                EymenIkonu(EymenIkon.Telefon, null, EymenRenk.ikincilEtiket, boyut = EymenBoyut.ikonMini)
                                Text(numaraTuruMetni(num), style = EymenYazi.altBaslik, color = EymenRenk.ikincilEtiket)
                            }
                        }
                    },
                    sag = { IosBilgiButonu(stringResource(R.string.ae_bilgi)) { onKisiDetay(k.id, null) } },
                    onTikla = { vm.ara(k) },
                    onUzunBas = { vm.favoridenCikar(k) },
                )
            }
            item { Box(Modifier.padding(bottom = EymenBosluk.xl)) }
        }
    }
}
