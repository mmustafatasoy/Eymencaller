package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.AramaTuru
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AramaSatiri(
    ad: String,
    altBilgi: String,
    saat: String,
    tur: AramaTuru,
    tekrar: Int,
    onAc: () -> Unit,
    onAra: () -> Unit,
    onUzunBas: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val r = EymenRenk
    val olumsuz = tur == AramaTuru.CEVAPSIZ || tur == AramaTuru.REDDEDILEN || tur == AramaTuru.ENGELLENEN
    val ikon = if (tur == AramaTuru.GIDEN) EymenIkon.Giden else EymenIkon.Gelen
    val isaretRengi = when {
        olumsuz -> r.cevapsiz
        tur == AramaTuru.GIDEN -> r.vurgu2
        else -> r.vurgu
    }
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = EymenBoyut.dokunmaMin).combinedClickable(onClick = onAc, onLongClick = onUzunBas),
        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m), verticalAlignment = Alignment.CenterVertically,
    ) {
        KisiAvatari(ad, boyut = EymenBoyut.avatarAramaListe)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(EymenBosluk.xs)) {
            Text(if (tekrar > 1) "$ad ($tekrar)" else ad, color = if (olumsuz) r.cevapsiz else r.metin1, style = EymenYazi.isim, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs), verticalAlignment = Alignment.CenterVertically) {
                EymenIkonu(ikon, aciklama = null, renk = isaretRengi, boyut = EymenBoyut.ikonKucuk)
                Text(altBilgi, color = r.metin3, style = EymenYazi.kucuk, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Text(saat, color = r.metin3, style = EymenYazi.kucuk)
        Box(modifier = Modifier.size(EymenBoyut.dokunmaMin).clickable(onClick = onAra), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(EymenBoyut.listeAraButonu).background(r.araYesil.copy(alpha = .18f), CircleShape), contentAlignment = Alignment.Center) {
                EymenIkonu(EymenIkon.Telefon, aciklama = stringResource(R.string.ae_ara), renk = r.araYesil, boyut = EymenBoyut.ikonKucuk)
            }
        }
    }
}

@Preview(name = "Koyu")
@Composable private fun AramaSatiriKoyu() { EymenTelefonTemasi(tema = TemaSecimi.KOYU) { AramaSatiri("Ayşe Demir", "Mobil", "20:31", AramaTuru.GELEN, 1, {}, {}, {}) } }
@Preview(name = "Açık")
@Composable private fun AramaSatiriAcik() { EymenTelefonTemasi(tema = TemaSecimi.ACIK) { AramaSatiri("Mehmet Çelik", "Mobil", "19:05", AramaTuru.CEVAPSIZ, 2, {}, {}, {}) } }
