package com.atasoy.eymentelefon.veri.model

// ✅ HAZIR — DEĞİŞTİRME.

enum class AramaTuru { GELEN, GIDEN, CEVAPSIZ, REDDEDILEN, ENGELLENEN, SESLI_MESAJ }

data class AramaKaydi(
    /** CallLog.Calls._ID */
    val id: Long,
    val numara: String,
    /** Rehberdeki ad (CallLog.Calls.CACHED_NAME) — rehberde yoksa null */
    val ad: String?,
    val kisiId: Long?,
    val tur: AramaTuru,
    /** epoch ms */
    val tarih: Long,
    val sureSn: Long,
    /** "Mobil", "İş"… ya da konum ("İstanbul") */
    val altBilgi: String?,
    /** Görülmemiş cevapsız arama */
    val yeni: Boolean,
    /** Hangi SIM (PhoneAccountHandle id) */
    val hesapId: String?,
)

/** Son aramalar listesinde art arda aynı numaradan gelen kayıtlar tek satır olur: (n) */
data class AramaGrubu(
    val ilk: AramaKaydi,
    val kayitIdleri: List<Long>,
) {
    val sayi: Int get() = kayitIdleri.size
}
