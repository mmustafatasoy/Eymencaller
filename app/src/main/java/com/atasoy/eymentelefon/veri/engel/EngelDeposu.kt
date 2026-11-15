package com.atasoy.eymentelefon.veri.engel

// ✅ HAZIR (Claude tamamladı). Kaynak: BlockedNumberContract (sadece varsayılan telefon uygulaması erişebilir).

import android.content.ContentValues
import android.content.Context
import android.provider.BlockedNumberContract
import android.provider.BlockedNumberContract.BlockedNumbers
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.veri.icerikDegisimi
import com.atasoy.eymentelefon.veri.rehber.str
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EngelDeposu(private val context: Context) {

    private fun izinli(): Boolean = try {
        BlockedNumberContract.canCurrentUserBlockNumbers(context)
    } catch (e: SecurityException) {
        HataKaydedici.kaydet("EngelDeposu.izinli", e)
        false
    }

    fun engelliler(): Flow<List<String>> =
        context.icerikDegisimi(BlockedNumbers.CONTENT_URI, "EngelDeposu")
            .map { oku() }
            .flowOn(Dispatchers.IO)

    suspend fun engelliMi(numara: String): Boolean = withContext(Dispatchers.IO) {
        if (numara.isBlank() || !izinli()) return@withContext false
        try {
            BlockedNumberContract.isBlocked(context, numara)
        } catch (e: RuntimeException) {
            HataKaydedici.kaydet("EngelDeposu.engelliMi", e)
            false
        }
    }

    suspend fun engelle(numara: String) {
        withContext(Dispatchers.IO) {
            if (numara.isBlank() || !izinli()) return@withContext
            try {
                val degerler = ContentValues().apply { put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, numara) }
                context.contentResolver.insert(BlockedNumbers.CONTENT_URI, degerler)
            } catch (e: RuntimeException) {
                HataKaydedici.kaydet("EngelDeposu.engelle", e)
            }
        }
    }

    suspend fun engeliKaldir(numara: String) {
        withContext(Dispatchers.IO) {
            if (!izinli()) return@withContext
            try {
                BlockedNumberContract.unblock(context, numara)
            } catch (e: RuntimeException) {
                HataKaydedici.kaydet("EngelDeposu.engeliKaldir", e)
            }
        }
    }

    private fun oku(): List<String> {
        if (!izinli()) return emptyList()
        return try {
            val liste = mutableListOf<String>()
            context.contentResolver.query(
                BlockedNumbers.CONTENT_URI,
                arrayOf(BlockedNumbers.COLUMN_ORIGINAL_NUMBER),
                null, null, null,
            )?.use { c ->
                while (c.moveToNext()) c.str(BlockedNumbers.COLUMN_ORIGINAL_NUMBER)?.let { liste += it }
            }
            liste
        } catch (e: RuntimeException) {
            HataKaydedici.kaydet("EngelDeposu.oku", e)
            emptyList()
        }
    }
}
