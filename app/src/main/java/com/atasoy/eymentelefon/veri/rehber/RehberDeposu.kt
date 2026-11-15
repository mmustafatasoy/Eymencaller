package com.atasoy.eymentelefon.veri.rehber

// ✅ HAZIR (Claude tamamladı). İmzalar sabit.
// Kaynak: ContactsContract · değişince yeniden yayın · IO thread · izin yoksa boş liste.

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Note
import android.provider.ContactsContract.CommonDataKinds.Organization
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.PhoneLookup
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.veri.icerikDegisimi
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.model.Numara
import com.atasoy.eymentelefon.veri.model.NumaraTuru
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RehberDeposu(private val context: Context) {

    private val cozucu get() = context.contentResolver

    /** Numarası olan tüm kişiler, Türkçe sıralı. */
    fun kisiler(): Flow<List<Kisi>> =
        context.icerikDegisimi(Contacts.CONTENT_URI, ETIKET)
            .map { tumKisileriOku() }
            .flowOn(Dispatchers.IO)

    /** STARRED = 1 olanlar. */
    fun favoriler(): Flow<List<Kisi>> =
        context.icerikDegisimi(Contacts.CONTENT_URI, ETIKET)
            .map { tumKisileriOku().filter { it.favori } }
            .flowOn(Dispatchers.IO)

    /** Tüm alanlarıyla tek kişi. */
    suspend fun kisi(id: Long): Kisi? = withContext(Dispatchers.IO) {
        guvenli("kisi") {
            var ad: String? = null
            var lookup = ""
            var foto: Uri? = null
            var favori = false
            cozucu.query(
                ContentUris.withAppendedId(Contacts.CONTENT_URI, id),
                arrayOf(Contacts.DISPLAY_NAME_PRIMARY, Contacts.LOOKUP_KEY, Contacts.PHOTO_URI, Contacts.STARRED),
                null, null, null,
            )?.use { c ->
                if (c.moveToFirst()) {
                    ad = c.str(Contacts.DISPLAY_NAME_PRIMARY)
                    lookup = c.str(Contacts.LOOKUP_KEY).orEmpty()
                    foto = c.str(Contacts.PHOTO_URI)?.let(Uri::parse)
                    favori = c.int(Contacts.STARRED) == 1
                }
            }
            val kisiAdi = ad ?: return@guvenli null

            val numaralar = mutableListOf<Numara>()
            var eposta: String? = null
            var sirket: String? = null
            var not: String? = null
            cozucu.query(
                Data.CONTENT_URI,
                arrayOf(Data.MIMETYPE, Data.DATA1, Data.DATA2, Data.DATA3, Data.IS_SUPER_PRIMARY),
                "${Data.CONTACT_ID} = ?",
                arrayOf(id.toString()),
                null,
            )?.use { c ->
                while (c.moveToNext()) {
                    val veri = c.str(Data.DATA1)?.takeIf { it.isNotBlank() } ?: continue
                    when (c.str(Data.MIMETYPE)) {
                        Phone.CONTENT_ITEM_TYPE -> numaralar += Numara(
                            numara = veri,
                            tur = turCevir(c.int(Data.DATA2)),
                            ozelEtiket = c.str(Data.DATA3),
                            varsayilan = c.int(Data.IS_SUPER_PRIMARY) == 1,
                        )
                        Email.CONTENT_ITEM_TYPE -> if (eposta == null) eposta = veri
                        Organization.CONTENT_ITEM_TYPE -> if (sirket == null) sirket = veri
                        Note.CONTENT_ITEM_TYPE -> if (not == null) not = veri
                    }
                }
            }
            Kisi(
                id = id,
                lookupKey = lookup,
                ad = kisiAdi,
                numaralar = tekillestir(numaralar),
                fotoUri = foto,
                favori = favori,
                sirket = sirket,
                eposta = eposta,
                not = not,
            )
        }
    }

    /** Arayanın adını bulmak için. */
    suspend fun numaradanKisi(numara: String): Kisi? = withContext(Dispatchers.IO) {
        if (numara.isBlank()) return@withContext null
        guvenli("numaradanKisi") {
            cozucu.query(
                Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(numara)),
                arrayOf(
                    PhoneLookup._ID, PhoneLookup.DISPLAY_NAME, PhoneLookup.LOOKUP_KEY,
                    PhoneLookup.PHOTO_THUMBNAIL_URI, PhoneLookup.TYPE, PhoneLookup.LABEL,
                    PhoneLookup.NUMBER, PhoneLookup.STARRED,
                ),
                null, null, null,
            )?.use { c ->
                if (!c.moveToFirst()) return@use null
                Kisi(
                    id = c.long(PhoneLookup._ID),
                    lookupKey = c.str(PhoneLookup.LOOKUP_KEY).orEmpty(),
                    ad = c.str(PhoneLookup.DISPLAY_NAME) ?: numara,
                    numaralar = listOf(
                        Numara(
                            numara = c.str(PhoneLookup.NUMBER) ?: numara,
                            tur = turCevir(c.int(PhoneLookup.TYPE)),
                            ozelEtiket = c.str(PhoneLookup.LABEL),
                            varsayilan = true,
                        ),
                    ),
                    fotoUri = c.str(PhoneLookup.PHOTO_THUMBNAIL_URI)?.let(Uri::parse),
                    favori = c.int(PhoneLookup.STARRED) == 1,
                )
            }
        }
    }

    /** ContactsContract.Profile — yoksa null. */
    suspend fun benimKartim(): Kisi? = withContext(Dispatchers.IO) {
        guvenli("benimKartim") {
            cozucu.query(
                ContactsContract.Profile.CONTENT_URI,
                arrayOf(Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY, Contacts.LOOKUP_KEY, Contacts.PHOTO_THUMBNAIL_URI),
                null, null, null,
            )?.use { c ->
                if (!c.moveToFirst()) return@use null
                val ad = c.str(Contacts.DISPLAY_NAME_PRIMARY)?.takeIf { it.isNotBlank() } ?: return@use null
                Kisi(
                    id = c.long(Contacts._ID),
                    lookupKey = c.str(Contacts.LOOKUP_KEY).orEmpty(),
                    ad = ad,
                    numaralar = emptyList(),
                    fotoUri = c.str(Contacts.PHOTO_THUMBNAIL_URI)?.let(Uri::parse),
                )
            }
        }
    }

    suspend fun favoriYap(id: Long, favori: Boolean) {
        withContext(Dispatchers.IO) {
            guvenli("favoriYap") {
                val degerler = ContentValues().apply { put(Contacts.STARRED, if (favori) 1 else 0) }
                cozucu.update(ContentUris.withAppendedId(Contacts.CONTENT_URI, id), degerler, null, null)
            }
        }
        com.atasoy.eymentelefon.widget.FavoriWidget.guncelle(context) // V13-10
    }

    suspend fun sil(id: Long) {
        withContext(Dispatchers.IO) {
            guvenli("sil") {
                val kisi = kisi(id) ?: return@guvenli null
                val uri = Contacts.getLookupUri(id, kisi.lookupKey)
                    ?: ContentUris.withAppendedId(Contacts.CONTENT_URI, id)
                cozucu.delete(uri, null, null)
            }
        }
    }

    /** Sistem ekranları için (düzenle / paylaş). */
    fun kisiUri(kisi: Kisi): Uri =
        Contacts.getLookupUri(kisi.id, kisi.lookupKey) ?: ContentUris.withAppendedId(Contacts.CONTENT_URI, kisi.id)

    fun vcardUri(kisi: Kisi): Uri = Uri.withAppendedPath(Contacts.CONTENT_VCARD_URI, kisi.lookupKey)

    // ---------------- İç ----------------

    private fun tumKisileriOku(): List<Kisi> = guvenli("kisiler") {
        val harita = LinkedHashMap<Long, Ham>()
        cozucu.query(
            Phone.CONTENT_URI,
            arrayOf(
                Phone.CONTACT_ID, Phone.DISPLAY_NAME_PRIMARY, Phone.LOOKUP_KEY, Phone.PHOTO_THUMBNAIL_URI,
                Phone.STARRED, Phone.NUMBER, Phone.TYPE, Phone.LABEL, Phone.IS_SUPER_PRIMARY,
            ),
            null, null, null,
        )?.use { c ->
            while (c.moveToNext()) {
                val id = c.long(Phone.CONTACT_ID)
                val numara = c.str(Phone.NUMBER)?.takeIf { it.isNotBlank() } ?: continue
                val ham = harita.getOrPut(id) {
                    Ham(
                        id = id,
                        ad = c.str(Phone.DISPLAY_NAME_PRIMARY)?.takeIf { it.isNotBlank() } ?: numara,
                        lookup = c.str(Phone.LOOKUP_KEY).orEmpty(),
                        foto = c.str(Phone.PHOTO_THUMBNAIL_URI)?.let(Uri::parse),
                        favori = c.int(Phone.STARRED) == 1,
                        numaralar = mutableListOf(),
                    )
                }
                ham.numaralar += Numara(
                    numara = numara,
                    tur = turCevir(c.int(Phone.TYPE)),
                    ozelEtiket = c.str(Phone.LABEL),
                    varsayilan = c.int(Phone.IS_SUPER_PRIMARY) == 1,
                )
            }
        }
        harita.values
            .map { Kisi(it.id, it.lookup, it.ad, tekillestir(it.numaralar), it.foto, it.favori) }
            .sortedWith(compareBy(TurkceMetin.karsilastirici) { it.ad })
    } ?: emptyList()

    /** Aynı numara farklı biçimlerde birden çok kez kayıtlıysa teke indirir. */
    private fun tekillestir(liste: List<Numara>): List<Numara> =
        liste.distinctBy { NumaraBicimleyici.normalize(it.numara).ifEmpty { it.numara } }

    private fun turCevir(tip: Int): NumaraTuru = when (tip) {
        Phone.TYPE_MOBILE, Phone.TYPE_WORK_MOBILE -> NumaraTuru.MOBIL
        Phone.TYPE_HOME -> NumaraTuru.EV
        Phone.TYPE_WORK, Phone.TYPE_COMPANY_MAIN -> NumaraTuru.IS
        else -> NumaraTuru.DIGER
    }

    private inline fun <T> guvenli(islem: String, blok: () -> T?): T? = try {
        blok()
    } catch (e: SecurityException) {
        HataKaydedici.kaydet("$ETIKET.$islem", e)
        null
    } catch (e: IllegalArgumentException) {
        HataKaydedici.kaydet("$ETIKET.$islem", e)
        null
    }

    private class Ham(
        val id: Long,
        val ad: String,
        val lookup: String,
        val foto: Uri?,
        val favori: Boolean,
        val numaralar: MutableList<Numara>,
    )

    private companion object {
        const val ETIKET = "RehberDeposu"
    }
}

internal fun Cursor.str(kolon: String): String? {
    val i = getColumnIndex(kolon)
    return if (i < 0 || isNull(i)) null else getString(i)
}

internal fun Cursor.int(kolon: String): Int {
    val i = getColumnIndex(kolon)
    return if (i < 0 || isNull(i)) 0 else getInt(i)
}

internal fun Cursor.long(kolon: String): Long {
    val i = getColumnIndex(kolon)
    return if (i < 0 || isNull(i)) 0L else getLong(i)
}
