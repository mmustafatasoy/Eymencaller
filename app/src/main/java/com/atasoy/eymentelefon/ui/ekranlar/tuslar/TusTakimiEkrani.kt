package com.atasoy.eymentelefon.ui.ekranlar.tuslar

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.DisIslemler
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.ui.bilesenler.IosTusTakimi
import com.atasoy.eymentelefon.ui.bilesenler.sessizTiklama
import com.atasoy.eymentelefon.ui.ekranlar.numaraTuruMetni
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi

private const val TON_SURE_MS = 120
private const val TON_SES = 80

/** 08 · Tuş Takımı — numara, rehber eşleşmesi / "Numara Ekle", 3×4 tuşlar, yeşil ara, sil (basılı tut: hepsini sil). */
@Composable
fun TusTakimiEkrani(
    baslangicNumara: String,
    vm: TusTakimiViewModel = viewModel(key = "tuslar-$baslangicNumara", factory = TusTakimiViewModel.fabrika(baslangicNumara)),
) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val dokunsal = LocalHapticFeedback.current
    val ton = remember {
        try {
            ToneGenerator(AudioManager.STREAM_DTMF, TON_SES)
        } catch (e: RuntimeException) {
            HataKaydedici.kaydet("TusTakimi.ton", e)
            null
        }
    }
    DisposableEffect(Unit) { onDispose { ton?.release() } }
    val geriBildirim: (Char) -> Unit = { c ->
        if (durum.titresim) dokunsal.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        if (durum.tusSesi) dtmfTonu(c)?.let { ton?.startTone(it, TON_SURE_MS) }
    }
    val r = EymenRenk
    val bos = durum.numara.isEmpty()

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))
        // Numara + eşleşme alanı (sabit yükseklik: tuşlar zıplamasın)
        Column(
            Modifier.fillMaxWidth().height(EymenBoyut.tus + EymenBosluk.xl).padding(horizontal = EymenBosluk.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                NumaraBicimleyici.bicimle(durum.numara),
                style = EymenYazi.numaraEkrani,
                color = r.etiket,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.Center,
            )
            val eslesme = durum.eslesme
            val altMetin = when {
                eslesme != null -> eslesme.ad + (eslesme.varsayilanNumara?.let { " · " + numaraTuruMetni(it) } ?: "")
                !bos -> stringResource(R.string.numara_ekle)
                else -> " "
            }
            Text(
                altMetin,
                style = EymenYazi.altBaslik,
                color = r.vurgu,
                maxLines = 1,
                modifier = Modifier.padding(top = EymenBosluk.xs).sessizTiklama(etkin = !bos) {
                    if (eslesme != null) vm.olay(TusTakimiOlay.EslesmeSec) else DisIslemler.kisiyeEkle(context, durum.numara)
                },
            )
        }
        Spacer(Modifier.height(EymenBosluk.xl))
        IosTusTakimi(
            onTus = { geriBildirim(it); vm.olay(TusTakimiOlay.Tus(it)) },
            onUzunBas = { vm.olay(TusTakimiOlay.UzunBas(it)) },
        )
        Spacer(Modifier.height(EymenBosluk.tusDikey))
        Row(horizontalArrangement = Arrangement.spacedBy(EymenBosluk.tusYatay), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.size(EymenBoyut.tus))
            Box(
                Modifier.size(EymenBoyut.tus).clip(CircleShape).background(r.yesil).sessizTiklama { vm.olay(TusTakimiOlay.Ara) },
                contentAlignment = Alignment.Center,
            ) {
                EymenIkonu(EymenIkon.Telefon, stringResource(R.string.ara), AramaEkraniRenk.beyaz, boyut = EymenBoyut.cagriIkonu + EymenBosluk.xs)
            }
            val silGorunur by animateFloatAsState(if (bos) 0f else 1f, label = "sil")
            Box(Modifier.size(EymenBoyut.tus).alpha(silGorunur), contentAlignment = Alignment.Center) {
                if (!bos) SilButonu({ vm.olay(TusTakimiOlay.Sil) }, { vm.olay(TusTakimiOlay.HepsiniSil) })
            }
        }
        Spacer(Modifier.height(EymenBosluk.xl))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SilButonu(onSil: () -> Unit, onHepsi: () -> Unit) {
    Box(
        Modifier
            .size(EymenBoyut.tus)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSil,
                onLongClick = onHepsi,
            ),
        contentAlignment = Alignment.Center,
    ) {
        EymenIkonu(EymenIkon.Sil, stringResource(R.string.ae_sil), EymenRenk.ikincilEtiket, boyut = EymenBoyut.cagriIkonu)
    }
}

internal fun dtmfTonu(c: Char): Int? = when (c) {
    in '0'..'9' -> ToneGenerator.TONE_DTMF_0 + (c - '0')
    '*' -> ToneGenerator.TONE_DTMF_S
    '#' -> ToneGenerator.TONE_DTMF_P
    else -> null
}
