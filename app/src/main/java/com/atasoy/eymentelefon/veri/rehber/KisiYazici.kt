package com.atasoy.eymentelefon.veri.rehber

// V12-10 · Uygulama içinde kişi ekleme / düzenleme (docs/07_V12_PLANI.md, görsel v12_08)
// ContactsContract toplu işlem: RawContacts + StructuredName + Phone + Email + Organization + Photo.
// Düzenlemede kişinin ilk ham kaydındaki bu türler silinip yeniden yazılır (diğer veriler — not, adres… — korunur).

import android.content.ContentProviderOperation
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Organization
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Photo
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.RawContacts
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.veri.model.NumaraTuru
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

data class TaslakNumara(val numara: String = "", val tur: NumaraTuru = NumaraTuru.MOBIL)

data class KisiTaslak(
    /** -1 → yeni kişi */
    val kisiId: Long = -1,
    val lookupKey: String? = null,
    val ad: String = "",
    val soyad: String = "",
    val sirket: String = "",
    val telefonlar: List<TaslakNumara> = emptyList(),
    val epostalar: List<String> = emptyList(),
    /** Kayıtlı fotoğraf (gösterim için) */
    val fotoUri: Uri? = null,
) {
    val bosMu: Boolean get() =
        ad.isBlank() && soyad.isBlank() && sirket.isBlank() && telefonlar.all { it.numara.isBlank() } && epostalar.all { it.isBlank() }
}

/** Fotoğraf değişikliği: değişmedi / yeni seçildi / kaldırıldı */
sealed interface FotoDegisimi {
    data object Yok : FotoDegisimi
    data class Yeni(val uri: Uri) : FotoDegisimi
    data object Kaldir : FotoDegisimi
}

class KisiYazici(context: Context) {
    private val cozucu = context.applicationContext.contentResolver

    /** Düzenleme formu için mevcut kişiyi okur. */
    suspend fun oku(kisiId: Long): KisiTaslak? = withContext(Dispatchers.IO) {
        runCatching {
            var lookup: String? = null
            var foto: Uri? = null
            cozucu.query(
                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, kisiId),
                arrayOf(ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.PHOTO_URI),
                null, null, null,
            )?.use { c ->
                if (!c.moveToFirst()) return@runCatching null
                lookup = c.getString(0)
                foto = c.getString(1)?.let(Uri::parse)
            }
            var t = KisiTaslak(kisiId = kisiId, lookupKey = lookup, fotoUri = foto)
            val tel = mutableListOf<TaslakNumara>()
            val posta = mutableListOf<String>()
            cozucu.query(
                Data.CONTENT_URI,
                arrayOf(Data.MIMETYPE, Data.DATA1, Data.DATA2, Data.DATA3),
                "${Data.CONTACT_ID} = ?",
                arrayOf(kisiId.toString()),
                null,
            )?.use { c ->
                while (c.moveToNext()) {
                    when (c.getString(0)) {
                        StructuredName.CONTENT_ITEM_TYPE -> t = t.copy(ad = c.getString(2).orEmpty(), soyad = c.getString(3).orEmpty())
                        Phone.CONTENT_ITEM_TYPE -> c.getString(1)?.let { tel += TaslakNumara(it, turCevir(c.getInt(2))) }
                        Email.CONTENT_ITEM_TYPE -> c.getString(1)?.let { posta += it }
                        Organization.CONTENT_ITEM_TYPE -> t = t.copy(sirket = c.getString(1).orEmpty())
                    }
                }
            }
            t.copy(telefonlar = tel.distinctBy { it.numara }, epostalar = posta.distinct())
        }.onFailure { HataKaydedici.kaydet("KisiYazici.oku", it) }.getOrNull()
    }

    /** Kaydeder; kişinin (Contacts._ID) kimliğini döner. Hata → null. */
    suspend fun kaydet(t: KisiTaslak, foto: FotoDegisimi): Long? = withContext(Dispatchers.IO) {
        runCatching {
            val islemler = ArrayList<ContentProviderOperation>()
            val hamId: Long? = if (t.kisiId > 0) ilkHamKayit(t.kisiId) else null
            val fotoBayt = (foto as? FotoDegisimi.Yeni)?.let { fotoHazirla(it.uri) }

            // Hedef ham kayıt: düzenlemede mevcut, yenide geri referans (0)
            fun ekle(mime: String): ContentProviderOperation.Builder {
                val b = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, mime)
                return if (hamId != null) b.withValue(Data.RAW_CONTACT_ID, hamId) else b.withValueBackReference(Data.RAW_CONTACT_ID, 0)
            }

            if (hamId == null) {
                islemler += ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                    .withValue(RawContacts.ACCOUNT_NAME, null)
                    .build()
            } else {
                val silinecek = mutableListOf(StructuredName.CONTENT_ITEM_TYPE, Phone.CONTENT_ITEM_TYPE, Email.CONTENT_ITEM_TYPE, Organization.CONTENT_ITEM_TYPE)
                if (foto != FotoDegisimi.Yok) silinecek += Photo.CONTENT_ITEM_TYPE
                silinecek.forEach { mime ->
                    islemler += ContentProviderOperation.newDelete(Data.CONTENT_URI)
                        .withSelection("${Data.RAW_CONTACT_ID} = ? AND ${Data.MIMETYPE} = ?", arrayOf(hamId.toString(), mime))
                        .build()
                }
            }

            if (t.ad.isNotBlank() || t.soyad.isNotBlank()) {
                islemler += ekle(StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.GIVEN_NAME, t.ad.trim())
                    .withValue(StructuredName.FAMILY_NAME, t.soyad.trim())
                    .build()
            }
            t.telefonlar.filter { it.numara.isNotBlank() }.forEach { n ->
                islemler += ekle(Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, n.numara.trim())
                    .withValue(Phone.TYPE, turKodu(n.tur))
                    .build()
            }
            t.epostalar.filter { it.isNotBlank() }.forEach { e ->
                islemler += ekle(Email.CONTENT_ITEM_TYPE)
                    .withValue(Email.ADDRESS, e.trim())
                    .withValue(Email.TYPE, Email.TYPE_OTHER)
                    .build()
            }
            if (t.sirket.isNotBlank()) {
                islemler += ekle(Organization.CONTENT_ITEM_TYPE).withValue(Organization.COMPANY, t.sirket.trim()).build()
            }
            if (fotoBayt != null) {
                islemler += ekle(Photo.CONTENT_ITEM_TYPE).withValue(Photo.PHOTO, fotoBayt).build()
            }

            val sonuc = cozucu.applyBatch(ContactsContract.AUTHORITY, islemler)
            if (hamId != null) {
                t.kisiId
            } else {
                val yeniHam = sonuc.firstOrNull()?.uri?.let(ContentUris::parseId) ?: return@runCatching null
                kisiKimligi(yeniHam)
            }
        }.onFailure { HataKaydedici.kaydet("KisiYazici.kaydet", it) }.getOrNull()
    }

    private fun ilkHamKayit(kisiId: Long): Long? =
        cozucu.query(
            RawContacts.CONTENT_URI,
            arrayOf(RawContacts._ID),
            "${RawContacts.CONTACT_ID} = ? AND ${RawContacts.DELETED} = 0",
            arrayOf(kisiId.toString()),
            null,
        )?.use { c -> if (c.moveToFirst()) c.getLong(0) else null }

    private fun kisiKimligi(hamId: Long): Long? =
        cozucu.query(
            ContentUris.withAppendedId(RawContacts.CONTENT_URI, hamId),
            arrayOf(RawContacts.CONTACT_ID),
            null, null, null,
        )?.use { c -> if (c.moveToFirst()) c.getLong(0).takeIf { it > 0 } else null }

    /** En uzun kenar 720px, JPEG %90 */
    private fun fotoHazirla(uri: Uri): ByteArray? {
        val sinir = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        cozucu.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, sinir) }
        var ornek = 1
        while (maxOf(sinir.outWidth, sinir.outHeight) / (ornek * 2) >= FOTO_KENAR) ornek *= 2
        val bmp = cozucu.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = ornek })
        } ?: return null
        val olcek = FOTO_KENAR.toFloat() / maxOf(bmp.width, bmp.height)
        val son = if (olcek < 1f) Bitmap.createScaledBitmap(bmp, (bmp.width * olcek).toInt(), (bmp.height * olcek).toInt(), true) else bmp
        return ByteArrayOutputStream().use { cikti ->
            son.compress(Bitmap.CompressFormat.JPEG, FOTO_KALITE, cikti)
            cikti.toByteArray()
        }
    }

    private fun turCevir(tip: Int): NumaraTuru = when (tip) {
        Phone.TYPE_MOBILE -> NumaraTuru.MOBIL
        Phone.TYPE_HOME -> NumaraTuru.EV
        Phone.TYPE_WORK, Phone.TYPE_WORK_MOBILE -> NumaraTuru.IS
        else -> NumaraTuru.DIGER
    }

    private fun turKodu(tur: NumaraTuru): Int = when (tur) {
        NumaraTuru.MOBIL -> Phone.TYPE_MOBILE
        NumaraTuru.EV -> Phone.TYPE_HOME
        NumaraTuru.IS -> Phone.TYPE_WORK
        NumaraTuru.DIGER -> Phone.TYPE_OTHER
    }

    private companion object {
        const val FOTO_KENAR = 720
        const val FOTO_KALITE = 90
    }
}
