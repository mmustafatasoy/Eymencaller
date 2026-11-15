package com.atasoy.eymentelefon.ui.ekranlar.anaekran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.EymenAltMenu
import com.atasoy.eymentelefon.ui.ekranlar.tuslar.TusTakimiEkrani
import com.atasoy.eymentelefon.ui.ekranlar.sonaramalar.SonAramalarEkrani
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import com.atasoy.eymentelefon.ui.navigasyon.AnaSekme
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

/** GOREV-04: 4 sekme iskeleti. Diğer üç sekmenin gerçek listeleri GOREV-06/07'de yazılacak. */
@Composable
fun AnaEkran(
    baslangicSekme: AnaSekme,
    baslangicNumara: String,
    onAramaAc: () -> Unit,
    onDetay: (AramaKaydi) -> Unit,
) {
    val ayarlar = LocalContext.current.konteyner().ayarDeposu
    var secili by rememberSaveable { mutableStateOf(baslangicSekme) }
    LaunchedEffect(baslangicNumara) {
        if (baslangicNumara.isNotBlank()) secili = AnaSekme.TUSLAR
    }
    LaunchedEffect(secili) { ayarlar.sonSekme(secili.name) }
    AnaIcerik(
        secili = secili,
        baslangicNumara = baslangicNumara,
        onSec = { secili = it },
        onAramaAc = onAramaAc,
        onDetay = onDetay,
    )
}

@Composable
fun AnaIcerik(
    secili: AnaSekme,
    baslangicNumara: String,
    onSec: (AnaSekme) -> Unit,
    onAramaAc: () -> Unit,
    onDetay: (AramaKaydi) -> Unit,
) {
    Column(
        Modifier.fillMaxSize().background(EymenRenk.arkaPlan).safeDrawingPadding(),
    ) {
        Box(Modifier.weight(1f).fillMaxSize()) {
            when (secili) {
                AnaSekme.TUSLAR -> TusTakimiEkrani(
                    baslangicNumara = baslangicNumara,
                    onAramaAc = onAramaAc,
                )
                AnaSekme.SON -> SonAramalarEkrani(
                    onAramaEkrani = onAramaAc,
                    onDetay = onDetay,
                    onTuslar = { onSec(AnaSekme.TUSLAR) },
                )
                AnaSekme.REHBER -> BekleyenSekme(R.string.rehber_bos)
                AnaSekme.FAVORILER -> BekleyenSekme(R.string.favori_bos)
            }
        }
        EymenAltMenu(secili = secili, onSec = onSec)
    }
}

@Composable
private fun BekleyenSekme(metin: Int) {
    Box(Modifier.fillMaxSize().padding(EymenBosluk.kenar), contentAlignment = Alignment.Center) {
        Text(stringResource(metin), style = EymenYazi.govde, color = EymenRenk.metin3)
    }
}

@Preview(name = "Koyu")
@Composable private fun AnaEkranKoyu() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        AnaIcerik(AnaSekme.SON, "", {}, {}, {})
    }
}

@Preview(name = "Açık")
@Composable private fun AnaEkranAcik() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        AnaIcerik(AnaSekme.REHBER, "", {}, {}, {})
    }
}
