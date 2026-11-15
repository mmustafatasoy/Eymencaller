package com.atasoy.eymentelefon.veri.poster

// V12-05 · Kişi Posteri deposu (docs/07_V12_PLANI.md). Uygulamanın özel klasöründe tutulur, izin gerekmez.
// filesDir/posterler/<anahtar>.jpg  (en uzun kenar 1600px, JPEG %90)
// filesDir/posterler/<anahtar>.json {"canli":true,"renk":null}

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

data class Poster(
    /** Seçilen fotoğraf; null → renk gradyanı */
    val dosya: File?,
    /** AramaEkraniRenk.palet içindeki sıra; null → kişinin avatar rengi */
    val renkIndex: Int?,
    /** Ken Burns yakınlaşma + canlı ad efekti */
    val canli: Boolean,
    /** Önbellek kırıcı (Coil/Image yeniden okusun) */
    val surum: Long,
)

class PosterDeposu(context: Context) {
    private val klasor = File(context.filesDir, "posterler").apply { mkdirs() }
    private val cozumleyici = context.applicationContext.contentResolver

    /** Değişince ekranlar yeniden okusun diye artar */
    private val _degisim = MutableStateFlow(0L)
    val degisim: StateFlow<Long> = _degisim.asStateFlow()

    private fun ad(anahtar: String) = anahtar.replace(Regex("[^A-Za-z0-9._-]"), "_").take(MAKS_AD)
    private fun foto(anahtar: String) = File(klasor, ad(anahtar) + ".jpg")
    private fun ayar(anahtar: String) = File(klasor, ad(anahtar) + ".json")

    suspend fun oku(anahtar: String?): Poster? = withContext(Dispatchers.IO) {
        if (anahtar.isNullOrBlank()) return@withContext null
        val f = foto(anahtar)
        val j = ayar(anahtar)
        if (!f.exists() && !j.exists()) return@withContext null
        val json = runCatching { JSONObject(j.readText()) }.getOrNull()
        Poster(
            dosya = f.takeIf { it.exists() },
            renkIndex = json?.takeIf { it.has("renk") && !it.isNull("renk") }?.getInt("renk"),
            canli = json?.optBoolean("canli", true) ?: true,
            surum = maxOf(f.lastModified(), j.lastModified()),
        )
    }

    /** Fotoğrafı küçültüp kaydeder. Başarısızsa false. */
    suspend fun fotoKaydet(anahtar: String, kaynak: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val sinir = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            cozumleyici.openInputStream(kaynak)?.use { BitmapFactory.decodeStream(it, null, sinir) }
            var ornek = 1
            while (maxOf(sinir.outWidth, sinir.outHeight) / (ornek * 2) >= MAKS_KENAR) ornek *= 2
            val bmp = cozumleyici.openInputStream(kaynak)?.use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = ornek })
            } ?: return@runCatching false
            val olcek = MAKS_KENAR.toFloat() / maxOf(bmp.width, bmp.height)
            val son = if (olcek < 1f) {
                Bitmap.createScaledBitmap(bmp, (bmp.width * olcek).toInt(), (bmp.height * olcek).toInt(), true)
            } else {
                bmp
            }
            foto(anahtar).outputStream().use { son.compress(Bitmap.CompressFormat.JPEG, KALITE, it) }
            if (!ayar(anahtar).exists()) ayarYaz(anahtar, canli = true, renk = null)
            _degisim.value++
            true
        }.getOrElse { HataKaydedici.kaydet("PosterDeposu.fotoKaydet", it); false }
    }

    suspend fun ayarKaydet(anahtar: String, canli: Boolean, renk: Int?) = withContext(Dispatchers.IO) {
        ayarYaz(anahtar, canli, renk)
        _degisim.value++
    }

    /** Fotoğrafı kaldırır (renk/canlı ayarı kalır) */
    suspend fun fotoSil(anahtar: String) = withContext(Dispatchers.IO) {
        foto(anahtar).delete()
        _degisim.value++
    }

    suspend fun sil(anahtar: String) = withContext(Dispatchers.IO) {
        foto(anahtar).delete()
        ayar(anahtar).delete()
        _degisim.value++
    }

    private fun ayarYaz(anahtar: String, canli: Boolean, renk: Int?) {
        val j = JSONObject().put("canli", canli).put("renk", renk ?: JSONObject.NULL)
        ayar(anahtar).writeText(j.toString())
    }

    private companion object {
        const val MAKS_KENAR = 1600
        const val KALITE = 90
        const val MAKS_AD = 120
    }
}
