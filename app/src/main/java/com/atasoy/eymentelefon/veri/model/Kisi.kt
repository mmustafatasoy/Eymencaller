package com.atasoy.eymentelefon.veri.model

// ✅ HAZIR — DEĞİŞTİRME. Alan eklemek gerekirse HATA_GUNLUGU › SORULAR.

import android.net.Uri
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin

enum class NumaraTuru { MOBIL, EV, IS, DIGER }

data class Numara(
    val numara: String,
    val tur: NumaraTuru,
    /** Kullanıcının özel etiketi (ContactsContract.CommonDataKinds.Phone.LABEL) */
    val ozelEtiket: String? = null,
    val varsayilan: Boolean = false,
)

data class Kisi(
    /** ContactsContract.Contacts._ID */
    val id: Long,
    val lookupKey: String,
    val ad: String,
    val numaralar: List<Numara>,
    val fotoUri: Uri? = null,
    val favori: Boolean = false,
    val sirket: String? = null,
    val eposta: String? = null,
    val not: String? = null,
    /** Önceden hesaplanır (5000+ kişide akıcılık) */
    val basHarfler: String = TurkceMetin.basHarfler(ad),
    val grupHarfi: String = TurkceMetin.grupHarfi(ad),
) {
    val varsayilanNumara: Numara? get() = numaralar.firstOrNull { it.varsayilan } ?: numaralar.firstOrNull()
}
