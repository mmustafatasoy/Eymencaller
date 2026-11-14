package com.atasoy.eymentelefon.ui.navigasyon

// ✅ HAZIR — DEĞİŞTİRME. Yeni rota gerekiyorsa HATA_GUNLUGU › SORULAR.

import android.net.Uri

enum class AnaSekme { FAVORILER, SON, REHBER, TUSLAR }

object Rotalar {
    const val KARSILAMA = "karsilama"

    const val IZINLER = "izinler?ayarModu={ayarModu}"
    fun izinler(ayarModu: Boolean = false) = "izinler?ayarModu=$ayarModu"

    const val ANA = "ana?sekme={sekme}&numara={numara}"
    fun ana(sekme: AnaSekme = AnaSekme.SON, numara: String? = null) =
        "ana?sekme=${sekme.name}&numara=${Uri.encode(numara.orEmpty())}"

    /** Rehberde olmayan numara için kisiId = -1 ve numara dolu. */
    const val KISI_DETAY = "kisi/{kisiId}?numara={numara}"
    fun kisiDetay(kisiId: Long, numara: String? = null) =
        "kisi/$kisiId?numara=${Uri.encode(numara.orEmpty())}"

    const val ARAMA = "arama"
    const val AYARLAR = "ayarlar"
    const val HATA_KAYITLARI = "ayarlar/hata-kayitlari"
    const val ENGELLENENLER = "ayarlar/engellenenler"
    const val HIZLI_YANITLAR = "ayarlar/hizli-yanitlar"

    object Arguman {
        const val AYAR_MODU = "ayarModu"
        const val SEKME = "sekme"
        const val NUMARA = "numara"
        const val KISI_ID = "kisiId"
    }
}
