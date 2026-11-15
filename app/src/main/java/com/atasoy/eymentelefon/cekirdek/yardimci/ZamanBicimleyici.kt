package com.atasoy.eymentelefon.cekirdek.yardimci

// ✅ HAZIR. Son aramalar ve kişi detayında saat/tarih ve süre metinleri.

import android.content.Context
import com.atasoy.eymentelefon.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

enum class GunGrubu { BUGUN, DUN, BU_HAFTA, ESKI }

object ZamanBicimleyici {

    private val saat = SimpleDateFormat("HH:mm", TurkceMetin.TR)
    private val gunAdi = SimpleDateFormat("EEEE", TurkceMetin.TR)
    private val kisaTarih = SimpleDateFormat("dd.MM.yy", TurkceMetin.TR)

    private fun gunBasi(simdi: Long, geri: Int): Long = Calendar.getInstance().apply {
        timeInMillis = simdi
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, -geri)
    }.timeInMillis

    fun grup(zaman: Long, simdi: Long = System.currentTimeMillis()): GunGrubu = when {
        zaman >= gunBasi(simdi, 0) -> GunGrubu.BUGUN
        zaman >= gunBasi(simdi, 1) -> GunGrubu.DUN
        zaman >= gunBasi(simdi, 6) -> GunGrubu.BU_HAFTA
        else -> GunGrubu.ESKI
    }

    /** iOS listesi: bugün "20:31" · dün "Dün" · bu hafta "Cumartesi" · daha eski "12.11.26" */
    @Synchronized
    fun kisa(context: Context, zaman: Long): String = when (grup(zaman)) {
        GunGrubu.BUGUN -> saat.format(Date(zaman))
        GunGrubu.DUN -> context.getString(R.string.dun)
        GunGrubu.BU_HAFTA -> gunAdi.format(Date(zaman)).replaceFirstChar { it.titlecase(TurkceMetin.TR) }
        GunGrubu.ESKI -> kisaTarih.format(Date(zaman))
    }

    /** Kişi kartı: "Bugün 20:31", "Dün 09:02", "Cumartesi 11:20", "12.11.26 11:20" */
    @Synchronized
    fun detay(context: Context, zaman: Long): String = when (grup(zaman)) {
        GunGrubu.BUGUN -> context.getString(R.string.bugun) + " " + saat.format(Date(zaman))
        else -> kisa(context, zaman) + " " + saat.format(Date(zaman))
    }

    /** "4 dk 12 sn" / "45 sn" */
    fun sure(context: Context, saniye: Long): String =
        if (saniye >= 60) context.getString(R.string.sure_dk_sn, (saniye / 60).toInt(), (saniye % 60).toInt())
        else context.getString(R.string.sure_sn, saniye.toInt())

    /** Görüşme sayacı: "03:42" / "1:03:42" */
    fun sayac(saniye: Long): String {
        val s = saniye.coerceAtLeast(0)
        val sa = s / 3600
        val dk = (s % 3600) / 60
        val sn = s % 60
        return if (sa > 0) "%d:%02d:%02d".format(sa, dk, sn) else "%02d:%02d".format(dk, sn)
    }
}
