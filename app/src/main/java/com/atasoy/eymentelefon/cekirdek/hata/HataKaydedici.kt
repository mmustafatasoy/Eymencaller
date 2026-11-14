package com.atasoy.eymentelefon.cekirdek.hata

// ✅ HAZIR — DEĞİŞTİRME.
// Uygulama içi hata kaydı. Boş catch YASAK: her yakalanan hata buraya gelir.
// Kayıtlar: files/hata_kayitlari/kayit_YYYY-AA-GG.txt — son 7 gün tutulur.
// Ayarlar › Hata kayıtları ekranı bu dosyaları okur.

import android.app.Application
import android.os.Build
import android.util.Log
import com.atasoy.eymentelefon.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object HataKaydedici {

    private const val ETIKET = "EymenHata"
    private const val KLASOR = "hata_kayitlari"
    private const val SAKLAMA_GUN = 7L
    private const val KAYIT_BASI = "=== "

    private val tr = Locale.forLanguageTag("tr-TR")
    private val zamanBicimi = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", tr)
    private val gunBicimi = SimpleDateFormat("yyyy-MM-dd", tr)

    @Volatile private var klasor: File? = null

    /** EymenTelefonUygulamasi.onCreate içinde bir kez çağrılır. */
    fun kur(uygulama: Application) {
        val k = File(uygulama.filesDir, KLASOR).apply { mkdirs() }
        klasor = k
        eskileriTemizle(k)
        val onceki = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, hata ->
            yaz(tur = "ÇÖKME", etiket = "Thread:${thread.name}", mesaj = null, hata = hata)
            onceki?.uncaughtException(thread, hata)
        }
    }

    /** Yakalanan hata. Örnek: catch (e: SecurityException) { HataKaydedici.kaydet("RehberDeposu", e) } */
    fun kaydet(etiket: String, hata: Throwable? = null, mesaj: String? = null) {
        Log.e(ETIKET, "[$etiket] ${mesaj.orEmpty()}", hata)
        yaz(tur = "HATA", etiket = etiket, mesaj = mesaj, hata = hata)
    }

    /** Çökme olmayan ama dikkat edilmesi gereken durum. */
    fun uyari(etiket: String, mesaj: String) {
        Log.w(ETIKET, "[$etiket] $mesaj")
        yaz(tur = "UYARI", etiket = etiket, mesaj = mesaj, hata = null)
    }

    /** Ayarlar › Hata kayıtları için: en yeni dosya en üstte. */
    fun dosyalar(): List<File> =
        klasor?.listFiles { f -> f.isFile && f.name.endsWith(".txt") }
            ?.sortedByDescending { it.name }
            .orEmpty()

    /** Son 7 gündeki HATA + ÇÖKME sayısı (Ayarlar özet satırı). */
    fun kayitSayisi(): Int = dosyalar().sumOf { dosya ->
        runCatching {
            dosya.readLines().count { it.startsWith(KAYIT_BASI) && !it.contains("UYARI") }
        }.getOrDefault(0)
    }

    /** Paylaşmak için tüm kayıtlar tek metin. */
    fun tumMetin(): String = buildString {
        append("Eymen Telefon ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\n")
        append("Cihaz: ${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n\n")
        dosyalar().forEach { append(runCatching { it.readText() }.getOrDefault("")).append('\n') }
    }

    fun temizle() {
        dosyalar().forEach { it.delete() }
    }

    @Synchronized
    private fun yaz(tur: String, etiket: String, mesaj: String?, hata: Throwable?) {
        val k = klasor ?: return
        runCatching {
            val simdi = Date()
            val dosya = File(k, "kayit_${gunBicimi.format(simdi)}.txt")
            val metin = buildString {
                append(KAYIT_BASI).append(tur).append(" · ").append(zamanBicimi.format(simdi))
                append(" · ").append(etiket).append('\n')
                if (!mesaj.isNullOrBlank()) append(mesaj).append('\n')
                if (hata != null) append(Log.getStackTraceString(hata).take(6000)).append('\n')
                append('\n')
            }
            dosya.appendText(metin)
        }.onFailure { Log.e(ETIKET, "Hata kaydı yazılamadı", it) }
    }

    private fun eskileriTemizle(k: File) {
        val sinir = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(SAKLAMA_GUN)
        k.listFiles()?.filter { it.lastModified() < sinir }?.forEach { it.delete() }
    }
}
