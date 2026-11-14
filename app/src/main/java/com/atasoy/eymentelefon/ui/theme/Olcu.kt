package com.atasoy.eymentelefon.ui.theme

// ✅ HAZIR — DEĞİŞTİRME. Kaynak: docs/01_TASARIM_SISTEMI.md §2, §3
// Kod içinde çıplak 16.dp / RoundedCornerShape(12.dp) YASAK; buradakileri kullan.

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object EymenBosluk {
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val l = 16.dp
    val kenar = 18.dp
    val xl = 24.dp
    val xxl = 32.dp
    val kartArasi = 10.dp
    val listeDikey = 8.dp
    val tusYatay = 26.dp
    val tusDikey = 10.dp
    val kontrolYatay = 26.dp
    val kontrolDikey = 18.dp
}

object EymenBoyut {
    val dokunmaMin = 48.dp
    val anaButon = 54.dp
    val aramaCubugu = 50.dp
    val altMenu = 84.dp
    val altMenuHapGenislik = 60.dp
    val altMenuHapYukseklik = 32.dp
    val tusGenislik = 76.dp
    val tusYukseklik = 62.dp
    val araButonu = 70.dp
    val kapatButonu = 76.dp
    val kontrolButonu = 64.dp
    val kaydirRay = 76.dp
    val kaydirTutamac = 62.dp
    val fab = 58.dp
    val aksiyonKutusu = 58.dp
    val izinIkonKutusu = 42.dp
    val listeAraButonu = 38.dp
    val ikon = 22.dp
    val ikonKucuk = 18.dp
    val ikonBuyuk = 24.dp
    val avatarListe = 40.dp
    val avatarAramaListe = 42.dp
    val avatarFavori = 54.dp
    val avatarFavoriIzgara = 72.dp
    val avatarDetay = 104.dp
    val avatarGiden = 120.dp
    val avatarGelen = 128.dp
    val avatarGorusme = 96.dp
    val avatarProfil = 30.dp
    val karsilamaSimge = 132.dp
    val anahtarGenislik = 44.dp
    val anahtarYukseklik = 26.dp
    val altSayfaTutamacG = 42.dp
    val altSayfaTutamacY = 5.dp
    val odakCercevesi = 1.5.dp
    val ilerlemeCubugu = 6.dp
}

object EymenSekil {
    val kart = RoundedCornerShape(20.dp)
    val izinKarti = RoundedCornerShape(18.dp)
    val buton = RoundedCornerShape(18.dp)
    val hap = CircleShape
    val fab = RoundedCornerShape(20.dp)
    val altSayfa = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    val cip = RoundedCornerShape(12.dp)
    val aksiyon = RoundedCornerShape(20.dp)
    val ikonKutusu = RoundedCornerShape(13.dp)
    val oneriKarti = RoundedCornerShape(16.dp)
    val etiket = RoundedCornerShape(6.dp)
    val klavyeTusu = RoundedCornerShape(7.dp)
}
