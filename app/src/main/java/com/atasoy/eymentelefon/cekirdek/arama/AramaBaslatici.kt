package com.atasoy.eymentelefon.cekirdek.arama

// ✅ HAZIR — DEĞİŞTİRME.
// Uygulamadaki TEK arama başlatma noktası. Hiçbir ekran placeCall / ACTION_CALL kullanmaz.

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.izin.IzinTanimi
import com.atasoy.eymentelefon.cekirdek.izin.IzinYoneticisi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** 12 numaralı alt sayfanın açılma isteği. null = kapalı. */
data class IzinEksikIstegi(val numara: String, val eksikler: List<IzinTanimi>)

class AramaBaslatici(
    private val context: Context,
    private val izinler: IzinYoneticisi,
) {
    private val _izinEksik = MutableStateFlow<IzinEksikIstegi?>(null)

    /** EymenNavHost bunu dinler; null değilse IzinEksikAltSayfa açılır. */
    val izinEksik: StateFlow<IzinEksikIstegi?> = _izinEksik.asStateFlow()

    /** Numarada aranabilir karakterler dışındakileri atar. */
    fun temizle(numara: String): String = numara.filter { it.isDigit() || it in "+*#,;" }

    /**
     * @return arama sisteme verildiyse true. İzin eksikse alt sayfayı açar ve false döner.
     */
    @SuppressLint("MissingPermission") // İzin kontrolü IzinYoneticisi.aramaIcinEksikler() ile yapılıyor
    fun ara(numara: String, hesap: PhoneAccountHandle? = null, goruntulu: Boolean = false): Boolean {
        val temiz = temizle(numara)
        if (temiz.isEmpty()) return false

        val eksikler = izinler.aramaIcinEksikler()
        if (eksikler.isNotEmpty()) {
            _izinEksik.value = IzinEksikIstegi(temiz, eksikler)
            return false
        }
        return try {
            val telecom = context.getSystemService(TelecomManager::class.java)
            val ekler = Bundle()
            if (hesap != null) ekler.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, hesap)
            if (goruntulu) {
                ekler.putInt(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, VideoProfile.STATE_BIDIRECTIONAL)
            }
            telecom.placeCall(Uri.fromParts("tel", temiz, null), ekler)
            true
        } catch (e: SecurityException) {
            HataKaydedici.kaydet("AramaBaslatici.ara", e)
            _izinEksik.value = IzinEksikIstegi(temiz, izinler.aramaIcinEksikler())
            false
        }
    }

    /** Tuş takımında "1" uzun basış: operatörün sesli mesaj kutusu. */
    @SuppressLint("MissingPermission")
    fun sesliMesaj(): Boolean {
        val eksikler = izinler.aramaIcinEksikler()
        if (eksikler.isNotEmpty()) {
            _izinEksik.value = IzinEksikIstegi("", eksikler)
            return false
        }
        return try {
            context.getSystemService(TelecomManager::class.java)
                .placeCall(Uri.fromParts(PhoneAccount.SCHEME_VOICEMAIL, "", null), Bundle())
            true
        } catch (e: RuntimeException) {
            HataKaydedici.kaydet("AramaBaslatici.sesliMesaj", e)
            false
        }
    }

    /** Kişi detayında "Görüntülü" butonu sadece hat destekliyorsa görünür. */
    @SuppressLint("MissingPermission")
    fun goruntuluDestekleniyor(): Boolean = try {
        val telecom = context.getSystemService(TelecomManager::class.java)
        telecom.callCapablePhoneAccounts.any { hesap ->
            telecom.getPhoneAccount(hesap)?.hasCapabilities(PhoneAccount.CAPABILITY_VIDEO_CALLING) == true
        }
    } catch (e: SecurityException) {
        HataKaydedici.kaydet("AramaBaslatici.goruntuluDestekleniyor", e)
        false
    }

    /** Alt sayfada izinler verildi → bekleyen numarayı otomatik ara. */
    fun izinlerTamamlandi() {
        val bekleyen = _izinEksik.value ?: return
        _izinEksik.value = null
        if (bekleyen.numara.isNotEmpty()) ara(bekleyen.numara)
    }

    /** "Şimdi değil" */
    fun vazgec() {
        _izinEksik.value = null
    }
}
