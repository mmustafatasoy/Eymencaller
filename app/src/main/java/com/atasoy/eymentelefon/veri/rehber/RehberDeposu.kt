package com.atasoy.eymentelefon.veri.rehber

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.model.Kisi
import com.atasoy.eymentelefon.veri.model.Numara
import com.atasoy.eymentelefon.veri.model.NumaraTuru
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * ContactsContract kaynağını okur, veriyi özel veritabanına kopyalamaz.
 * Kişiler ve Data satırları toplu sorgulanır (kişi başına ayrı sorgu yok).
 */
class RehberDeposu(private val context: Context) {

    /** Numarası olan tüm kişiler, Türkçe sıralı. */
    fun kisiler(): Flow<List<Kisi>> = degisiklikler()
        .map { tumunuOku(favoriOnly = false) }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    /** STARRED = 1 olanlar. */
    fun favoriler(): Flow<List<Kisi>> = degisiklikler()
        .map { tumunuOku(favoriOnly = true) }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    /** Tüm alanlarıyla tek kişi (Kişi detayı ekranı). */
    suspend fun kisi(id: Long): Kisi? = withContext(Dispatchers.IO) {
        if (!okumaIzniVar()) return@withContext null
        try {
            kisileriOku(
                ContactsContract.Contacts.CONTENT_URI,
                "${ContactsContract.Contacts._ID} = ?",
                arrayOf(id.toString()),
            ).firstOrNull()
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("RehberDeposu.kisi", hata)
            null
        }
    }

    /** Arayanın adını bulmak için PhoneLookup.CONTENT_FILTER_URI kullan. */
    suspend fun numaradanKisi(numara: String): Kisi? = withContext(Dispatchers.IO) {
        if (!okumaIzniVar() || numara.isBlank()) return@withContext null
        try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(numara)
            )
            val id = context.contentResolver.query(
                uri, arrayOf(ContactsContract.PhoneLookup._ID), null, null, null
            )?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getLong(0) else null
            }
            if (id != null) kisi(id) else null
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("RehberDeposu.numaradanKisi", hata)
            null
        }
    }

    /** ContactsContract.Profile — yoksa null. */
    suspend fun benimKartim(): Kisi? = withContext(Dispatchers.IO) {
        if (!okumaIzniVar()) return@withContext null
        try {
            kisileriOku(ContactsContract.Profile.CONTENT_URI, null, null).firstOrNull()
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("RehberDeposu.benimKartim", hata)
            null
        }
    }

    suspend fun favoriYap(id: Long, favori: Boolean) {
        withContext(Dispatchers.IO) {
            if (!yazmaIzniVar()) return@withContext
            try {
                context.contentResolver.update(
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id),
                    ContentValues().apply { put(ContactsContract.Contacts.STARRED, if (favori) 1 else 0) },
                    null, null,
                )
            } catch (hata: SecurityException) {
                HataKaydedici.kaydet("RehberDeposu.favoriYap", hata)
            }
        }
    }

    suspend fun sil(id: Long) {
        withContext(Dispatchers.IO) {
            if (!yazmaIzniVar()) return@withContext
            try {
                context.contentResolver.delete(
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id), null, null
                )
            } catch (hata: SecurityException) {
                HataKaydedici.kaydet("RehberDeposu.sil", hata)
            }
        }
    }

    private fun okumaIzniVar() = context.konteyner().izinYoneticisi
        .izinVarMi(Manifest.permission.READ_CONTACTS)

    private fun yazmaIzniVar() = context.konteyner().izinYoneticisi
        .izinVarMi(Manifest.permission.WRITE_CONTACTS)

    private fun degisiklikler(): Flow<Unit> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(Unit)
            }
        }
        val kayitli = try {
            context.contentResolver.registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI, true, observer
            )
            true
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("RehberDeposu.observer", hata)
            false
        }
        trySend(Unit)
        awaitClose {
            if (kayitli) {
                try {
                    context.contentResolver.unregisterContentObserver(observer)
                } catch (hata: IllegalStateException) {
                    HataKaydedici.kaydet("RehberDeposu.observerKapat", hata)
                }
            }
        }
    }

    private fun tumunuOku(favoriOnly: Boolean): List<Kisi> {
        if (!okumaIzniVar()) return emptyList()
        val kosul = buildString {
            append("${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1")
            if (favoriOnly) append(" AND ${ContactsContract.Contacts.STARRED} = 1")
        }
        return try {
            kisileriOku(ContactsContract.Contacts.CONTENT_URI, kosul, null)
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("RehberDeposu.tumunuOku", hata)
            emptyList()
        }
    }

    private data class KisiSatiri(
        val id: Long,
        val lookupKey: String,
        val ad: String,
        val foto: Uri?,
        val favori: Boolean,
        val numaralar: MutableList<Numara> = mutableListOf(),
        var sirket: String? = null,
        var eposta: String? = null,
        var not: String? = null,
    ) {
        fun kisi(): Kisi = Kisi(
            id = id, lookupKey = lookupKey, ad = ad,
            numaralar = numaralar.toList(), fotoUri = foto, favori = favori,
            sirket = sirket, eposta = eposta, not = not,
        )
    }

    /** Çağrıldığı akış IO iş parçacığındadır; ayrıca suspend fonksiyonlarda withContext(IO) var. */
    private fun kisileriOku(
        uri: Uri,
        kosul: String?,
        argumanlar: Array<String>?,
    ): List<Kisi> {
                val satirlar = linkedMapOf<Long, KisiSatiri>()
        val sutunlar = arrayOf(
            ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, ContactsContract.Contacts.PHOTO_URI, ContactsContract.Contacts.STARRED
        )
        context.contentResolver.query(uri, sutunlar, kosul, argumanlar, null)?.use { cursor ->
            val idI = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            val lookupI = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)
            val adI = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val fotoI = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)
            val favoriI = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.STARRED)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idI)
                val ad = cursor.getString(adI).orEmpty()
                val foto = cursor.getString(fotoI)?.let(Uri::parse)
                satirlar[id] = KisiSatiri(
                    id, cursor.getString(lookupI).orEmpty(),
                    ad.ifBlank { id.toString() }, foto, cursor.getInt(favoriI) == 1
                )
            }
        }
        if (satirlar.isEmpty()) return emptyList()

        // Android SQLite binder parametre sınırı için 200 kimliklik parçalar.
                val turler = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE,
        )
        for (ids in satirlar.keys.toList().chunked(200)) {
            val idYerTutucular = List(ids.size) { "?" }.joinToString(",")
            val veriKosulu = "${ContactsContract.Data.CONTACT_ID} IN ($idYerTutucular) AND ${ContactsContract.Data.MIMETYPE} IN (?,?,?,?)"
            val degerler = ids.map(Long::toString) + turler.toList()
            context.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.Data.CONTACT_ID, ContactsContract.Data.MIMETYPE, ContactsContract.Data.DATA1, ContactsContract.Data.DATA2, ContactsContract.Data.DATA3,
                    ContactsContract.Data.IS_PRIMARY, ContactsContract.Data.IS_SUPER_PRIMARY),
                veriKosulu,
                degerler.toTypedArray(), null,
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val satir = satirlar[cursor.getLong(0)] ?: continue
                    val tur = cursor.getString(1)
                    val value = cursor.getString(2)
                    when (tur) {
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
                            if (value.isNullOrBlank()) continue
                            val phone = ContactsContract.CommonDataKinds.Phone
                            val tip = cursor.getInt(3)
                            val tipAdi = when (tip) {
                                phone.TYPE_MOBILE -> NumaraTuru.MOBIL
                                phone.TYPE_HOME -> NumaraTuru.EV
                                phone.TYPE_WORK, phone.TYPE_WORK_MOBILE -> NumaraTuru.IS
                                else -> NumaraTuru.DIGER
                            }
                            val ozel = if (tip == phone.TYPE_CUSTOM) cursor.getString(4) else null
                            satir.numaralar.add(Numara(
                                numara = value, tur = tipAdi, ozelEtiket = ozel,
                                varsayilan = cursor.getInt(6) != 0 || cursor.getInt(5) != 0,
                            ))
                        }
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE ->
                            if (satir.eposta == null) satir.eposta = value
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE ->
                            if (satir.sirket == null) satir.sirket = value
                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE ->
                            if (satir.not == null) satir.not = value
                    }
                }
            }
        }
        return satirlar.values.asSequence()
            .map(KisiSatiri::kisi)
            .sortedWith { a, b -> TurkceMetin.karsilastirici.compare(a.ad, b.ad) }
            .toList()
    }
}
