package com.atasoy.eymentelefon.gorusme.ui

// V12-06 · Arama ekranında Kişi Posteri + iOS tarzı canlı ad (docs/07_V12_PLANI.md, görsel v12_01..03)

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import kotlinx.coroutines.delay

/** Ekranda gösterilecek hazır poster (bitmap ViewModel'de IO'da çözülür). */
data class PosterGorunumu(
    val resim: ImageBitmap?,
    /** AramaEkraniRenk palet sırası; null → ada göre */
    val renkIndex: Int?,
    val canli: Boolean,
)

/** Tam ekran poster fotoğrafı: canlıysa yavaş Ken Burns, görüşmede bulanık + karartma. */
@Composable
fun PosterFotografi(resim: ImageBitmap, canli: Boolean, bulanik: Boolean) {
    // Ken Burns: yavaş yakınlaşma + hafif yatay kayma
    val ilerleme = if (canli) {
        val t = rememberInfiniteTransition(label = "poster")
        val o by t.animateFloat(
            0f, 1f,
            infiniteRepeatable(tween(POSTER_SURE_MS, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "posterIlerleme",
        )
        o
    } else {
        0f
    }
    // taban büyütme kaymada kenar boşluğu açılmasın diye
    val olcek = if (canli) POSTER_TABAN + (POSTER_OLCEK - POSTER_TABAN) * ilerleme else 1f
    val b by animateFloatAsState(if (bulanik) 1f else 0f, tween(GECIS_MS), label = "bulaniklik")
    Image(
        resim,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = olcek
                scaleY = olcek
                translationX = (ilerleme - YARIM) * size.width * POSTER_KAYMA
            }
            .then(if (b > ESIK) Modifier.blur(EymenBoyut.posterBulanik * b) else Modifier),
    )
    // Üst ve alt karartma: yazılar ve butonlar her fotoğrafta okunur
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                0f to AramaEkraniRenk.siyah.copy(alpha = UST_KARARTMA),
                UST_BITIS to Color.Transparent,
                ALT_BASLANGIC to Color.Transparent,
                1f to AramaEkraniRenk.siyah.copy(alpha = ALT_KARARTMA),
            ),
        ),
    )
    if (b > ESIK) Box(Modifier.fillMaxSize().background(AramaEkraniRenk.siyah.copy(alpha = GORUSME_KARARTMA * b)))
}

/** Görüşme ekranında adın üstündeki küçük yuvarlak poster. */
@Composable
fun PosterKucukResim(resim: ImageBitmap, modifier: Modifier = Modifier) {
    Image(resim, null, modifier.size(EymenBoyut.posterKucuk).clip(CircleShape), contentScale = ContentScale.Crop)
}

/**
 * Gelen aramada iOS gibi dev, canlı ad: harfler sırayla aşağıdan belirir, sonra hafifçe "nefes alır".
 * Ad / soyad iki satır.
 */
@Composable
fun CanliAd(ad: String, modifier: Modifier = Modifier) {
    val satirlar = remember(ad) {
        // Numara (harf yok) tek satır kalır; ad soyad iki satır
        val p = ad.trim().split(Regex("\\s+"), limit = 2)
        if (p.size == 2 && ad.any { it.isLetter() }) p else listOf(ad.trim())
    }
    val enUzun = satirlar.maxOf { it.length }.coerceAtLeast(1)
    val stil = if (enUzun > SIGAN_HARF) {
        EymenYazi.posterIsim.copy(fontSize = EymenYazi.posterIsim.fontSize * (SIGAN_HARF.toFloat() / enUzun))
    } else {
        EymenYazi.posterIsim
    }
    val t = rememberInfiniteTransition(label = "nefes")
    val nefes by t.animateFloat(
        1f, NEFES_OLCEK,
        infiniteRepeatable(tween(NEFES_SURE_MS, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "nefesOlcek",
    )
    // Harflerin üzerinden soldan sağa geçen ışık (iOS canlı poster parıltısı)
    val parilti by t.animateFloat(
        PARILTI_BAS, PARILTI_SON,
        infiniteRepeatable(tween(PARILTI_SURE_MS, easing = LinearEasing), RepeatMode.Restart),
        label = "parilti",
    )
    val toplam = satirlar.sumOf { it.length }.coerceAtLeast(1)
    Column(
        modifier.graphicsLayer {
            scaleX = nefes
            scaleY = nefes
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var sira = 0
        satirlar.forEach { satir ->
            Row {
                satir.forEach { harf ->
                    val konum = sira.toFloat() / toplam
                    val isik = (1f - kotlin.math.abs(parilti - konum) * PARILTI_GENISLIK).coerceIn(0f, 1f)
                    BelirenHarf(harf, sira++, stil, ad, isik)
                }
            }
        }
    }
}

@Composable
private fun BelirenHarf(harf: Char, sira: Int, stil: androidx.compose.ui.text.TextStyle, anahtar: String, isik: Float) {
    val a = remember(anahtar) { Animatable(0f) }
    LaunchedEffect(anahtar) {
        delay(sira * HARF_ARALIK_MS)
        a.animateTo(1f, tween(HARF_SURE_MS, easing = FastOutSlowInEasing))
    }
    Text(
        harf.toString(),
        style = stil,
        color = AramaEkraniRenk.beyaz.copy(alpha = SONUK + (1f - SONUK) * isik),
        maxLines = 1,
        modifier = Modifier.graphicsLayer {
            alpha = a.value
            translationY = (1f - a.value) * size.height * HARF_KAYMA
            // ışık geçerken harf hafifçe büyür
            val b = 1f + isik * PARILTI_BUYUME
            scaleX = b
            scaleY = b
        },
    )
}

private const val POSTER_OLCEK = 1.16f
private const val POSTER_TABAN = 1.06f
private const val POSTER_SURE_MS = 12_000
private const val POSTER_KAYMA = 0.05f
private const val YARIM = 0.5f
private const val GECIS_MS = 450
private const val ESIK = 0.01f
private const val UST_KARARTMA = 0.45f
private const val UST_BITIS = 0.35f
private const val ALT_BASLANGIC = 0.6f
private const val ALT_KARARTMA = 0.6f
private const val GORUSME_KARARTMA = 0.35f
private const val NEFES_OLCEK = 1.035f
private const val NEFES_SURE_MS = 3_200
private const val HARF_ARALIK_MS = 70L
private const val HARF_SURE_MS = 600
private const val HARF_KAYMA = 0.6f
private const val SIGAN_HARF = 9
private const val PARILTI_BAS = -0.4f
private const val PARILTI_SON = 1.4f
private const val PARILTI_SURE_MS = 2_800
private const val PARILTI_GENISLIK = 3.5f
private const val PARILTI_BUYUME = 0.06f
private const val SONUK = 0.82f
