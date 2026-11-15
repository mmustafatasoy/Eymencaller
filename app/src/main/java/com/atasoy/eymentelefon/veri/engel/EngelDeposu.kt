package com.atasoy.eymentelefon.veri.engel

import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.BlockedNumberContract
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.konteyner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** Android sistem engelleme listesi; ayrı uygulama veri tabanı kullanılmaz. */
class EngelDeposu(private val context: Context) {

    fun engelliler(): Flow<List<String>> = degisiklikler()
        .map { oku() }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    suspend fun engelliMi(numara: String): Boolean = withContext(Dispatchers.IO) {
        if (numara.isBlank() || !erisilebilirMi()) return@withContext false
        try {
            BlockedNumberContract.isBlocked(context, numara)
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("EngelDeposu.engelliMi", hata)
            false
        }
    }

    suspend fun engelle(numara: String) {
        withContext(Dispatchers.IO) {
            val temiz = numara.trim()
            if (temiz.isBlank() || !erisilebilirMi()) return@withContext
            try {
                if (!BlockedNumberContract.isBlocked(context, temiz)) {
                    context.contentResolver.insert(
                        BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                        ContentValues().apply {
                            put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, temiz)
                        },
                    )
                }
            } catch (hata: SecurityException) {
                HataKaydedici.kaydet("EngelDeposu.engelle", hata)
            }
        }
    }

    suspend fun engeliKaldir(numara: String) {
        withContext(Dispatchers.IO) {
            if (numara.isBlank() || !erisilebilirMi()) return@withContext
            try {
                BlockedNumberContract.unblock(context, numara)
            } catch (hata: SecurityException) {
                HataKaydedici.kaydet("EngelDeposu.engeliKaldir", hata)
            }
        }
    }

    private fun erisilebilirMi(): Boolean = try {
        // Sistem API yetkilendirmesi yanında tasarımın gerektirdiği dialer rolü aranır.
        context.konteyner().izinYoneticisi.varsayilanUygulamaMi() &&
            BlockedNumberContract.canCurrentUserBlockNumbers(context)
    } catch (hata: SecurityException) {
        HataKaydedici.kaydet("EngelDeposu.erisim", hata)
        false
    }

    private fun degisiklikler(): Flow<Unit> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) { trySend(Unit) }
        }
        val kayitli = try {
            if (erisilebilirMi()) {
                context.contentResolver.registerContentObserver(
                    BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                    true, observer,
                )
                true
            } else {
                false
            }
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("EngelDeposu.observer", hata)
            false
        }
        trySend(Unit)
        awaitClose {
            if (kayitli) {
                try {
                    context.contentResolver.unregisterContentObserver(observer)
                } catch (hata: IllegalStateException) {
                    HataKaydedici.kaydet("EngelDeposu.observerKapat", hata)
                }
            }
        }
    }

    private fun oku(): List<String> {
        if (!erisilebilirMi()) return emptyList()
        return try {
            val bulunanlar = linkedSetOf<String>()
            context.contentResolver.query(
                BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                arrayOf(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER),
                null, null, null,
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    cursor.getString(0)?.takeIf(String::isNotBlank)?.let(bulunanlar::add)
                }
            }
            bulunanlar.toList().sorted()
        } catch (hata: SecurityException) {
            HataKaydedici.kaydet("EngelDeposu.oku", hata)
            emptyList()
        }
    }
}
