package com.atasoy.eymentelefon.veri.gecmis

// ⬜ CODEX YAZACAK — GOREV-05
// İMZALAR SABİT. Sadece gövdeleri doldur.
// Kurallar: Kaynak CallLog.Calls · ContentObserver(CallLog.Calls.CONTENT_URI) · Dispatchers.IO
//  - TYPE eşlemesi: INCOMING→GELEN, OUTGOING→GIDEN, MISSED→CEVAPSIZ, REJECTED→REDDEDILEN,
//    BLOCKED→ENGELLENEN, VOICEMAIL→SESLI_MESAJ
//  - En yeni en üstte, en fazla 500 kayıt
//  - İzin yoksa boş liste; SecurityException → HataKaydedici

import android.content.Context
import com.atasoy.eymentelefon.veri.model.AramaGrubu
import com.atasoy.eymentelefon.veri.model.AramaKaydi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AramaGecmisiDeposu(private val context: Context) {

    fun kayitlar(): Flow<List<AramaKaydi>> = flowOf(emptyList()) // GOREV-05

    /** Art arda aynı numara + aynı tür kayıtları birleştirir → (n). Saf fonksiyon, test edilebilir. */
    fun grupla(kayitlar: List<AramaKaydi>): List<AramaGrubu> = emptyList() // GOREV-05

    /** Kişi detayı "Son aramalar" kartı için (NumaraBicimleyici.normalize ile karşılaştır). */
    fun numaraKayitlari(numara: String): Flow<List<AramaKaydi>> = flowOf(emptyList()) // GOREV-05

    suspend fun sonArananNumara(): String? = null // GOREV-05

    suspend fun sil(kayitIdleri: List<Long>) { // GOREV-05
    }

    /** Son aramalar ekranına girilince: NEW=0, IS_READ=1 (cevapsızlar) */
    suspend fun cevapsizlariGorulduYap() { // GOREV-05
    }
}
