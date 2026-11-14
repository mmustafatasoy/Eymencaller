package com.atasoy.eymentelefon.gorusme

// ✅ HAZIR — DEĞİŞTİRME.
// android.telecom.Call nesnelerine dokunan TEK yer. Ekranlar sadece aşağıdaki public fonksiyonları çağırır.
// Tüm çağrılar ana thread'de (Telecom geri çağırımları ana thread'de gelir).

import android.annotation.SuppressLint
import android.os.Build
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import java.lang.ref.WeakReference
import java.util.IdentityHashMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object GorusmeYoneticisi {

    private val aramalar = LinkedHashMap<String, Call>()
    private val kimlikler = IdentityHashMap<Call, String>()
    private var sayac = 0
    private var servis: WeakReference<InCallService>? = null

    private val _gorusmeler = MutableStateFlow<List<GorusmeDurumu>>(emptyList())
    val gorusmeler: StateFlow<List<GorusmeDurumu>> = _gorusmeler.asStateFlow()

    private val _ses = MutableStateFlow(SesDurumu())
    val ses: StateFlow<SesDurumu> = _ses.asStateFlow()

    /** Ekranda gösterilecek arama: çalan > aktif > aranıyor > beklemede. */
    fun oncelikli(liste: List<GorusmeDurumu> = _gorusmeler.value): GorusmeDurumu? =
        liste.minByOrNull {
            when (it.asama) {
                GorusmeAsamasi.CALIYOR -> 0
                GorusmeAsamasi.AKTIF -> 1
                GorusmeAsamasi.ARANIYOR, GorusmeAsamasi.BAGLANIYOR -> 2
                GorusmeAsamasi.BEKLEMEDE -> 3
                GorusmeAsamasi.BITTI -> 4
            }
        }

    // ---------------- Kontroller (ekranlar bunları çağırır) ----------------

    fun cevapla(id: String) = guvenli("cevapla") { bul(id)?.answer(VideoProfile.STATE_AUDIO_ONLY) }

    /** @param mesaj Hızlı yanıt ile reddetmek için (null = sadece reddet) */
    fun reddet(id: String, mesaj: String? = null) = guvenli("reddet") {
        bul(id)?.reject(mesaj != null, mesaj)
    }

    fun kapat(id: String) = guvenli("kapat") { bul(id)?.disconnect() }

    fun beklet(id: String, beklet: Boolean) = guvenli("beklet") {
        bul(id)?.let { if (beklet) it.hold() else it.unhold() }
    }

    fun dtmf(id: String, tus: Char) = guvenli("dtmf") {
        bul(id)?.let {
            it.playDtmfTone(tus)
            it.stopDtmfTone()
        }
    }

    fun sessiz(acik: Boolean) = guvenli("sessiz") { servis?.get()?.setMuted(acik) }

    @Suppress("DEPRECATION") // setAudioRoute API 34'te eskidi ama tüm sürümlerde çalışır
    fun sesYolu(yol: SesYolu) = guvenli("sesYolu") {
        val rota = when (yol) {
            SesYolu.TELEFON -> CallAudioState.ROUTE_WIRED_OR_EARPIECE
            SesYolu.KULAKLIK -> CallAudioState.ROUTE_WIRED_HEADSET
            SesYolu.HOPARLOR -> CallAudioState.ROUTE_SPEAKER
            SesYolu.BLUETOOTH -> CallAudioState.ROUTE_BLUETOOTH
        }
        servis?.get()?.setAudioRoute(rota)
    }

    // ---------------- EymenInCallService tarafından çağrılır ----------------

    internal fun servisBaglandi(s: InCallService) {
        servis = WeakReference(s)
    }

    internal fun servisAyrildi(s: InCallService) {
        if (servis?.get() === s) servis = null
        aramalar.values.forEach { it.unregisterCallback(geriCagirim) }
        aramalar.clear()
        kimlikler.clear()
        yayinla()
    }

    internal fun ekle(call: Call) {
        val id = "g${++sayac}"
        aramalar[id] = call
        kimlikler[call] = id
        call.registerCallback(geriCagirim)
        yayinla()
    }

    internal fun cikar(call: Call) {
        call.unregisterCallback(geriCagirim)
        kimlikler.remove(call)?.let { aramalar.remove(it) }
        yayinla()
    }

    @Suppress("DEPRECATION")
    internal fun sesDegisti(durum: CallAudioState) {
        val maske = durum.supportedRouteMask
        _ses.value = SesDurumu(
            sessiz = durum.isMuted,
            yol = when (durum.route) {
                CallAudioState.ROUTE_SPEAKER -> SesYolu.HOPARLOR
                CallAudioState.ROUTE_BLUETOOTH -> SesYolu.BLUETOOTH
                CallAudioState.ROUTE_WIRED_HEADSET -> SesYolu.KULAKLIK
                else -> SesYolu.TELEFON
            },
            bluetoothVar = maske and CallAudioState.ROUTE_BLUETOOTH != 0,
            kulaklikVar = maske and CallAudioState.ROUTE_WIRED_HEADSET != 0,
        )
    }

    // ---------------- İç ----------------

    private val geriCagirim = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) = yayinla()
        override fun onDetailsChanged(call: Call, details: Call.Details) = yayinla()
    }

    private fun bul(id: String): Call? = aramalar[id]

    private fun yayinla() {
        _gorusmeler.value = aramalar.map { (id, call) -> donustur(id, call) }
    }

    @Suppress("DEPRECATION")
    private fun durumKodu(call: Call): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) call.details.state else call.state

    private fun donustur(id: String, call: Call): GorusmeDurumu {
        val d = call.details
        val asama = when (durumKodu(call)) {
            Call.STATE_NEW, Call.STATE_DIALING -> GorusmeAsamasi.ARANIYOR
            Call.STATE_CONNECTING, Call.STATE_SELECT_PHONE_ACCOUNT, Call.STATE_PULLING_CALL -> GorusmeAsamasi.BAGLANIYOR
            Call.STATE_RINGING, Call.STATE_SIMULATED_RINGING -> GorusmeAsamasi.CALIYOR
            Call.STATE_ACTIVE, Call.STATE_AUDIO_PROCESSING -> GorusmeAsamasi.AKTIF
            Call.STATE_HOLDING -> GorusmeAsamasi.BEKLEMEDE
            else -> GorusmeAsamasi.BITTI // DISCONNECTING, DISCONNECTED
        }
        val ad = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            d.contactDisplayName ?: d.callerDisplayName
        } else {
            d.callerDisplayName
        }
        return GorusmeDurumu(
            id = id,
            numara = d.handle?.schemeSpecificPart.orEmpty(),
            sistemAdi = ad?.takeIf { it.isNotBlank() },
            asama = asama,
            baglanmaZamani = d.connectTimeMillis.takeIf { it > 0 },
            hd = d.hasProperty(Call.Details.PROPERTY_HIGH_DEF_AUDIO),
            beklemeDesteklenir = d.can(Call.Details.CAPABILITY_HOLD),
            konferans = d.hasProperty(Call.Details.PROPERTY_CONFERENCE),
            simSirasi = simSirasi(d),
        )
    }

    @SuppressLint("MissingPermission") // READ_PHONE_STATE zorunlu izinlerde; yoksa SecurityException yakalanır
    private fun simSirasi(d: Call.Details): Int? {
        val s = servis?.get() ?: return null
        return try {
            val telecom = s.getSystemService(TelecomManager::class.java)
            val hesaplar = telecom.callCapablePhoneAccounts
            if (hesaplar.size < 2) null else hesaplar.indexOf(d.accountHandle).takeIf { it >= 0 }?.plus(1)
        } catch (e: SecurityException) {
            HataKaydedici.kaydet("GorusmeYoneticisi.simSirasi", e)
            null
        }
    }

    private inline fun guvenli(islem: String, blok: () -> Unit) {
        try {
            blok()
        } catch (e: RuntimeException) {
            HataKaydedici.kaydet("GorusmeYoneticisi.$islem", e)
        }
    }
}
