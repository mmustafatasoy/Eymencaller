package com.atasoy.eymentelefon.ui.ekranlar.izineksik

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.izin.IzinListesi
import com.atasoy.eymentelefon.cekirdek.izin.IzinTuru
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.IosUyari
import com.atasoy.eymentelefon.ui.bilesenler.UyariButonu

/**
 * 17 · Aramaya basınca izin eksikse (iOS uyarısı). "İzin Ver ve Ara": önce varsayılan uygulama rolü,
 * sonra Telefon izni istenir; ikisi de tamamsa bekleyen numara otomatik aranır.
 */
@Composable
fun IzinEksikUyarisi() {
    val context = LocalContext.current
    val k = remember { context.konteyner() }
    val izinler = k.izinYoneticisi
    val baslatici = k.aramaBaslatici
    val telefon = remember { IzinListesi.bul(IzinTuru.TELEFON) }

    lateinit var devam: () -> Unit
    val izinIstegi = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { sonuc ->
        if (sonuc.values.all { it }) devam() else baslatici.vazgec()
    }
    val rolIstegi = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (izinler.varsayilanUygulamaMi()) devam() else baslatici.vazgec()
    }
    devam = {
        val eksikTelefon = izinler.eksikAndroidIzinleri(telefon)
        when {
            !izinler.varsayilanUygulamaMi() -> izinler.rolIstemeIntenti()?.let { rolIstegi.launch(it) } ?: baslatici.vazgec()
            eksikTelefon.isNotEmpty() -> izinIstegi.launch(eksikTelefon)
            else -> baslatici.izinlerTamamlandi()
        }
    }

    IosUyari(
        baslik = stringResource(R.string.eksik_baslik),
        mesaj = stringResource(R.string.eksik_aciklama),
        butonlar = listOf(
            UyariButonu(stringResource(R.string.simdi_degil), baslatici::vazgec),
            UyariButonu(stringResource(R.string.eksik_izin_ver_ara), { devam() }, kalin = true),
        ),
        onKapat = baslatici::vazgec,
    )
}
