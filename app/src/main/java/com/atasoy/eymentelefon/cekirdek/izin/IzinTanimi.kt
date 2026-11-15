package com.atasoy.eymentelefon.cekirdek.izin

// ✅ HAZIR — DEĞİŞTİRME. Kaynak: docs/04_IZINLER.md §1 (sıra ve zorunluluk sabit)

import android.Manifest
import android.os.Build
import androidx.annotation.StringRes
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.ui.ikon.EymenIkon

enum class IzinTuru { VARSAYILAN_UYGULAMA, TELEFON, REHBER, ARAMA_GECMISI, BILDIRIMLER }

data class IzinTanimi(
    val tur: IzinTuru,
    @StringRes val baslik: Int,
    @StringRes val aciklama: Int,
    @StringRes val neden: Int,
    val ikon: EymenIkon,
    val zorunlu: Boolean,
    /** Boş liste = Android izni değil, rol (VARSAYILAN_UYGULAMA) */
    val androidIzinleri: List<String>,
    val minSdk: Int = Build.VERSION_CODES.Q,
)

object IzinListesi {

    val tumu: List<IzinTanimi> = listOf(
        IzinTanimi(
            tur = IzinTuru.VARSAYILAN_UYGULAMA,
            baslik = R.string.izin_rol_baslik,
            aciklama = R.string.izin_rol_aciklama,
            neden = R.string.izin_rol_neden,
            ikon = EymenIkon.Cihaz,
            zorunlu = true,
            androidIzinleri = emptyList(),
        ),
        IzinTanimi(
            tur = IzinTuru.TELEFON,
            baslik = R.string.izin_telefon_baslik,
            aciklama = R.string.izin_telefon_aciklama,
            neden = R.string.izin_telefon_neden,
            ikon = EymenIkon.Telefon,
            zorunlu = true,
            androidIzinleri = listOf(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.READ_PHONE_NUMBERS,
            ),
        ),
        IzinTanimi(
            tur = IzinTuru.REHBER,
            baslik = R.string.izin_rehber_baslik,
            aciklama = R.string.izin_rehber_aciklama,
            neden = R.string.izin_rehber_neden,
            ikon = EymenIkon.Kart,
            zorunlu = true,
            androidIzinleri = listOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
            ),
        ),
        IzinTanimi(
            tur = IzinTuru.ARAMA_GECMISI,
            baslik = R.string.izin_gecmis_baslik,
            aciklama = R.string.izin_gecmis_aciklama,
            neden = R.string.izin_gecmis_neden,
            ikon = EymenIkon.SaatDolu,
            zorunlu = true,
            androidIzinleri = listOf(
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG,
            ),
        ),
        IzinTanimi(
            tur = IzinTuru.BILDIRIMLER,
            baslik = R.string.izin_bildirim_baslik,
            aciklama = R.string.izin_bildirim_aciklama,
            neden = R.string.izin_bildirim_neden,
            ikon = EymenIkon.Bildirim,
            zorunlu = false,
            androidIzinleri = bildirimIzni(),
            minSdk = Build.VERSION_CODES.TIRAMISU,
        ),
    )

    /** Bu cihazda gösterilecek izinler (Android 13 altında Bildirimler kartı yok). */
    fun gecerli(): List<IzinTanimi> = tumu.filter { Build.VERSION.SDK_INT >= it.minSdk }

    fun bul(tur: IzinTuru): IzinTanimi = tumu.first { it.tur == tur }

    private fun bildirimIzni(): List<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }
}
