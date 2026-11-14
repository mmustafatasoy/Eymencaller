package com.atasoy.eymentelefon.veri.rehber

// ⬜ CODEX YAZACAK — GOREV-05
// İMZALAR SABİT: fonksiyon adı, parametre ve dönüş tipi DEĞİŞTİRİLEMEZ. Sadece gövdeleri doldur.
// Kurallar (docs/03_MIMARI.md §5):
//  - Kaynak: ContactsContract (Contacts + Data + Phone + Email + Organization + Note)
//  - Flow'lar ContentObserver(ContactsContract.Contacts.CONTENT_URI) ile değişince yeniden yayınlar (callbackFlow)
//  - Sorgular Dispatchers.IO (flowOn)
//  - İzin yoksa boş liste / null döner; SecurityException → HataKaydedici.kaydet("RehberDeposu", e)
//  - Sıralama TurkceMetin.karsilastirici ile (ad'a göre)

import android.content.Context
import com.atasoy.eymentelefon.veri.model.Kisi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RehberDeposu(private val context: Context) {

    /** Numarası olan tüm kişiler, Türkçe sıralı. */
    fun kisiler(): Flow<List<Kisi>> = flowOf(emptyList()) // GOREV-05

    /** STARRED = 1 olanlar. */
    fun favoriler(): Flow<List<Kisi>> = flowOf(emptyList()) // GOREV-05

    /** Tüm alanlarıyla tek kişi (Kişi detayı ekranı). */
    suspend fun kisi(id: Long): Kisi? = null // GOREV-05

    /** Arayanın adını bulmak için. PhoneLookup.CONTENT_FILTER_URI kullan. */
    suspend fun numaradanKisi(numara: String): Kisi? = null // GOREV-05

    /** ContactsContract.Profile — yoksa null. */
    suspend fun benimKartim(): Kisi? = null // GOREV-05

    suspend fun favoriYap(id: Long, favori: Boolean) { // GOREV-05
    }

    suspend fun sil(id: Long) { // GOREV-05
    }
}
