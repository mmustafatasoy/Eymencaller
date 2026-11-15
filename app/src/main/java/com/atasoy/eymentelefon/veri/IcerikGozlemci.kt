package com.atasoy.eymentelefon.veri

// ✅ HAZIR. Sistem sağlayıcısı (rehber, arama geçmişi) değişince yeniden yayın yapan ortak akış.

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

internal fun Context.icerikDegisimi(uri: Uri, etiket: String): Flow<Unit> = callbackFlow {
    val gozlemci = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            trySend(Unit)
        }
    }
    trySend(Unit)
    val kayitli = try {
        contentResolver.registerContentObserver(uri, true, gozlemci)
        true
    } catch (e: SecurityException) {
        HataKaydedici.kaydet("$etiket.gozlemci", e)
        false
    }
    awaitClose { if (kayitli) contentResolver.unregisterContentObserver(gozlemci) }
}.conflate()
