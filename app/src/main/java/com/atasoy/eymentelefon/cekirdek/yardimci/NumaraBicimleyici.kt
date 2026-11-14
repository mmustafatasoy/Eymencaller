package com.atasoy.eymentelefon.cekirdek.yardimci

// ⬜ CODEX YAZACAK — GOREV-04
// İmzalar SABİT, sadece gövdeleri doldur.
// Kural: Türkiye formatı. "05321234567" → "0532 123 45 67", "+905321234567" → "+90 532 123 45 67",
//        "02125550123" → "0212 555 01 23". Tanınmayan biçimde girdiyi olduğu gibi döndür.
//        Android'in PhoneNumberUtils.formatNumber(numara, "TR") sonucunu temel al.

object NumaraBicimleyici {

    /** Ekranda gösterim için biçimler. */
    fun bicimle(numara: String): String = numara // GOREV-04

    /** Karşılaştırma için normalize eder: "+90 532..." ve "0532..." aynı sonucu verir → "5321234567" */
    fun normalize(numara: String): String = numara.filter { it.isDigit() } // GOREV-04
}
