package com.atasoy.eymentelefon.ui.ekranlar

// Ekranların ortak metin yardımcıları.

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.veri.model.AramaTuru
import com.atasoy.eymentelefon.veri.model.Numara
import com.atasoy.eymentelefon.veri.model.NumaraTuru

@Composable
fun numaraTuruMetni(n: Numara): String = n.ozelEtiket?.takeIf { it.isNotBlank() } ?: stringResource(
    when (n.tur) {
        NumaraTuru.MOBIL -> R.string.numara_mobil
        NumaraTuru.EV -> R.string.numara_ev
        NumaraTuru.IS -> R.string.numara_is
        NumaraTuru.DIGER -> R.string.numara_diger
    },
)

@Composable
fun aramaTuruMetni(t: AramaTuru): String = stringResource(
    when (t) {
        AramaTuru.GELEN -> R.string.arama_gelen
        AramaTuru.GIDEN -> R.string.arama_giden
        AramaTuru.CEVAPSIZ -> R.string.arama_cevapsiz
        AramaTuru.REDDEDILEN -> R.string.arama_reddedilen
        AramaTuru.ENGELLENEN -> R.string.arama_engellenen
        AramaTuru.SESLI_MESAJ -> R.string.arama_sesli_mesaj
    },
)

/** iOS kişi listesi: soyad kalın ("Ahmet **Yılmaz**"). Tek kelimeyse tamamı kalın. */
fun kisiAdiMetni(ad: String): AnnotatedString = buildAnnotatedString {
    val i = ad.trim().lastIndexOf(' ')
    if (i <= 0) {
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(ad) }
    } else {
        append(ad.substring(0, i + 1))
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(ad.substring(i + 1)) }
    }
}

/** Aramada eşleşen kısım kalın (iOS gibi). */
fun vurgula(metin: String, sorgu: String): AnnotatedString {
    val aralik = TurkceMetin.eslesmeAraligi(metin, sorgu) ?: return AnnotatedString(metin)
    return buildAnnotatedString {
        append(metin.substring(0, aralik.first))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(metin.substring(aralik.first, aralik.last + 1)) }
        append(metin.substring(aralik.last + 1))
    }
}
