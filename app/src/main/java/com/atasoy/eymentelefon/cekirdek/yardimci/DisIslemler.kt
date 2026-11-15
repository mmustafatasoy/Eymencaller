package com.atasoy.eymentelefon.cekirdek.yardimci

// ✅ HAZIR. Sistem uygulamalarını açan tüm Intent'ler tek yerde (mesaj, kişi ekle/düzenle, e-posta, paylaş, kopyala).
// Bulunamazsa çökmez, HataKaydedici'ye yazar.

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici

object DisIslemler {

    fun mesajGonder(context: Context, numara: String, metin: String? = null) = baslat(context, "mesaj") {
        Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", numara, null)).apply {
            if (metin != null) putExtra("sms_body", metin)
        }
    }

    fun kisiyeEkle(context: Context, numara: String) = baslat(context, "kisiyeEkle") {
        Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI)
            .putExtra(ContactsContract.Intents.Insert.PHONE, numara)
    }

    fun yeniKisi(context: Context) = baslat(context, "yeniKisi") {
        Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI)
    }

    fun kisiDuzenle(context: Context, kisiUri: Uri) = baslat(context, "kisiDuzenle") {
        Intent(Intent.ACTION_EDIT, kisiUri).putExtra("finishActivityOnSaveCompleted", true)
    }

    fun epostaGonder(context: Context, adres: String) = baslat(context, "eposta") {
        Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", adres, null))
    }

    fun vcardPaylas(context: Context, vcard: Uri) = baslat(context, "paylas") {
        Intent.createChooser(
            Intent(Intent.ACTION_SEND)
                .setType(ContactsContract.Contacts.CONTENT_VCARD_TYPE)
                .putExtra(Intent.EXTRA_STREAM, vcard)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
            null,
        )
    }

    fun metinPaylas(context: Context, metin: String) = baslat(context, "metinPaylas") {
        Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, metin), null)
    }

    fun kopyala(context: Context, etiket: String, metin: String) {
        context.getSystemService(ClipboardManager::class.java)?.setPrimaryClip(ClipData.newPlainText(etiket, metin))
    }

    fun baslat(context: Context, etiket: String, intent: () -> Intent): Boolean = try {
        context.startActivity(intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (e: ActivityNotFoundException) {
        HataKaydedici.kaydet("DisIslemler.$etiket", e)
        false
    } catch (e: SecurityException) {
        HataKaydedici.kaydet("DisIslemler.$etiket", e)
        false
    }
}
