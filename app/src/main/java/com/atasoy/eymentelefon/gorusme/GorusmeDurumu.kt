package com.atasoy.eymentelefon.gorusme

// ✅ HAZIR — DEĞİŞTİRME. Arama ekranları SADECE bu modelleri görür, android.telecom.Call'a dokunmaz.

enum class GorusmeAsamasi {
    /** Giden: numara çevriliyor (08) */
    ARANIYOR,
    /** Giden: hat seçimi / bağlanıyor (08) */
    BAGLANIYOR,
    /** Gelen: çalıyor (09) */
    CALIYOR,
    /** Konuşuluyor (10) */
    AKTIF,
    /** Beklemede (10, "Beklemede" yazısı) */
    BEKLEMEDE,
    /** Kapanıyor / kapandı → ekran kısa süre "Arama bitti" gösterip kapanır */
    BITTI,
}

data class GorusmeDurumu(
    /** GorusmeYoneticisi'nin verdiği kimlik; kontrol fonksiyonlarına bu verilir. */
    val id: String,
    val numara: String,
    /** Sistemin bildirdiği ad (varsa). Yoksa GorusmeViewModel rehberden bulur. */
    val sistemAdi: String?,
    val asama: GorusmeAsamasi,
    /** Bağlanma anı (System.currentTimeMillis tabanlı). Sayaç bundan hesaplanır. */
    val baglanmaZamani: Long?,
    val hd: Boolean,
    val beklemeDesteklenir: Boolean,
    val konferans: Boolean,
    /** 1 tabanlı SIM sırası; tek SIM'de null (ekranda "SIM n" gösterme) */
    val simSirasi: Int?,
)

enum class SesYolu { TELEFON, HOPARLOR, BLUETOOTH, KULAKLIK }

data class SesDurumu(
    val sessiz: Boolean = false,
    val yol: SesYolu = SesYolu.TELEFON,
    val bluetoothVar: Boolean = false,
    val kulaklikVar: Boolean = false,
)
