package com.atasoy.eymentelefon.veri.gecmis

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.veri.model.AramaGrubu
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import com.atasoy.eymentelefon.veri.model.AramaTuru
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** Android CallLog üzerinde çalışır; cihaz arama verilerini uygulama veritabanında saklamaz. */
class AramaGecmisiDeposu(private val context: Context) {

    fun kayitlar(): Flow<List<AramaKaydi>> = degisiklikler()
        .map { oku() }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    /** Art arda aynı numara + aynı tür kayıtları birleştirir → (n). Saf fonksiyon. */
    fun grupla(kayitlar: List<AramaKaydi>): List<AramaGrubu> {
        val sonuc = mutableListOf<AramaGrubu>()
        for (kayit in kayitlar) {
            val son = sonuc.lastOrNull()
            val ilk = son?.ilk
            val ayniNumara = ilk != null &&
                NumaraBicimleyici.normalize(kayit.numara).isNotBlank() &&
                NumaraBicimleyici.normalize(kayit.numara) ==
                    NumaraBicimleyici.normalize(ilk.numara)
            if (ilk != null && ayniNumara && ilk.tur == kayit.tur) {
                sonuc[sonuc.lastIndex] = son.copy(kayitIdleri = son.kayitIdleri + kayit.id)
            } else {
                sonuc.add(AramaGrubu(ilk = kayit, kayitIdleri = listOf(kayit.id)))
            }
        }
        return sonuc
    }

    /** Kişi detayı kartında aynı numaranın tüm kayıtları (ülke kodu dikkate alınır). */
    fun numaraKayitlari(numara: String): Flow<List<AramaKaydi>> {
        val hedef = NumaraBicimleyici.normalize(numara)
        return kayitlar().map { liste ->
            if (hedef.isBlank()) emptyList()
            else liste.filter { NumaraBicimleyici.normalize(it.numara) == hedef }
        }.distinctUntilChanged()
    }

    /** Son giden aramanın numarası. */
    suspend fun sonArananNumara(): String? = withContext(Dispatchers.IO) {
        if (!okumaIzniVar()) return@withContext null
                try {
            val uri = CallLog.Calls.CONTENT_URI.buildUpon()
                .appendQueryParameter(CallLog.Calls.LIMIT_PARAM_KEY, "1").build()
            context.contentResolver.query(
                uri, arrayOf(CallLog.Calls.NUMBER), "${CallLog.Calls.TYPE} = ?",
                arrayOf(CallLog.Calls.OUTGOING_TYPE.toString()), "${CallLog.Calls.DATE} DESC",
            )?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0)?.takeIf(String::isNotBlank)
                else null
            }
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("AramaGecmisiDeposu.sonArananNumara", hata)
            null
        }
    }

    suspend fun sil(kayitIdleri: List<Long>) {
        withContext(Dispatchers.IO) {
            if (!yazmaIzniVar() || kayitIdleri.isEmpty()) return@withContext
            try {
                for (parca in kayitIdleri.distinct().chunked(200)) {
                    context.contentResolver.delete(
                        CallLog.Calls.CONTENT_URI,
                        "${CallLog.Calls._ID} IN (${List(parca.size) { "?" }.joinToString(",")})",
                        parca.map(Long::toString).toTypedArray(),
                    )
                }
            } catch (hata: SecurityException) {
                HataKaydedici.kaydet("AramaGecmisiDeposu.sil", hata)
            }
        }
    }

    /** NEW=0 ve IS_READ=1; sadece cevapsız aramalar. */
    suspend fun cevapsizlariGorulduYap() {
        withContext(Dispatchers.IO) {
            if (!yazmaIzniVar()) return@withContext
            try {
                context.contentResolver.update(
                    CallLog.Calls.CONTENT_URI,
                    ContentValues().apply {
                        put(CallLog.Calls.NEW, 0)
                        put(CallLog.Calls.IS_READ, 1)
                    },
                    "${CallLog.Calls.TYPE} = ? AND (${CallLog.Calls.NEW} = 1 OR ${CallLog.Calls.IS_READ} = 0)",
                    arrayOf(CallLog.Calls.MISSED_TYPE.toString()),
                )
            } catch (hata: SecurityException) {
                HataKaydedici.kaydet("AramaGecmisiDeposu.cevapsizlariGorulduYap", hata)
            }
        }
    }

    private fun okumaIzniVar() = context.konteyner().izinYoneticisi
        .izinVarMi(Manifest.permission.READ_CALL_LOG)
    private fun yazmaIzniVar() = context.konteyner().izinYoneticisi
        .izinVarMi(Manifest.permission.WRITE_CALL_LOG)

    private fun degisiklikler(): Flow<Unit> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) { trySend(Unit) }
        }
        val kayitli = try {
            context.contentResolver.registerContentObserver(
                CallLog.Calls.CONTENT_URI, true, observer
            )
            true
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("AramaGecmisiDeposu.observer", hata)
            false
        }
        trySend(Unit)
        awaitClose {
            if (kayitli) {
                try {
                    context.contentResolver.unregisterContentObserver(observer)
                } catch (hata: IllegalStateException) {
                    HataKaydedici.kaydet("AramaGecmisiDeposu.observerKapat", hata)
                }
            }
        }
    }

    private fun oku(): List<AramaKaydi> {
        if (!okumaIzniVar()) return emptyList()
        return try {
            val uri = CallLog.Calls.CONTENT_URI.buildUpon()
                .appendQueryParameter(CallLog.Calls.LIMIT_PARAM_KEY, "500").build()
            val kayitlar = ArrayList<AramaKaydi>()
            context.contentResolver.query(
                uri,
                arrayOf(
                    CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE,
                    CallLog.Calls.DURATION, CallLog.Calls.GEOCODED_LOCATION, CallLog.Calls.CACHED_NUMBER_LABEL,
                    CallLog.Calls.NEW, CallLog.Calls.IS_READ, CallLog.Calls.PHONE_ACCOUNT_ID,
                ),
                null, null, "${CallLog.Calls.DATE} DESC",
            )?.use { cursor ->
                while (cursor.moveToNext() && kayitlar.size < 500) {
                    val tur = when (cursor.getInt(3)) {
                        CallLog.Calls.INCOMING_TYPE -> AramaTuru.GELEN
                        CallLog.Calls.OUTGOING_TYPE -> AramaTuru.GIDEN
                        CallLog.Calls.MISSED_TYPE -> AramaTuru.CEVAPSIZ
                        CallLog.Calls.REJECTED_TYPE -> AramaTuru.REDDEDILEN
                        CallLog.Calls.BLOCKED_TYPE -> AramaTuru.ENGELLENEN
                        CallLog.Calls.VOICEMAIL_TYPE -> AramaTuru.SESLI_MESAJ
                        else -> null
                    } ?: continue
                    val konum = cursor.getString(6)?.takeIf(String::isNotBlank)
                    val etiket = cursor.getString(7)?.takeIf(String::isNotBlank)
                    kayitlar.add(
                        AramaKaydi(
                            id = cursor.getLong(0),
                            numara = cursor.getString(1).orEmpty(),
                            ad = cursor.getString(2)?.takeIf(String::isNotBlank),
                            kisiId = null, // CallLog'da güvenilir Contacts._ID alanı yok.
                            tur = tur,
                            tarih = cursor.getLong(4),
                            sureSn = cursor.getLong(5),
                            altBilgi = etiket ?: konum,
                            yeni = tur == AramaTuru.CEVAPSIZ &&
                                cursor.getInt(8) != 0 && cursor.getInt(9) == 0,
                            hesapId = cursor.getString(10),
                        )
                    )
                }
            }
            kayitlar
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("AramaGecmisiDeposu.oku", hata)
            emptyList()
        }
    }
}
