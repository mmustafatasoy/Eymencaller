package com.atasoy.eymentelefon.veri.gecmis

// ✅ HAZIR (Claude tamamladı). İmzalar sabit.
// Kaynak: CallLog.Calls · değişince yeniden yayın · IO thread · izin yoksa boş liste.

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.provider.CallLog
import android.telecom.TelecomManager
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.cekirdek.yardimci.ZamanBicimleyici
import com.atasoy.eymentelefon.veri.icerikDegisimi
import com.atasoy.eymentelefon.veri.model.AramaGrubu
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import com.atasoy.eymentelefon.veri.model.AramaTuru
import com.atasoy.eymentelefon.veri.rehber.int
import com.atasoy.eymentelefon.veri.rehber.long
import com.atasoy.eymentelefon.veri.rehber.str
import android.provider.ContactsContract.CommonDataKinds.Phone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AramaGecmisiDeposu(private val context: Context) {

    private val cozucu get() = context.contentResolver

    fun kayitlar(): Flow<List<AramaKaydi>> =
        context.icerikDegisimi(CallLog.Calls.CONTENT_URI, ETIKET)
            .map { oku() }
            .flowOn(Dispatchers.IO)

    /** Art arda aynı numara + aynı tür + aynı gün → tek satır (n). */
    fun grupla(kayitlar: List<AramaKaydi>): List<AramaGrubu> {
        val sonuc = mutableListOf<AramaGrubu>()
        var ilk: AramaKaydi? = null
        val idler = mutableListOf<Long>()
        for (k in kayitlar) {
            val bas = ilk
            val ayni = bas != null &&
                NumaraBicimleyici.normalize(bas.numara).ifEmpty { bas.numara } ==
                NumaraBicimleyici.normalize(k.numara).ifEmpty { k.numara } &&
                bas.tur == k.tur &&
                ZamanBicimleyici.grup(bas.tarih) == ZamanBicimleyici.grup(k.tarih)
            if (ayni) {
                idler += k.id
            } else {
                if (bas != null) sonuc += AramaGrubu(bas, idler.toList())
                ilk = k
                idler.clear()
                idler += k.id
            }
        }
        ilk?.let { sonuc += AramaGrubu(it, idler.toList()) }
        return sonuc
    }

    fun numaraKayitlari(numara: String): Flow<List<AramaKaydi>> =
        kayitlar().map { liste -> liste.filter { NumaraBicimleyici.ayniMi(it.numara, numara) } }

    @SuppressLint("MissingPermission") // Zorunlu izinler açılışta kontrol ediliyor; SecurityException yakalanır
    suspend fun sonArananNumara(): String? = withContext(Dispatchers.IO) {
        try {
            CallLog.Calls.getLastOutgoingCall(context)?.takeIf { it.isNotBlank() }
        } catch (e: SecurityException) {
            HataKaydedici.kaydet("$ETIKET.sonArananNumara", e)
            null
        }
    }

    suspend fun sil(kayitIdleri: List<Long>) {
        if (kayitIdleri.isEmpty()) return
        withContext(Dispatchers.IO) {
            try {
                val yer = kayitIdleri.joinToString(",") { "?" }
                cozucu.delete(
                    CallLog.Calls.CONTENT_URI,
                    "${CallLog.Calls._ID} IN ($yer)",
                    kayitIdleri.map { it.toString() }.toTypedArray(),
                )
            } catch (e: SecurityException) {
                HataKaydedici.kaydet("$ETIKET.sil", e)
            }
        }
    }

    /** Son aramalar ekranına girilince: cevapsızlar okundu, sistem/uygulama bildirimi kapanır. */
    @SuppressLint("MissingPermission") // Varsayılan telefon uygulaması bu çağrıya yetkilidir
    suspend fun cevapsizlariGorulduYap() {
        withContext(Dispatchers.IO) {
            try {
                val degerler = ContentValues().apply {
                    put(CallLog.Calls.NEW, 0)
                    put(CallLog.Calls.IS_READ, 1)
                }
                cozucu.update(
                    CallLog.Calls.CONTENT_URI,
                    degerler,
                    "${CallLog.Calls.TYPE} = ? AND ${CallLog.Calls.NEW} = 1",
                    arrayOf(CallLog.Calls.MISSED_TYPE.toString()),
                )
                context.getSystemService(TelecomManager::class.java)?.cancelMissedCallsNotification()
            } catch (e: SecurityException) {
                HataKaydedici.kaydet("$ETIKET.cevapsizlariGorulduYap", e)
            }
        }
    }

    // ---------------- İç ----------------

    private fun oku(): List<AramaKaydi> = try {
        val liste = ArrayList<AramaKaydi>(SINIR)
        cozucu.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE,
                CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.CACHED_NUMBER_TYPE,
                CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.GEOCODED_LOCATION, CallLog.Calls.NEW,
                CallLog.Calls.PHONE_ACCOUNT_ID,
            ),
            null, null,
            "${CallLog.Calls.DATE} DESC",
        )?.use { c ->
            while (c.moveToNext() && liste.size < SINIR) {
                val ad = c.str(CallLog.Calls.CACHED_NAME)?.takeIf { it.isNotBlank() }
                val tur = turCevir(c.int(CallLog.Calls.TYPE))
                liste += AramaKaydi(
                    id = c.long(CallLog.Calls._ID),
                    numara = c.str(CallLog.Calls.NUMBER).orEmpty(),
                    ad = ad,
                    kisiId = null,
                    tur = tur,
                    tarih = c.long(CallLog.Calls.DATE),
                    sureSn = c.long(CallLog.Calls.DURATION),
                    altBilgi = if (ad != null) {
                        numaraTuruMetni(c.int(CallLog.Calls.CACHED_NUMBER_TYPE), c.str(CallLog.Calls.CACHED_NUMBER_LABEL))
                    } else {
                        c.str(CallLog.Calls.GEOCODED_LOCATION)?.takeIf { it.isNotBlank() }
                    },
                    yeni = tur == AramaTuru.CEVAPSIZ && c.int(CallLog.Calls.NEW) == 1,
                    hesapId = c.str(CallLog.Calls.PHONE_ACCOUNT_ID),
                )
            }
        }
        liste
    } catch (e: SecurityException) {
        HataKaydedici.kaydet("$ETIKET.oku", e)
        emptyList()
    }

    private fun turCevir(tip: Int): AramaTuru = when (tip) {
        CallLog.Calls.INCOMING_TYPE, CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> AramaTuru.GELEN
        CallLog.Calls.OUTGOING_TYPE -> AramaTuru.GIDEN
        CallLog.Calls.MISSED_TYPE -> AramaTuru.CEVAPSIZ
        CallLog.Calls.REJECTED_TYPE -> AramaTuru.REDDEDILEN
        CallLog.Calls.BLOCKED_TYPE -> AramaTuru.ENGELLENEN
        CallLog.Calls.VOICEMAIL_TYPE -> AramaTuru.SESLI_MESAJ
        else -> AramaTuru.GELEN
    }

    private fun numaraTuruMetni(tip: Int, etiket: String?): String = when (tip) {
        Phone.TYPE_MOBILE, Phone.TYPE_WORK_MOBILE -> context.getString(R.string.numara_mobil)
        Phone.TYPE_HOME -> context.getString(R.string.numara_ev)
        Phone.TYPE_WORK -> context.getString(R.string.numara_is)
        Phone.TYPE_CUSTOM -> etiket?.takeIf { it.isNotBlank() } ?: context.getString(R.string.numara_diger)
        else -> context.getString(R.string.numara_diger)
    }

    private companion object {
        const val ETIKET = "AramaGecmisiDeposu"
        const val SINIR = 500
    }
}
