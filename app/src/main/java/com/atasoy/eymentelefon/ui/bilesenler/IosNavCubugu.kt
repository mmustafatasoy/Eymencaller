package com.atasoy.eymentelefon.ui.bilesenler

// iOS gezinme çubuğu (44dp) + büyük başlık. Liste kaydırılınca büyük başlık kaybolur, küçük başlık ortada belirir.

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi

/** Büyük başlık listenin ilk öğesidir; ilk öğe kaydırılınca true olur. */
@Composable
fun rememberKucukBaslikGorunur(liste: LazyListState): State<Boolean> = remember(liste) {
    derivedStateOf { liste.firstVisibleItemIndex > 0 || liste.firstVisibleItemScrollOffset > BASLIK_ESIGI }
}

private const val BASLIK_ESIGI = 60

@Composable
fun IosNavCubugu(
    modifier: Modifier = Modifier,
    kucukBaslik: String? = null,
    baslikGorunur: Boolean = false,
    zemin: Color = EymenRenk.arkaPlan,
    sol: @Composable RowScope.() -> Unit = {},
    orta: (@Composable () -> Unit)? = null,
    sag: @Composable RowScope.() -> Unit = {},
) {
    Box(modifier.fillMaxWidth().height(EymenBoyut.navCubugu).background(zemin)) {
        Row(
            Modifier.align(Alignment.CenterStart).padding(start = EymenBosluk.s),
            verticalAlignment = Alignment.CenterVertically,
            content = sol,
        )
        Box(Modifier.align(Alignment.Center)) {
            if (orta != null) {
                orta()
            } else if (kucukBaslik != null) {
                AnimatedVisibility(baslikGorunur, enter = fadeIn(), exit = fadeOut()) {
                    Text(kucukBaslik, style = EymenYazi.vurguluGovde, color = EymenRenk.etiket, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Row(
            Modifier.align(Alignment.CenterEnd).padding(end = EymenBosluk.s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs),
            content = sag,
        )
        if (baslikGorunur) {
            HorizontalDivider(Modifier.align(Alignment.BottomCenter), EymenBoyut.hairline, EymenRenk.ayirici)
        }
    }
}

@Composable
fun IosBuyukBaslik(metin: String, modifier: Modifier = Modifier) {
    Text(
        metin,
        style = EymenYazi.buyukBaslik,
        color = EymenRenk.etiket,
        modifier = modifier.padding(start = EymenBosluk.kenar, end = EymenBosluk.kenar, bottom = EymenBosluk.s),
    )
}

/** Mavi metin butonu ("Düzenle", "Vazgeç", "Bitti"). */
@Composable
fun IosMetinButonu(
    metin: String,
    onTikla: () -> Unit,
    modifier: Modifier = Modifier,
    kalin: Boolean = false,
    renk: Color = EymenRenk.vurgu,
    etkin: Boolean = true,
) {
    Text(
        metin,
        style = if (kalin) EymenYazi.vurguluGovde else EymenYazi.govde,
        color = if (etkin) renk else EymenRenk.ucunculEtiket,
        maxLines = 1,
        modifier = modifier
            .sessizTiklama(etkin, onTikla)
            .padding(horizontal = EymenBosluk.s, vertical = EymenBosluk.m),
    )
}

/** "‹ Kişiler" geri butonu. */
@Composable
fun IosGeriButonu(metin: String, onTikla: () -> Unit) {
    Row(
        Modifier.sessizTiklama(onTikla = onTikla).padding(vertical = EymenBosluk.m, horizontal = EymenBosluk.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EymenIkonu(EymenIkon.Geri, null, EymenRenk.vurgu, boyut = EymenBoyut.ikon)
        Text(metin, style = EymenYazi.govde, color = EymenRenk.vurgu, maxLines = 1)
    }
}

/** Gezinme çubuğunda ikon butonu (dişli, +). */
@Composable
fun IosNavIkonu(ikon: EymenIkon, aciklama: String, onTikla: () -> Unit) {
    Box(
        Modifier.sessizTiklama(onTikla = onTikla).padding(EymenBosluk.m - EymenBosluk.cokKucuk),
        contentAlignment = Alignment.Center,
    ) {
        EymenIkonu(ikon, aciklama, EymenRenk.vurgu)
    }
}
