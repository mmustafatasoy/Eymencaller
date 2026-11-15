package com.atasoy.eymentelefon.ui.bilesenler

// Arama ekranı parçaları: 76dp yarı saydam yuvarlak butonlar, kırmızı bitir, Kabul/Reddet, "kaydırarak cevapla".

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenOpaklik
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/** Görüşme butonu (hoparlör, sessiz, tuşlar…). Aktifken beyaz zemin + siyah ikon. */
@Composable
fun IosCagriButonu(
    ikon: EymenIkon,
    metin: String,
    onTikla: () -> Unit,
    modifier: Modifier = Modifier,
    aktif: Boolean = false,
    etkin: Boolean = true,
) {
    val etkilesim = remember { MutableInteractionSource() }
    val basili by etkilesim.collectIsPressedAsState()
    val zemin = when {
        aktif -> AramaEkraniRenk.beyaz
        basili -> AramaEkraniRenk.butonBasili
        else -> AramaEkraniRenk.butonZemini
    }
    Column(
        modifier.alpha(if (etkin) 1f else EymenOpaklik.pasif),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EymenBosluk.s),
    ) {
        Box(
            Modifier
                .yayliBasma(etkilesim)
                .size(EymenBoyut.cagriButonu)
                // V13-05: aktif değilken buzlu cam (arkadaki poster bulanık + ışık kenarı)
                .then(if (aktif) Modifier.clip(CircleShape).background(zemin) else Modifier.camYuzey(CircleShape, zemin))
                .clickable(interactionSource = etkilesim, indication = null, enabled = etkin, role = Role.Switch, onClick = onTikla),
            contentAlignment = Alignment.Center,
        ) {
            EymenIkonu(ikon, metin, if (aktif) AramaEkraniRenk.siyah else AramaEkraniRenk.beyaz, boyut = EymenBoyut.cagriIkonu)
        }
        Text(metin, style = EymenYazi.dipnot, color = AramaEkraniRenk.beyaz, maxLines = 1)
    }
}

/** Renkli yuvarlak buton: Bitir (kırmızı), Kabul Et (yeşil), Reddet (kırmızı). */
@Composable
fun IosRenkliCagriButonu(
    renk: Color,
    aciklama: String,
    onTikla: () -> Unit,
    modifier: Modifier = Modifier,
    kapatma: Boolean = true,
    etiket: String? = null,
    boyut: androidx.compose.ui.unit.Dp = EymenBoyut.cagriButonu,
) {
    val etkilesim = remember { MutableInteractionSource() }
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(EymenBosluk.s)) {
        Box(
            Modifier
                .yayliBasma(etkilesim)
                .size(boyut)
                .clip(CircleShape)
                .background(renk)
                .clickable(interactionSource = etkilesim, indication = null, role = Role.Button, onClick = onTikla),
            contentAlignment = Alignment.Center,
        ) {
            EymenIkonu(EymenIkon.Telefon, aciklama, AramaEkraniRenk.beyaz, boyut = EymenBoyut.cagriIkonu + EymenBosluk.xs, kapatma = kapatma)
        }
        if (etiket != null) Text(etiket, style = EymenYazi.dipnot, color = AramaEkraniRenk.beyaz, maxLines = 1)
    }
}

/** Gelen arama, ekran kilitliyken: yeşil tutamacı sağa kaydır → cevapla (%70 eşik). Yazı parıltılı akar. */
@Composable
fun IosKaydirarakCevapla(metin: String, aciklama: String, onCevapla: () -> Unit, modifier: Modifier = Modifier) {
    val kapsam = rememberCoroutineScope()
    val guncel by rememberUpdatedState(onCevapla)
    val ofset = remember { Animatable(0f) }
    var azami by remember { mutableFloatStateOf(1f) }
    val context = LocalContext.current
    val erisilebilir = remember {
        context.getSystemService(android.view.accessibility.AccessibilityManager::class.java)?.isTouchExplorationEnabled == true
    }
    val parilti = rememberInfiniteTransition(label = "parilti")
    val konum by parilti.animateFloat(-1f, 2f, infiniteRepeatable(tween(PARILTI_MS, easing = LinearEasing), RepeatMode.Restart), label = "konum")

    Box(
        modifier
            .fillMaxWidth()
            .height(EymenBoyut.kaydirY)
            .clip(EymenSekil.hap)
            .background(AramaEkraniRenk.kaydirZemini)
            .onSizeChanged { azami = (it.width - it.height).toFloat().coerceAtLeast(1f) }
            .padding(EymenBosluk.xs + EymenBosluk.cokKucuk),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            metin,
            style = EymenYazi.kaydir.merge(
                TextStyle(
                    brush = Brush.linearGradient(
                        listOf(AramaEkraniRenk.ikincil, AramaEkraniRenk.beyaz, AramaEkraniRenk.ikincil),
                        start = Offset(PARILTI_GENISLIK * konum, 0f),
                        end = Offset(PARILTI_GENISLIK * (konum + 1f), 0f),
                    ),
                ),
            ),
            maxLines = 1,
            modifier = Modifier.align(Alignment.Center).padding(start = EymenBoyut.kaydirTutamac).alpha(1f - (ofset.value / azami).coerceIn(0f, 1f)),
        )
        Box(
            Modifier
                .offset { IntOffset(ofset.value.roundToInt(), 0) }
                .size(EymenBoyut.kaydirTutamac)
                .clip(CircleShape)
                .background(AramaEkraniRenk.kabul)
                .then(if (erisilebilir) Modifier.clickable(onClick = onCevapla) else Modifier)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (ofset.value > azami * ESIK) {
                                kapsam.launch { ofset.animateTo(azami) }
                                guncel()
                            } else {
                                kapsam.launch { ofset.animateTo(0f) }
                            }
                        },
                        onDragCancel = { kapsam.launch { ofset.animateTo(0f) } },
                    ) { degisim, miktar ->
                        degisim.consume()
                        kapsam.launch { ofset.snapTo((ofset.value + miktar).coerceIn(0f, azami)) }
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            EymenIkonu(EymenIkon.Telefon, aciklama, AramaEkraniRenk.beyaz, boyut = EymenBoyut.cagriIkonu - EymenBosluk.cokKucuk)
        }
    }
}

private const val ESIK = 0.7f
private const val PARILTI_MS = 2200
private const val PARILTI_GENISLIK = 420f
