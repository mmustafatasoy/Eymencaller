package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.navigasyon.AnaSekme
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

@Composable
fun EymenAltMenu(secili: AnaSekme, onSec: (AnaSekme) -> Unit, modifier: Modifier = Modifier) {
    val r = EymenRenk
    val sekmeler = listOf(
        Triple(AnaSekme.FAVORILER, R.string.sekme_favoriler, EymenIkon.Yildiz),
        Triple(AnaSekme.SON, R.string.sekme_son, EymenIkon.Saat),
        Triple(AnaSekme.REHBER, R.string.sekme_rehber, EymenIkon.Kisiler),
        Triple(AnaSekme.TUSLAR, R.string.sekme_tuslar, EymenIkon.Tuslar),
    )
    Row(modifier = modifier.fillMaxWidth().height(EymenBoyut.altMenu).background(r.yuzey1), verticalAlignment = Alignment.CenterVertically) {
        sekmeler.forEach { (sekme, baslik, ikon) ->
            val aktif = secili == sekme
            Column(
                modifier = Modifier.weight(1f).clickable { onSec(sekme) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(EymenBosluk.xs),
            ) {
                Box(
                    modifier = Modifier.size(EymenBoyut.altMenuHapGenislik, EymenBoyut.altMenuHapYukseklik)
                        .clip(EymenSekil.hap).then(if (aktif) Modifier.background(r.vurgu) else Modifier),
                    contentAlignment = Alignment.Center,
                ) {
                    EymenIkonu(ikon, stringResource(baslik), if (aktif) r.vurguUstu else r.metin3, boyut = EymenBoyut.ikonKucuk)
                }
                Text(stringResource(baslik), color = if (aktif) r.metin1 else r.metin3, style = EymenYazi.altMenu)
            }
        }
    }
}

@Preview(name = "Koyu")
@Composable private fun EymenAltMenuKoyu() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) { EymenAltMenu(AnaSekme.SON, {}) }
}
@Preview(name = "Açık")
@Composable private fun EymenAltMenuAcik() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) { EymenAltMenu(AnaSekme.TUSLAR, {}) }
}
