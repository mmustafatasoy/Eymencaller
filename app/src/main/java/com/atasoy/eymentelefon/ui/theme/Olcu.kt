package com.atasoy.eymentelefon.ui.theme

// ✅ v1.1 iOS ölçüleri. Kaynak: docs/01_TASARIM_SISTEMI.md §2–3 · Kod içinde çıplak 16.dp YASAK.

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object EymenBosluk {
    val cokKucuk = 2.dp
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val kenar = 16.dp
    val l = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val dev = 48.dp
    /** Gruplu liste başlığının soldan girintisi (kenar + iç girinti) */
    val grupBaslik = 32.dp
    val grupArasi = 35.dp
    val tusYatay = 28.dp
    val tusDikey = 16.dp
    val cagriYatay = 30.dp
    val cagriDikey = 26.dp
}

object EymenBoyut {
    val dokunmaMin = 44.dp
    val navCubugu = 44.dp
    val sekmeCubugu = 49.dp
    val sekmeIkonu = 26.dp
    val satir = 44.dp
    val satirAvatarli = 60.dp
    val avatarListe = 40.dp
    val avatarKartim = 58.dp
    val avatarKucuk = 34.dp
    val posterBulanik = 18.dp
    val posterKucuk = 72.dp
    val posterOnizlemeEn = 220.dp
    val posterOnizlemeBoy = 400.dp
    val cerceve = 3.dp
    val duzenleIsaret = 22.dp
    val onizlemeKart = 270.dp
    val kartPoster = 330.dp
    val onizlemeKartBoy = 300.dp
    val onizlemeMenu = 250.dp
    val satirKisi = 52.dp
    val avatarKart = 112.dp
    val aramaAlani = 36.dp
    val segmentYukseklik = 32.dp
    val anahtarG = 51.dp
    val anahtarY = 31.dp
    val anahtarTop = 27.dp
    val buton = 50.dp
    val kapsulY = 30.dp
    val ikonKare = 29.dp
    val ikon = 22.dp
    val ikonKucuk = 17.dp
    val ikonMini = 12.dp
    val tus = 78.dp
    val cagriButonu = 86.dp
    val cagriIkonu = 34.dp
    val cevapButonu = 88.dp
    val kaydirY = 80.dp
    val kaydirTutamac = 68.dp
    val eylemKutusuY = 62.dp
    val uyariGenislik = 270.dp
    val karsilamaSimge = 92.dp
    val ozellikIkonu = 30.dp
    val hairline = 0.5.dp
    val imlec = 2.dp
}

object EymenSekil {
    val grup = RoundedCornerShape(10.dp)
    val buton = RoundedCornerShape(14.dp)
    val kare = RoundedCornerShape(7.dp)
    val aramaAlani = RoundedCornerShape(10.dp)
    val segmentDis = RoundedCornerShape(9.dp)
    val segmentIc = RoundedCornerShape(7.dp)
    val eylemKutusu = RoundedCornerShape(12.dp)
    val uyari = RoundedCornerShape(14.dp)
    val sayfa = RoundedCornerShape(14.dp)
    val hap = CircleShape
}

object EymenDolgu {
    val satir = PaddingValues(top = 8.dp, bottom = 8.dp, end = 14.dp)
    val grupSatiri = PaddingValues(top = 11.dp, bottom = 11.dp, end = 14.dp)
    val kapsul = PaddingValues(horizontal = 12.dp, vertical = 5.dp)
    val segment = PaddingValues(horizontal = 16.dp, vertical = 5.dp)
    val segmentKucuk = PaddingValues(horizontal = 9.dp, vertical = 4.dp)
    val uyariMetni = PaddingValues(start = 16.dp, end = 16.dp, top = 19.dp, bottom = 18.dp)
    val uyariButonu = PaddingValues(vertical = 12.dp, horizontal = 4.dp)
    val sayfaSecenegi = PaddingValues(vertical = 16.dp, horizontal = 10.dp)
}

object EymenOpaklik {
    const val pasif = 0.35f
    const val basili = 0.5f
    const val golge = 0.12f
}
