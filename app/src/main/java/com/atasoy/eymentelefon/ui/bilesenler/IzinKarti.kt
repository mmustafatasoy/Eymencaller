package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.izin.IzinTanimi
import com.atasoy.eymentelefon.cekirdek.izin.IzinListesi
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

enum class IzinKartiDurumu { VERILDI, ISTENMEDI, ROL_YOK, AYARLARDAN_AC }

@Composable
fun IzinKarti(tanim: IzinTanimi, durum: IzinKartiDurumu, onIzinIste: () -> Unit, modifier: Modifier = Modifier) {
    val r = EymenRenk
    val verildi = durum == IzinKartiDurumu.VERILDI
    val durumMetni = when (durum) {
        IzinKartiDurumu.VERILDI -> R.string.izin_verildi
        IzinKartiDurumu.ISTENMEDI -> R.string.izin_ver
        IzinKartiDurumu.ROL_YOK -> R.string.izin_ayarla
        IzinKartiDurumu.AYARLARDAN_AC -> R.string.izin_ayarlardan_ac
    }
    Row(
        modifier = modifier.fillMaxWidth().background(r.yuzey1, EymenSekil.izinKarti)
            .then(if (verildi) Modifier.border(EymenBoyut.odakCercevesi, r.vurgu.copy(alpha = .36f), EymenSekil.izinKarti) else Modifier)
            .padding(EymenBosluk.m),
        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s), verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(EymenBoyut.izinIkonKutusu).background(r.yuzey2, EymenSekil.ikonKutusu), contentAlignment = Alignment.Center) {
            EymenIkonu(tanim.ikon, aciklama = null, renk = r.vurgu, boyut = EymenBoyut.ikonKucuk)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(EymenBosluk.xs)) {
            Row(horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(tanim.baslik), color = r.metin1, style = EymenYazi.isim, modifier = Modifier.weight(1f))
                EymenCip(stringResource(if (tanim.zorunlu) R.string.izin_zorunlu else R.string.izin_istege_bagli), tur = EymenCipTuru.ETIKET)
            }
            Text(stringResource(tanim.aciklama), color = r.metin3, style = EymenYazi.kucuk)
        }
        EymenCip(stringResource(durumMetni), olumlu = verildi, onClick = if (verildi) null else onIzinIste)
    }
}

@Preview(name = "Koyu")
@Composable private fun IzinKartiKoyu() { EymenTelefonTemasi(tema = TemaSecimi.KOYU) { IzinKarti(IzinListesi.tumu[0], IzinKartiDurumu.ROL_YOK, {}) } }
@Preview(name = "Açık")
@Composable private fun IzinKartiAcik() { EymenTelefonTemasi(tema = TemaSecimi.ACIK) { IzinKarti(IzinListesi.tumu[1], IzinKartiDurumu.VERILDI, {}) } }
