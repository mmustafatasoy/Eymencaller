package com.atasoy.eymentelefon.cekirdek.izin

// ✅ HAZIR — DEĞİŞTİRME.
// Tüm izin ve rol kontrolleri BURADAN yapılır. Ekranlarda doğrudan checkSelfPermission YASAK.

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

enum class IzinDurumu { VERILDI, ISTENMEDI, REDDEDILDI, KALICI_REDDEDILDI }

class IzinYoneticisi(private val context: Context) {

    private val rolYoneticisi: RoleManager? = context.getSystemService(RoleManager::class.java)

    // ---------- Rol ----------
    fun varsayilanUygulamaMi(): Boolean =
        rolYoneticisi?.let { it.isRoleAvailable(RoleManager.ROLE_DIALER) && it.isRoleHeld(RoleManager.ROLE_DIALER) }
            ?: false

    /** ActivityResultContracts.StartActivityForResult ile başlatılır. */
    fun rolIstemeIntenti(): Intent? =
        rolYoneticisi?.takeIf { it.isRoleAvailable(RoleManager.ROLE_DIALER) }
            ?.createRequestRoleIntent(RoleManager.ROLE_DIALER)

    // ---------- Android izinleri ----------
    fun izinVarMi(androidIzni: String): Boolean =
        ContextCompat.checkSelfPermission(context, androidIzni) == PackageManager.PERMISSION_GRANTED

    fun verildiMi(tanim: IzinTanimi): Boolean = when (tanim.tur) {
        IzinTuru.VARSAYILAN_UYGULAMA -> varsayilanUygulamaMi()
        else -> tanim.androidIzinleri.all(::izinVarMi)
    }

    fun eksikAndroidIzinleri(tanim: IzinTanimi): Array<String> =
        tanim.androidIzinleri.filterNot(::izinVarMi).toTypedArray()

    /**
     * Kartın durumu (docs/04_IZINLER.md §3).
     * @param dahaOnceIstenenler AyarDeposu.ayarlar.istenenIzinler
     */
    fun durum(tanim: IzinTanimi, activity: Activity, dahaOnceIstenenler: Set<String>): IzinDurumu {
        if (verildiMi(tanim)) return IzinDurumu.VERILDI
        if (tanim.tur == IzinTuru.VARSAYILAN_UYGULAMA) return IzinDurumu.ISTENMEDI
        val eksikler = eksikAndroidIzinleri(tanim)
        val istenmisti = eksikler.any { it in dahaOnceIstenenler }
        if (!istenmisti) return IzinDurumu.ISTENMEDI
        val gerekceGosterilebilir = eksikler.any { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
        return if (gerekceGosterilebilir) IzinDurumu.REDDEDILDI else IzinDurumu.KALICI_REDDEDILDI
    }

    // ---------- Toplu kontroller ----------
    fun zorunlularTamam(): Boolean =
        IzinListesi.gecerli().filter { it.zorunlu }.all(::verildiMi)

    fun verilenSayisi(): Int = IzinListesi.gecerli().count(::verildiMi)

    fun toplamSayi(): Int = IzinListesi.gecerli().size

    /** Arama yapmak için gerekenler: rol + Telefon. (12 numaralı alt sayfa) */
    fun aramaIcinEksikler(): List<IzinTanimi> =
        IzinListesi.gecerli()
            .filter { it.tur == IzinTuru.VARSAYILAN_UYGULAMA || it.tur == IzinTuru.TELEFON }
            .filterNot(::verildiMi)

    /** Kalıcı reddedilen izin için uygulamanın sistem ayar sayfası. */
    fun uygulamaAyarlariIntenti(): Intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
