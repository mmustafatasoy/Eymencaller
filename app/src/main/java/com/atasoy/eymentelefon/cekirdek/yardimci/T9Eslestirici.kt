package com.atasoy.eymentelefon.cekirdek.yardimci

// ✅ HAZIR — DEĞİŞTİRME. Türkçe T9 tablosu (mockup 03'teki tuş harfleri ile aynı).

object T9Eslestirici {

    /** Tuş takımı çizimi için: rakam → alt yazı. Sıra tuş takımı sırasıdır. */
    val TUSLAR: List<Pair<Char, String>> = listOf(
        '1' to "",
        '2' to "ABCÇ",
        '3' to "DEF",
        '4' to "GĞHIİ",
        '5' to "JKL",
        '6' to "MNOÖ",
        '7' to "PQRSŞ",
        '8' to "TUÜV",
        '9' to "WXYZ",
        '*' to "",
        '0' to "+",
        '#' to "",
    )

    private val harfTusu: Map<Char, Char> = buildMap {
        TUSLAR.forEach { (rakam, harfler) ->
            if (rakam.isDigit() && rakam != '0') harfler.forEach { put(it, rakam) }
        }
    }

    /** "Ayşe" → "2973" */
    fun kod(ad: String): String = buildString {
        ad.forEach { c ->
            when {
                c.isDigit() -> append(c)
                else -> harfTusu[TurkceMetin.buyukHarf(c)]?.let { append(it) }
            }
        }
    }

    /**
     * Girilen rakamlar adın herhangi bir kelimesinin başıyla ya da tam kodun içiyle eşleşiyor mu?
     * Puan: kelime başı eşleşmesi = 2, iç eşleşme = 1, yok = 0 (en yüksek puanlı kişi kartta gösterilir).
     */
    fun puan(ad: String, rakamlar: String): Int {
        if (rakamlar.length < 2 || !rakamlar.all { it.isDigit() }) return 0
        val kelimeBasi = ad.split(Regex("\\s+")).any { kod(it).startsWith(rakamlar) }
        if (kelimeBasi) return 2
        return if (kod(ad.replace(" ", "")).contains(rakamlar)) 1 else 0
    }
}
