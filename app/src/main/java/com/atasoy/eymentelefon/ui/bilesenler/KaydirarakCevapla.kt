package com.atasoy.eymentelefon.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenGradyan
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import kotlin.math.roundToInt

@Composable
fun KaydirarakCevapla(onCevapla: () -> Unit, onReddet: () -> Unit, modifier: Modifier = Modifier) {
    val r = EymenRenk
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier.fillMaxWidth().height(EymenBoyut.kaydirRay)
        .clip(EymenSekil.hap).background(AramaEkraniRenk.rayZemini)) {
        val maxKayma = ((constraints.maxWidth - with(density) { EymenBoyut.kaydirTutamac.toPx() }) / 2f).coerceAtLeast(0f)
        var kayma by remember { mutableFloatStateOf(0f) }
        Row(modifier = Modifier.matchParentSize().padding(horizontal = EymenBosluk.s), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Box(Modifier.size(EymenBoyut.kaydirTutamac).background(EymenGradyan.kapat(r), CircleShape), contentAlignment = Alignment.Center) {
                EymenIkonu(EymenIkon.Telefon, stringResource(R.string.gelen_reddet), AramaEkraniRenk.beyaz, kapatma = true)
            }
            Box(Modifier.size(EymenBoyut.kaydirTutamac).background(EymenGradyan.ara(r), CircleShape), contentAlignment = Alignment.Center) {
                EymenIkonu(EymenIkon.Telefon, stringResource(R.string.gelen_cevapla), AramaEkraniRenk.beyaz)
            }
        }
        Text(
            text = stringResource(R.string.gelen_kaydir), color = r.metin2, style = EymenYazi.kucuk,
            modifier = Modifier.align(Alignment.Center).offset { IntOffset(kayma.roundToInt(), 0) },
        )
        Box(
            modifier = Modifier.matchParentSize().pointerInput(maxKayma, onCevapla, onReddet) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, miktar -> change.consume(); kayma = (kayma + miktar).coerceIn(-maxKayma, maxKayma) },
                    onDragCancel = { kayma = 0f },
                    onDragEnd = {
                        when {
                            kayma > maxKayma * .35f -> onCevapla()
                            kayma < -maxKayma * .35f -> onReddet()
                        }
                        kayma = 0f
                    },
                )
            },
        )
    }
}

@Preview(name = "Koyu")
@Composable private fun KaydirarakCevaplaKoyu() { EymenTelefonTemasi(tema = TemaSecimi.KOYU) { KaydirarakCevapla({}, {}) } }
@Preview(name = "Açık")
@Composable private fun KaydirarakCevaplaAcik() { EymenTelefonTemasi(tema = TemaSecimi.ACIK) { KaydirarakCevapla({}, {}) } }
