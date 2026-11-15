package com.atasoy.eymentelefon.ui.theme

// ✅ v1.1 iOS yazı ölçeği (Large Title 34 … Caption 11). Renk burada yok; kullanıldığı yerde EymenRenk ile verilir.

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object EymenYazi {
    val buyukBaslik = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.4).sp, lineHeight = 41.sp)
    val baslik1 = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.3).sp)
    val baslik2 = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
    val baslik3 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    val vurguluGovde = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.2).sp)
    val govde = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.2).sp)
    val aciklama = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
    val altBaslik = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)
    val dipnot = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, lineHeight = 18.sp)
    val etiket1 = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
    val etiket2 = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal)
    val sekme = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium)
    val numaraEkrani = TextStyle(fontSize = 38.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.5.sp)
    val tusRakam = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Normal)
    val tusHarf = TextStyle(fontSize = 9.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
    /** V12-06: gelen aramada dev canlı ad */
    val posterIsim = TextStyle(
        fontSize = 64.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-1).sp,
        lineHeight = 68.sp,
        // Her fotoğrafta okunsun diye yumuşak gölge
        shadow = Shadow(Color(0x73000000), Offset(0f, 3f), 18f),
    )
    val aramaIsim = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.2.sp)
    val aramaDurum = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, fontFeatureSettings = "tnum")
    val kaydir = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal)
    val dizin = TextStyle(fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
    val monogram = TextStyle(fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
}
