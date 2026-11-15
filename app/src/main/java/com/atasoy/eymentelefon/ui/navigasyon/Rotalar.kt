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

    /** V12-10: uygulama içi kişi ekle (kisiId = -1) / düzenle */
    const val KISI_DUZENLE = "kisi-duzenle/{kisiId}?numara={numara}"
    fun kisiDuzenle(kisiId: Long, numara: String? = null) =
        "kisi-duzenle/$kisiId?numara=${Uri.encode(numara.orEmpty())}"

    /** V12-08: kişi posteri düzenleyici */
    const val POSTER = "poster/{lookupKey}?ad={ad}"
    fun poster(lookupKey: String, ad: String) = "poster/${Uri.encode(lookupKey)}?ad=${Uri.encode(ad)}"

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
        const val LOOKUP = "lookupKey"
        const val AD = "ad"
    }
}
