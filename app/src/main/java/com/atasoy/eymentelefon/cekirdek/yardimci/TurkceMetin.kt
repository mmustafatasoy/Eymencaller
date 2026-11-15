package com.atasoy.eymentelefon.cekirdek.yardimci

// ✅ HAZIR — DEĞİŞTİRME. Türkçe sıralama, gruplama ve aksan-duyarsız arama.

import java.text.Collator
import java.util.Locale

object TurkceMetin {

    val TR: Locale = Locale.forLanguageTag("tr-TR")

    /** Türk alfabesi + yabancı harfler (Q, W, X) doğru sırada. Rehber harf başlıkları bunlardır. */
    val HARFLER: List<String> = listOf(
        "A", "B", "C", "Ç", "D", "E", "F", "G", "Ğ", "H", "I", "İ", "J", "K", "L", "M",
        "N", "O", "Ö", "P", "Q", "R", "S", "Ş", "T", "U", "Ü", "V", "W", "X", "Y", "Z",
    )

    /** Sağ kenardaki harf dizini (mockup 05 ile aynı) + "#". */
    val DIZIN: List<String> = listOf(
        "A", "B", "C", "Ç", "D", "E", "F", "G", "H", "I", "İ", "J", "K", "L", "M", "N",
        "O", "Ö", "P", "R", "S", "Ş", "T", "U", "Ü", "V", "Y", "Z", "#",
    )

    private val siralayici: Collator = Collator.getInstance(TR).apply { strength = Collator.SECONDARY }

    /** Rehber sıralaması: "Çağla" C'den sonra, "İsmail" I'dan sonra. */
    val karsilastirici: Comparator<String> = Comparator { a, b ->
        synchronized(siralayici) { siralayici.compare(a, b) }
    }

    fun buyukHarf(c: Char): Char = when (c) {
        'i' -> 'İ'
        'ı' -> 'I'
        else -> c.uppercaseChar()
    }

    /**
     * HATA-024: metni görünen karakterlere böler (emoji, bayrak, ❤️ gibi birleşik simgeler tek parça kalır).
     * "Hayatım❤️" → [H, a, y, a, t, ı, m, ❤️]
     */
    fun grafemler(metin: String): List<String> {
        val b = android.icu.text.BreakIterator.getCharacterInstance(TR)
        b.setText(metin)
        val sonuc = ArrayList<String>(metin.length)
        var bas = b.first()
        var son = b.next()
        while (son != android.icu.text.BreakIterator.DONE) {
            sonuc += metin.substring(bas, son)
            bas = son
            son = b.next()
        }
        return sonuc
    }

    /** Kişinin rehber grubu: HARFLER içinden biri ya da "#". */
    fun grupHarfi(ad: String): String {
        // HATA-024: baştaki simgeler atlanır ("❤️ Hayatım" → H grubu)
        val ilk = ad.firstOrNull { it.isLetterOrDigit() } ?: return "#"
        val h = buyukHarf(ilk).toString()
        return if (h in HARFLER) h else "#"
    }

    /** "Ayşe Demir" → "AD", "Annem" → "A", "+90 532..." → "#" */
    fun basHarfler(ad: String): String {
        // HATA-024: simgeler (❤️, 😍…) baş harf sayılmaz; "Hayatım❤️" → "H", "❤️ Hayatım" → "H", "❤️" → "❤️"
        val kelimeler = ad.trim().split(Regex("\\s+")).filter { k -> k.any { it.isLetter() } }
        if (kelimeler.isEmpty()) {
            if (ad.any { it.isDigit() } || ad.isBlank()) return "#"
            return grafemler(ad.trim()).firstOrNull() ?: "#"
        }
        val ilk = kelimeler.first().first { it.isLetter() }
        val son = kelimeler.drop(1).lastOrNull()?.firstOrNull { it.isLetter() }
        return buildString {
            append(buyukHarf(ilk))
            if (son != null) append(buyukHarf(son))
        }
    }

    /**
     * Aksan ve büyük/küçük harf duyarsız arama anahtarı. Karakter sayısı DEĞİŞMEZ (1:1),
     * bu yüzden bulunan aralık orijinal metinde aynen vurgulanabilir.
     * "Işık" → "isik", "ISIK" → "isik", "Ayşe" → "ayse"
     */
    fun aramaAnahtari(metin: String): String = buildString(metin.length) {
        metin.forEach { c ->
            append(
                when (c) {
                    'I', 'ı', 'İ', 'i' -> 'i'
                    'Ç', 'ç' -> 'c'
                    'Ğ', 'ğ' -> 'g'
                    'Ö', 'ö' -> 'o'
                    'Ş', 'ş' -> 's'
                    'Ü', 'ü' -> 'u'
                    else -> c.lowercaseChar()
                },
            )
        }
    }

    /** Vurgulanacak aralık; yoksa null. (Mockup 07: eşleşen kısım vurgu renginde) */
    fun eslesmeAraligi(metin: String, sorgu: String): IntRange? {
        if (sorgu.isBlank()) return null
        val i = aramaAnahtari(metin).indexOf(aramaAnahtari(sorgu.trim()))
        return if (i < 0) null else i until i + sorgu.trim().length
    }

    fun iceriyorMu(metin: String?, sorgu: String): Boolean =
        metin != null && aramaAnahtari(metin).contains(aramaAnahtari(sorgu.trim()))
}
