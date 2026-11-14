package com.atasoy.eymentelefon.ui.theme

// ✅ HAZIR — DEĞİŞTİRME. Kaynak: docs/01_TASARIM_SISTEMI.md §4
// Renk burada YOK: renk kullanıldığı yerde EymenRenk ile verilir.

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object EymenYazi {
    val girisBaslik = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp)
    val baslik1 = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp)
    val baslik2 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
    val isim = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
    val govde = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp)
    val kucuk = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
    val bolum = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp)
    val adim = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
    val etiket = TextStyle(fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
    val cip = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
    val buton = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
    val altMenu = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
    val numaraEkrani = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Light, letterSpacing = 1.sp)
    val tus = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Medium)
    val tusHarf = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Normal, letterSpacing = 1.sp)
    val aramaIsim = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp)
    val sayac = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum")
    val harfDizini = TextStyle(fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
    val harfBasligi = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
}
