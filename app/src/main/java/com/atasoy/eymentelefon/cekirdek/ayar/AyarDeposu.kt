package com.atasoy.eymentelefon.cekirdek.ayar

// ✅ HAZIR — DEĞİŞTİRME. Tüm kullanıcı ayarları (DataStore Preferences).
// Yeni ayar gerekiyorsa HATA_GUNLUGU › SORULAR'a yaz.

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

enum class TemaSecimi { SISTEM, KOYU, ACIK }

data class Ayarlar(
    val ilkAcilisTamam: Boolean = false,
    val tema: TemaSecimi = TemaSecimi.SISTEM,
    val vurguIndex: Int = 0,
    val tusTitresimi: Boolean = true,
    val tusSesi: Boolean = false,
    /** null = varsayılan 4 mesaj (strings.xml hizli_yanit_1..4) */
    val hizliYanitlar: List<String>? = null,
    /** İzin akışında daha önce istenmiş Android izinleri (kalıcı ret tespiti için) */
    val istenenIzinler: Set<String> = emptySet(),
    /** AnaSekme.name */
    val sonSekme: String = "SON",
)

private val Context.ayarDataStore: DataStore<Preferences> by preferencesDataStore(name = "eymen_ayarlar")

class AyarDeposu(private val context: Context) {

    private object Anahtar {
        val ilkAcilis = booleanPreferencesKey("ilk_acilis_tamam")
        val tema = stringPreferencesKey("tema")
        val vurgu = intPreferencesKey("vurgu_index")
        val titresim = booleanPreferencesKey("tus_titresimi")
        val tusSesi = booleanPreferencesKey("tus_sesi")
        val hizliYanit = stringPreferencesKey("hizli_yanitlar")
        val istenen = stringSetPreferencesKey("istenen_izinler")
        val sonSekme = stringPreferencesKey("son_sekme")
    }

    private companion object {
        const val AYIRICI = "\u001F"
    }

    val ayarlar: Flow<Ayarlar> = context.ayarDataStore.data
        .catch { hata ->
            if (hata is IOException) {
                HataKaydedici.kaydet("AyarDeposu", hata)
                emit(emptyPreferences())
            } else {
                throw hata
            }
        }
        .map { p ->
            Ayarlar(
                ilkAcilisTamam = p[Anahtar.ilkAcilis] ?: false,
                tema = p[Anahtar.tema]?.let { ad -> TemaSecimi.entries.firstOrNull { it.name == ad } }
                    ?: TemaSecimi.SISTEM,
                vurguIndex = p[Anahtar.vurgu] ?: 0,
                tusTitresimi = p[Anahtar.titresim] ?: true,
                tusSesi = p[Anahtar.tusSesi] ?: false,
                hizliYanitlar = p[Anahtar.hizliYanit]?.split(AYIRICI)?.filter { it.isNotBlank() },
                istenenIzinler = p[Anahtar.istenen] ?: emptySet(),
                sonSekme = p[Anahtar.sonSekme] ?: "SON",
            )
        }

    fun varsayilanHizliYanitlar(): List<String> = listOf(
        context.getString(R.string.hizli_yanit_1),
        context.getString(R.string.hizli_yanit_2),
        context.getString(R.string.hizli_yanit_3),
        context.getString(R.string.hizli_yanit_4),
    )

    suspend fun ilkAcilisiTamamla() = guncelle { it[Anahtar.ilkAcilis] = true }
    suspend fun temaSec(tema: TemaSecimi) = guncelle { it[Anahtar.tema] = tema.name }
    suspend fun vurguSec(index: Int) = guncelle { it[Anahtar.vurgu] = index }
    suspend fun tusTitresimi(acik: Boolean) = guncelle { it[Anahtar.titresim] = acik }
    suspend fun tusSesi(acik: Boolean) = guncelle { it[Anahtar.tusSesi] = acik }
    suspend fun hizliYanitlariKaydet(liste: List<String>) =
        guncelle { it[Anahtar.hizliYanit] = liste.joinToString(AYIRICI) }
    suspend fun sonSekme(ad: String) = guncelle { it[Anahtar.sonSekme] = ad }
    suspend fun izinlerIstendi(izinler: Collection<String>) = guncelle {
        it[Anahtar.istenen] = (it[Anahtar.istenen] ?: emptySet()) + izinler
    }

    private suspend fun guncelle(blok: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        try {
            context.ayarDataStore.edit { blok(it) }
        } catch (e: IOException) {
            HataKaydedici.kaydet("AyarDeposu.guncelle", e)
        }
    }
}
