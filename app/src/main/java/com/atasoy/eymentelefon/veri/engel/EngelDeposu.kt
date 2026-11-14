package com.atasoy.eymentelefon.veri.engel

// ⬜ CODEX YAZACAK — GOREV-05 (kullanımı GOREV-12)
// İMZALAR SABİT. Kaynak: BlockedNumberContract (sadece varsayılan telefon uygulaması erişebilir).
// BlockedNumberContract.canCurrentUserBlockNumbers(context) false ise boş liste.

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class EngelDeposu(private val context: Context) {

    fun engelliler(): Flow<List<String>> = flowOf(emptyList()) // GOREV-05

    suspend fun engelliMi(numara: String): Boolean = false // GOREV-05

    suspend fun engelle(numara: String) { // GOREV-05
    }

    suspend fun engeliKaldir(numara: String) { // GOREV-05
    }
}
