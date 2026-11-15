package com.atasoy.eymentelefon.ui.ekranlar.favoriler

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.EymenAramaCubugu
import com.atasoy.eymentelefon.ui.bilesenler.EymenButon
import com.atasoy.eymentelefon.ui.bilesenler.EymenButonTuru
import com.atasoy.eymentelefon.ui.bilesenler.KisiAvatari
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.model.Numara
import com.atasoy.eymentelefon.veri.model.NumaraTuru

/** Aramaya dokunmak çağrı başlatır; uzun basmak kişiyi açar. */
@Composable
fun FavorilerEkrani(
    onAramaAc: () -> Unit,
    onKisiDetay: (Kisi) -> Unit,
    onRehbereGit: () -> Unit,
    vm: FavorilerViewModel = viewModel(factory = FavorilerViewModel.Fabrika),
) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    val context = LocalContext.current
    FavorilerIcerik(
        durum = durum,
        onAramaAc = onAramaAc,
        onKisiDetay = onKisiDetay,
        onRehbereGit = onRehbereGit,
        onFavoriAra = { kisi ->
            val numara = kisi.varsayilanNumara?.numara
            if (numara.isNullOrBlank()) onKisiDetay(kisi)
            else context.konteyner().aramaBaslatici.ara(numara)
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavorilerIcerik(
    durum: FavorilerUiState,
    onAramaAc: () -> Unit,
    onKisiDetay: (Kisi) -> Unit,
    onRehbereGit: () -> Unit,
    onFavoriAra: (Kisi) -> Unit,
) {
    val renk = EymenRenk
    Column(
        modifier = Modifier.fillMaxSize().background(renk.arkaPlan)
            .padding(horizontal = EymenBosluk.kenar),
        verticalArrangement = Arrangement.spacedBy(EymenBosluk.l),
    ) {
        Box(Modifier.fillMaxWidth().padding(top = EymenBosluk.l)) {
            EymenAramaCubugu(
                deger = "",
                onDegerDegisti = { onAramaAc() },
                ipucu = stringResource(R.string.ara_ipucu_rehber, durum.toplamKisi),
            )
            Box(Modifier.matchParentSize().clickable(onClick = onAramaAc))
        }

        Text(
            stringResource(R.string.sekme_favoriler),
            style = EymenYazi.baslik1,
            color = renk.metin1,
        )

        if (durum.kisiler.isEmpty() && !durum.yukleniyor) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                EymenIkonu(
                    EymenIkon.Yildiz, aciklama = null, renk = renk.metin3,
                    boyut = EymenBoyut.fab,
                )
                Text(
                    text = stringResource(R.string.favori_bos),
                    modifier = Modifier.padding(top = EymenBosluk.l),
                    style = EymenYazi.baslik2,
                    color = renk.metin2,
                )
                Box(Modifier.fillMaxWidth().padding(top = EymenBosluk.l)) {
                    EymenButon(
                        metin = stringResource(R.string.favori_rehberden_ekle),
                        onClick = onRehbereGit,
                        tur = EymenButonTuru.IKINCIL,
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(bottom = EymenBosluk.xl),
                verticalArrangement = Arrangement.spacedBy(EymenBosluk.xl),
                horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s),
            ) {
                items(durum.kisiler, key = Kisi::id) { kisi ->
                    Column(
                        modifier = Modifier.fillMaxWidth().combinedClickable(
                            onClick = { onFavoriAra(kisi) },
                            onLongClick = { onKisiDetay(kisi) },
                        ).padding(vertical = EymenBosluk.s),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(EymenBosluk.s),
                    ) {
                        KisiAvatari(
                            ad = kisi.ad,
                            fotoUri = kisi.fotoUri,
                            boyut = EymenBoyut.avatarFavoriIzgara,
                        )
                        Text(
                            text = kisi.ad,
                            style = EymenYazi.isim,
                            color = renk.metin1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                        val tur = when (kisi.varsayilanNumara?.tur) {
                            NumaraTuru.MOBIL -> R.string.numara_mobil
                            NumaraTuru.EV -> R.string.numara_ev
                            NumaraTuru.IS -> R.string.numara_is
                            NumaraTuru.DIGER, null -> R.string.numara_diger
                        }
                        Text(
                            stringResource(tur),
                            style = EymenYazi.kucuk,
                            color = renk.metin3,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Koyu", showBackground = true)
@Composable
private fun FavorilerKoyuOnizleme() {
    val numara = listOf(Numara("05321234567", NumaraTuru.MOBIL))
    val ornekler = listOf(
        Kisi(1L, "ayse", "Ayşe Demir", numara),
        Kisi(2L, "burak", "Burak Arslan", numara),
        Kisi(3L, "cagla", "Çağla Yılmaz", numara),
    )
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        FavorilerIcerik(
            FavorilerUiState(kisiler = ornekler, toplamKisi = 3, yukleniyor = false),
            onAramaAc = {}, onKisiDetay = {}, onRehbereGit = {}, onFavoriAra = {},
        )
    }
}

@Preview(name = "Açık", showBackground = true)
@Composable
private fun FavorilerAcikOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        FavorilerIcerik(
            FavorilerUiState(yukleniyor = false),
            onAramaAc = {}, onKisiDetay = {}, onRehbereGit = {}, onFavoriAra = {},
        )
    }
}
