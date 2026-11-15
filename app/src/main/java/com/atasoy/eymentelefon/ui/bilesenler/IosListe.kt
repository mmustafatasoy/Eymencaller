package com.atasoy.eymentelefon.ui.bilesenler

// iOS listeleri: gruplu (inset grouped) kutu, satır, ayar satırı (renkli kare ikon), bölüm başlıkları, ⓘ butonu.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenDolgu
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenYazi

/** Satırın altındaki ince ayırıcı (son satırda çizilmez). */
fun Modifier.altAyirici(renk: Color, kalinlik: Dp, ciz: Boolean): Modifier =
    if (!ciz) this else drawBehind {
        val y = size.height - kalinlik.toPx() / 2
        drawLine(renk, Offset(0f, y), Offset(size.width, y), kalinlik.toPx())
    }

/** Gruplu ayarlar kutusu: soldan sağdan 16dp, köşe 10dp. Üstünde başlık, altında dipnot olabilir. */
@Composable
fun IosGrup(
    modifier: Modifier = Modifier,
    baslik: String? = null,
    dipnot: String? = null,
    icerik: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        if (baslik != null) IosGrupBasligi(baslik) else Box(Modifier.padding(top = EymenBosluk.l))
        Column(
            modifier = Modifier
                .padding(horizontal = EymenBosluk.kenar)
                .fillMaxWidth()
                .clip(EymenSekil.grup)
                .background(EymenRenk.yuzey)
                .padding(start = EymenBosluk.kenar),
            content = icerik,
        )
        if (dipnot != null) {
            Text(
                dipnot,
                style = EymenYazi.dipnot,
                color = EymenRenk.ikincilEtiket,
                modifier = Modifier.padding(start = EymenBosluk.grupBaslik, end = EymenBosluk.grupBaslik, top = EymenBosluk.s - EymenBosluk.cokKucuk),
            )
        }
    }
}

@Composable
fun IosGrupBasligi(metin: String) {
    Text(
        metin.uppercase(TurkceMetin.TR),
        style = EymenYazi.dipnot,
        color = EymenRenk.ikincilEtiket,
        modifier = Modifier.padding(start = EymenBosluk.grupBaslik, top = EymenBosluk.xl, bottom = EymenBosluk.s - EymenBosluk.cokKucuk),
    )
}

/** Düz listelerdeki harf / bölüm başlığı (Kişiler: "A", Arama: "KİŞİLER"). */
@Composable
fun IosBolumBasligi(metin: String, modifier: Modifier = Modifier, buyuk: Boolean = false) {
    Text(
        metin,
        style = if (buyuk) EymenYazi.altBaslik.copy(fontWeight = EymenYazi.vurguluGovde.fontWeight) else EymenYazi.dipnot,
        color = EymenRenk.ikincilEtiket,
        modifier = modifier
            .fillMaxWidth()
            .background(EymenRenk.arkaPlan)
            .padding(start = EymenBosluk.kenar, top = if (buyuk) EymenBosluk.s - EymenBosluk.cokKucuk else EymenBosluk.l, bottom = EymenBosluk.cokKucuk),
    )
}

/**
 * Genel iOS satırı. Düz listede başlangıç dolgusu verilir; gruplu kutuda kutunun kendi dolgusu kullanılır.
 * @param sol avatar / ikon yuvası  @param sag saat, ⓘ, anahtar, ok vb.
 */
@Composable
fun IosSatir(
    baslik: AnnotatedString,
    modifier: Modifier = Modifier,
    altBaslik: (@Composable () -> Unit)? = null,
    baslikRengi: Color = EymenRenk.etiket,
    kalin: Boolean = false,
    sol: (@Composable () -> Unit)? = null,
    sag: @Composable RowScope.() -> Unit = {},
    minYukseklik: Dp = EymenBoyut.satir,
    ayirici: Boolean = true,
    onTikla: (() -> Unit)? = null,
    onUzunBas: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onTikla != null || onUzunBas != null) Modifier.uzunBasilabilir(onTikla ?: {}, onUzunBas) else Modifier)
            .defaultMinSize(minHeight = minYukseklik),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (sol != null) {
            sol()
            Box(Modifier.size(EymenBosluk.m))
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = minYukseklik)
                .altAyirici(EymenRenk.ayirici, EymenBoyut.hairline, ayirici)
                .padding(EymenDolgu.satir),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EymenBosluk.s),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    baslik,
                    style = if (kalin) EymenYazi.vurguluGovde else EymenYazi.govde,
                    color = baslikRengi,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                altBaslik?.invoke()
            }
            sag()
        }
    }
}

@Composable
fun IosSatir(
    baslik: String,
    modifier: Modifier = Modifier,
    altBaslik: String? = null,
    baslikRengi: Color = EymenRenk.etiket,
    kalin: Boolean = false,
    sol: (@Composable () -> Unit)? = null,
    sag: @Composable RowScope.() -> Unit = {},
    minYukseklik: Dp = EymenBoyut.satir,
    ayirici: Boolean = true,
    onTikla: (() -> Unit)? = null,
    onUzunBas: (() -> Unit)? = null,
) {
    val alt: (@Composable () -> Unit)? = altBaslik?.let { a ->
        @Composable { Text(a, style = EymenYazi.altBaslik, color = EymenRenk.ikincilEtiket, maxLines = 2) }
    }
    IosSatir(
        baslik = AnnotatedString(baslik),
        modifier = modifier,
        altBaslik = alt,
        baslikRengi = baslikRengi,
        kalin = kalin,
        sol = sol,
        sag = sag,
        minYukseklik = minYukseklik,
        ayirici = ayirici,
        onTikla = onTikla,
        onUzunBas = onUzunBas,
    )
}

/** Ayarlar satırı: renkli kare ikon + başlık + değer + ok (iOS Ayarlar uygulaması gibi). */
@Composable
fun IosAyarSatiri(
    ikon: EymenIkon,
    kareRengi: Color,
    baslik: String,
    modifier: Modifier = Modifier,
    deger: String? = null,
    ok: Boolean = true,
    ayirici: Boolean = true,
    baslikRengi: Color = EymenRenk.etiket,
    onTikla: (() -> Unit)? = null,
    sag: @Composable RowScope.() -> Unit = {},
) {
    IosSatir(
        baslik = baslik,
        modifier = modifier,
        baslikRengi = baslikRengi,
        ayirici = ayirici,
        onTikla = onTikla,
        sol = { IkonKaresi(ikon, kareRengi) },
        sag = {
            sag()
            if (deger != null) Text(deger, style = EymenYazi.govde, color = EymenRenk.ikincilEtiket, maxLines = 1)
            if (ok) Ok()
        },
    )
}

@Composable
fun IkonKaresi(ikon: EymenIkon, renk: Color) {
    Box(
        Modifier.size(EymenBoyut.ikonKare).clip(EymenSekil.kare).background(renk),
        contentAlignment = Alignment.Center,
    ) {
        EymenIkonu(ikon, null, com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk.beyaz, boyut = EymenBoyut.ikonKucuk)
    }
}

/** Gri sağ ok (›). */
@Composable
fun Ok() {
    EymenIkonu(EymenIkon.Ileri, null, EymenRenk.ucunculEtiket, boyut = EymenBoyut.ikonKucuk - EymenBosluk.cokKucuk)
}

/** Mavi ⓘ butonu → kişi kartı. */
@Composable
fun IosBilgiButonu(aciklama: String, onTikla: () -> Unit) {
    Box(Modifier.sessizTiklama(onTikla = onTikla).padding(EymenBosluk.xs), contentAlignment = Alignment.Center) {
        EymenIkonu(EymenIkon.Bilgi, aciklama, EymenRenk.vurgu, boyut = EymenBoyut.ikon)
    }
}

/** Mavi kapsül buton ("İzin Ver"). */
@Composable
fun IosKapsulButon(metin: String, onTikla: () -> Unit, renk: Color = EymenRenk.vurgu) {
    Text(
        metin,
        style = EymenYazi.altBaslik.copy(fontWeight = EymenYazi.vurguluGovde.fontWeight),
        color = com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk.beyaz,
        maxLines = 1,
        modifier = Modifier.clip(EymenSekil.hap).background(renk).sessizTiklama(onTikla = onTikla).padding(EymenDolgu.kapsul),
    )
}

/** 50dp büyük buton (Devam). */
@Composable
fun IosButon(metin: String, onTikla: () -> Unit, modifier: Modifier = Modifier, etkin: Boolean = true) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = EymenBoyut.buton)
            .clip(EymenSekil.buton)
            .background(if (etkin) EymenRenk.vurgu else EymenRenk.vurgu.copy(alpha = com.atasoy.eymentelefon.ui.theme.EymenOpaklik.pasif))
            .uzunBasilabilir(onTikla = { if (etkin) onTikla() }, onUzunBas = null),
        contentAlignment = Alignment.Center,
    ) {
        Text(metin, style = EymenYazi.vurguluGovde, color = com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk.beyaz)
    }
}

/** Gruplu kutuda tek satırlık mavi / kırmızı eylem ("Mesaj Gönder", "Bu Arayanı Engelle"). */
@Composable
fun IosEylemSatiri(metin: String, onTikla: () -> Unit, yikici: Boolean = false, ayirici: Boolean = true) {
    IosSatir(
        baslik = metin,
        baslikRengi = if (yikici) EymenRenk.kirmizi else EymenRenk.vurgu,
        ayirici = ayirici,
        onTikla = onTikla,
    )
}

/** Gruplu kutu içinde serbest içerik arasına ince çizgi. */
@Composable
fun Ayirici() {
    androidx.compose.material3.HorizontalDivider(thickness = EymenBoyut.hairline, color = EymenRenk.ayirici)
}
