package com.atasoy.eymentelefon.ui.bilesenler

// iOS sekme çubuğu: yarı saydam zemin gezinme tuşlarının ARKASINA uzanır, ikonlar güvenli alanda kalır.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.navigasyon.AnaSekme
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi

private val sekmeler = listOf(
    Triple(AnaSekme.FAVORILER, EymenIkon.YildizDolu, R.string.sekme_favoriler),
    Triple(AnaSekme.SON, EymenIkon.SaatDolu, R.string.sekme_son),
    Triple(AnaSekme.REHBER, EymenIkon.KisiDolu, R.string.sekme_rehber),
    Triple(AnaSekme.TUSLAR, EymenIkon.Tuslar, R.string.sekme_tuslar),
)

@Composable
fun IosSekmeCubugu(secili: AnaSekme, onSec: (AnaSekme) -> Unit, modifier: Modifier = Modifier) {
    val r = EymenRenk
    Column(modifier.fillMaxWidth().background(r.sekmeCubugu)) {
        HorizontalDivider(thickness = EymenBoyut.hairline, color = r.ayirici)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .altGuvenli()
                .yatayGuvenli()
                .height(EymenBoyut.sekmeCubugu),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top,
        ) {
            sekmeler.forEach { (sekme, ikon, metin) ->
                val aktif = sekme == secili
                val renk = if (aktif) r.vurgu else r.gri
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .semantics { role = Role.Tab; selected = aktif }
                        .sessizTiklama { onSec(sekme) }
                        .padding(top = EymenBosluk.s - EymenBosluk.cokKucuk / 2),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(EymenBosluk.cokKucuk),
                ) {
                    EymenIkonu(ikon, null, renk, boyut = EymenBoyut.sekmeIkonu)
                    Text(stringResource(metin), style = EymenYazi.sekme, color = renk, maxLines = 1)
                }
            }
        }
    }
}
