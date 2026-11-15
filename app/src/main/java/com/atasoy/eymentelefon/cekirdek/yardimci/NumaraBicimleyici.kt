package com.atasoy.eymentelefon.cekirdek.yardimci

// GOREV-04: Türkiye numara biçimi. İmzalar korunmuştur.
// Tanınmayan numara veya özel servis kodlarına dokunulmaz.

object NumaraBicimleyici {
    /** Ekranda gösterim. "05321234567" -> "0532 123 45 67". */
    fun bicimle(numara: String): String {
        val rakamlar = numara.filter(Char::isDigit)
        val bosluksuz = numara.filterNot(Char::isWhitespace)
        if (bosluksuz.isEmpty() || bosluksuz.any { !it.isDigit() && it != '+' }) return numara
        return when {
            numara.startsWith("+90") && rakamlar.startsWith("90") && rakamlar.length in 3..12 ->
                bolumle("+90", rakamlar.drop(2), intArrayOf(3, 3, 2, 2))
            !numara.startsWith("+") && rakamlar.startsWith("0") && rakamlar.length in 1..11 ->
                bolumle("", rakamlar, intArrayOf(4, 3, 2, 2))
            !numara.startsWith("+") && rakamlar.length in 1..10 ->
                bolumle("", rakamlar, intArrayOf(3, 3, 2, 2))
            else -> numara
        }
    }

    /** "+90 532 123 45 67" ile "0532 123 45 67" -> "5321234567". */
    fun normalize(numara: String): String {
        val rakamlar = numara.filter(Char::isDigit)
        return when {
            rakamlar.length == 12 && rakamlar.startsWith("90") -> rakamlar.drop(2)
            rakamlar.length == 11 && rakamlar.startsWith("0") -> rakamlar.drop(1)
            else -> rakamlar
        }
    }

    private fun bolumle(onEk: String, rakamlar: String, araliklar: IntArray): String {
        val bolumler = mutableListOf<String>()
        if (onEk.isNotEmpty()) bolumler.add(onEk)
        var konum = 0
        for (uzunluk in araliklar) {
            if (konum >= rakamlar.length) break
            val son = (konum + uzunluk).coerceAtMost(rakamlar.length)
            bolumler.add(rakamlar.substring(konum, son))
            konum = son
        }
        return bolumler.joinToString(" ")
    }
}
