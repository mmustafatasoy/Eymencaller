package com.atasoy.eymentelefon.widget

// V13-10 · Favoriler ana ekran bileşeni (Glance). Dokununca o kişiyi arar.

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.atasoy.eymentelefon.MainActivity
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.navigasyon.AnaSekme
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.AvatarRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.veri.model.Kisi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private data class WidgetKisi(val kisi: Kisi, val foto: Bitmap?)

class FavoriWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val k = context.konteyner()
        val kisiler = if (k.izinYoneticisi.zorunlularTamam()) {
            runCatching { k.rehberDeposu.favoriler().first().filter { it.varsayilanNumara != null }.take(AZAMI) }
                .onFailure { HataKaydedici.kaydet("FavoriWidget.favoriler", it) }
                .getOrDefault(emptyList())
        } else {
            emptyList()
        }
        val ogeler = withContext(Dispatchers.IO) { kisiler.map { WidgetKisi(it, kucukFoto(context, it)) } }
        provideContent { Icerik(context, ogeler) }
    }

    @Composable
    private fun Icerik(context: Context, ogeler: List<WidgetKisi>) {
        Column(
            GlanceModifier
                .fillMaxSize()
                .cornerRadius(EymenBoyut.widgetKose)
                .background(AramaEkraniRenk.siyah)
                .padding(EymenBosluk.m)
                .clickable(actionStartActivity(MainActivity.sekmeIntenti(context, AnaSekme.FAVORILER))),
        ) {
            Text(
                context.getString(R.string.favoriler),
                style = TextStyle(color = ColorProvider(AramaEkraniRenk.beyaz), fontWeight = FontWeight.Bold, fontSize = EymenBoyut.widgetBaslik),
            )
            Spacer(GlanceModifier.height(EymenBosluk.s))
            if (ogeler.isEmpty()) {
                Text(context.getString(R.string.widget_bos), style = TextStyle(color = ColorProvider(AramaEkraniRenk.ikincil), fontSize = EymenBoyut.widgetYazi))
            } else {
                Row(GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    ogeler.forEach { o -> Oge(context, o) }
                }
            }
        }
    }

    @Composable
    private fun Oge(context: Context, o: WidgetKisi) {
        val numara = o.kisi.varsayilanNumara?.numara.orEmpty()
        Column(
            GlanceModifier
                .padding(horizontal = EymenBosluk.xs)
                .clickable(actionStartActivity(WidgetAramaActivity.intent(context, numara))),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val (ust, _) = if (o.kisi.basHarfler.any { it.isLetter() }) AvatarRenk.renkliCift(o.kisi.basHarfler) else AvatarRenk.ust to AvatarRenk.alt
            Box(
                GlanceModifier.size(EymenBoyut.widgetAvatar).cornerRadius(EymenBoyut.widgetAvatar / 2).background(ust),
                contentAlignment = Alignment.Center,
            ) {
                if (o.foto != null) {
                    Image(ImageProvider(o.foto), o.kisi.ad, GlanceModifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Text(o.kisi.basHarfler, style = TextStyle(color = ColorProvider(AvatarRenk.metin), fontWeight = FontWeight.Bold, fontSize = EymenBoyut.widgetBaslik))
                }
            }
            Text(
                o.kisi.ad.substringBefore(' '),
                maxLines = 1,
                style = TextStyle(color = ColorProvider(AramaEkraniRenk.beyaz), fontSize = EymenBoyut.widgetYazi, textAlign = TextAlign.Center),
                modifier = GlanceModifier.padding(top = EymenBosluk.xs).size(EymenBoyut.widgetAvatar + EymenBosluk.s, EymenBoyut.widgetAdYuksekligi),
            )
        }
    }

    private fun kucukFoto(context: Context, kisi: Kisi): Bitmap? = kisi.fotoUri?.let { uri ->
        runCatching {
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = ORNEK })
            }
        }.onFailure { HataKaydedici.kaydet("FavoriWidget.foto", it) }.getOrNull()
    }

    companion object {
        private const val AZAMI = 4
        private const val ORNEK = 2

        /** Favoriler değişince çağrılır. */
        suspend fun guncelle(context: Context) {
            runCatching { FavoriWidget().updateAll(context) }.onFailure { HataKaydedici.kaydet("FavoriWidget.guncelle", it) }
        }
    }
}

class FavoriWidgetAlicisi : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FavoriWidget()
}

