package com.atasoy.eymentelefon.cekirdek.yardimci

// ✅ HAZIR. Türkiye numara biçimi.
// "05321234567" → "0532 123 45 67" · "+905321234567" → "+90 532 123 45 67" · "02125550123" → "0212 555 01 23"
// Yazarken (yarım numara) de doğru gruplar. *, #, virgül içeren girdi olduğu gibi döner.

import android.telephony.PhoneNumberUtils

object NumaraBicimleyici {

    fun bicimle(numara: String): String {
        val n = numara.trim()
        if (n.isEmpty()) return n
        if (n.any { it == '*' || it == '#' || it == ',' || it == ';' }) return n
        val rakam = n.filter { it.isDigit() }
        return when {
            n.startsWith("+90") -> "+90" + grupla(rakam.drop(2), intArrayOf(3, 3, 2, 2)).let { if (it.isEmpty()) "" else " $it" }
            n.startsWith("+") -> PhoneNumberUtils.formatNumber(n, "TR") ?: n
            rakam.startsWith("0") && rakam.length <= 11 -> grupla(rakam, intArrayOf(4, 3, 2, 2))
            rakam.startsWith("444") && rakam.length <= 7 -> grupla(rakam, intArrayOf(3, 2, 2))
            rakam.length == 10 && rakam.startsWith("5") -> grupla(rakam, intArrayOf(3, 3, 2, 2))
            else -> rakam
        }
    }

    /** "+90 532…", "0532…", "532…" → hepsi "5321234567" */
    fun normalize(numara: String): String {
        var r = numara.filter { it.isDigit() }
        if (r.startsWith("0090")) r = r.drop(4)
        if (r.length == 12 && r.startsWith("90")) r = r.drop(2)
        if (r.length == 11 && r.startsWith("0")) r = r.drop(1)
        return r
    }

    fun ayniMi(a: String, b: String): Boolean {
        val na = normalize(a)
        val nb = normalize(b)
        return na.isNotEmpty() && na == nb
    }

    private fun grupla(rakam: String, gruplar: IntArray): String = buildString {
        var i = 0
        for (g in gruplar) {
            if (i >= rakam.length) break
            if (isNotEmpty()) append(' ')
            append(rakam, i, minOf(i + g, rakam.length))
            i += g
        }
        if (i < rakam.length) append(rakam.substring(i))
    }
}
