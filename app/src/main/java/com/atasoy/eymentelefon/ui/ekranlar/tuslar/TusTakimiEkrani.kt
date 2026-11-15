package com.atasoy.eymentelefon.ui.ekranlar.tuslar

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.Ayarlar
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.EymenAramaCubugu
import com.atasoy.eymentelefon.ui.bilesenler.EymenCip
import com.atasoy.eymentelefon.ui.bilesenler.KisiAvatari
import com.atasoy.eymentelefon.ui.bilesenler.TusTakimi
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenGradyan
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

/** 03: Gerçek arama / numara işlemleri ViewModel'de, çizim durumsuz içerikte. */
@SuppressLint("MissingPermission") // Sistem izni kullanıcıdan istenir; sesli mesaj sorgusunda SecurityException da kaydedilir.
@Composable
fun TusTakimiEkrani(
    baslangicNumara: String,
    onAramaAc: () -> Unit,
    vm: TusTakimiViewModel = viewModel(factory = TusTakimiViewModel.Fabrika),
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val ayarlar by context.konteyner().ayarDeposu.ayarlar.collectAsStateWithLifecycle(initialValue = Ayarlar())
    val state by vm.durum.collectAsStateWithLifecycle()
    val tonUretici = remember {
        try { ToneGenerator(AudioManager.STREAM_DTMF, 60) }
        catch (hata: RuntimeException) {
            HataKaydedici.kaydet("TusTakimi.ToneGenerator", hata)
            null
        }
    }
    DisposableEffect(tonUretici) { onDispose { tonUretici?.release() } }
    LaunchedEffect(baslangicNumara) {
        if (baslangicNumara.isNotBlank()) vm.olay(TusTakimiOlay.NumaraDoldur(baslangicNumara))
    }

    fun tusGeribildirim(tus: Char) {
        if (ayarlar.tusTitresimi) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        if (ayarlar.tusSesi) {
            val ton = when (tus) {
                '*' -> ToneGenerator.TONE_DTMF_S
                '#' -> ToneGenerator.TONE_DTMF_P
                in '0'..'9' -> ToneGenerator.TONE_DTMF_0 + (tus - '0')
                else -> null
            }
            if (ton != null) {
                try { tonUretici?.startTone(ton, 90) }
                catch (hata: RuntimeException) { HataKaydedici.kaydet("TusTakimi.ses", hata) }
            }
        }
    }

    TusTakimiIcerik(
        state = state,
        onAramaAc = onAramaAc,
        onTus = { tus ->
            vm.olay(TusTakimiOlay.TusaBasildi(tus))
            tusGeribildirim(tus)
        },
        onUzunTus = { tus ->
            when (tus) {
                '0' -> vm.olay(TusTakimiOlay.TusaBasildi('+'))
                '1' -> {
                    // Sesli mesaj numarası operatör/SIM tarafından sağlanır.
                    try {
                        val servis = context.getSystemService(TelephonyManager::class.java)
                        val numara = servis?.voiceMailNumber
                        if (!numara.isNullOrBlank()) {
                            context.konteyner().aramaBaslatici.ara(numara)
                        } else {
                            vm.olay(TusTakimiOlay.TusaBasildi('1'))
                        }
                    } catch (hata: SecurityException) {
                        HataKaydedici.kaydet("TusTakimi.sesliMesaj", hata)
                        vm.olay(TusTakimiOlay.TusaBasildi('1'))
                    }
                }
                else -> vm.olay(TusTakimiOlay.TusaBasildi(tus))
            }
            tusGeribildirim(tus)
        },
        onSil = { vm.olay(TusTakimiOlay.Sil) },
        onTemizle = { vm.olay(TusTakimiOlay.TumunuSil) },
        onAra = { vm.olay(TusTakimiOlay.Ara) },
        onEslesmeSec = { secilen -> vm.olay(TusTakimiOlay.NumaraDoldur(secilen)) },
        onKisiEkle = {
            try {
                context.startActivity(
                    Intent(Intent.ACTION_INSERT).apply {
                        type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
                        putExtra(ContactsContract.Intents.Insert.PHONE, state.numara)
                    }
                )
            } catch (hata: RuntimeException) {
                HataKaydedici.kaydet("TusTakimi.kisiEkle", hata)
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TusTakimiIcerik(
    state: TusTakimiUiState,
    onAramaAc: () -> Unit,
    onTus: (Char) -> Unit,
    onUzunTus: (Char) -> Unit,
    onSil: () -> Unit,
    onTemizle: () -> Unit,
    onAra: () -> Unit,
    onEslesmeSec: (String) -> Unit,
    onKisiEkle: () -> Unit,
) {
    val r = EymenRenk
    val araAciklama = stringResource(R.string.ae_ara)
    val silAciklama = stringResource(R.string.ae_sil)
    val kisiEkleAciklama = stringResource(R.string.ae_kisi_ekle)
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = EymenBosluk.kenar),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(EymenBosluk.l))
        Box(Modifier.fillMaxWidth()) {
            EymenAramaCubugu(deger = "", onDegerDegisti = { onAramaAc() }, profilAdi = null)
            Box(
                Modifier.matchParentSize().clickable(onClickLabel = araAciklama, onClick = onAramaAc)
            )
        }
        Spacer(Modifier.height(EymenBosluk.xxl))
        Box(
            modifier = Modifier.fillMaxWidth().height(EymenBoyut.anaButon),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (state.numara.isBlank()) " " else NumaraBicimleyici.bicimle(state.numara),
                style = EymenYazi.numaraEkrani,
                color = r.metin1,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
        if (state.numara.length >= 3) {
            Spacer(Modifier.height(EymenBosluk.m))
            Row(
                modifier = Modifier.fillMaxWidth().background(r.yuzey1, EymenSekil.kart)
                    .padding(EymenBosluk.m),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
            ) {
                val kisi = state.eslesme
                if (kisi != null) {
                    KisiAvatari(ad = kisi.ad, fotoUri = kisi.fotoUri, boyut = EymenBoyut.avatarAramaListe)
                    Column(Modifier.weight(1f)) {
                        Text(kisi.ad, style = EymenYazi.isim, color = r.metin1)
                        Text(
                            NumaraBicimleyici.bicimle(kisi.varsayilanNumara?.numara.orEmpty()),
                            style = EymenYazi.kucuk, color = r.metin3,
                        )
                    }
                    EymenCip(metin = stringResource(R.string.tus_ara), onClick = {
                        kisi.varsayilanNumara?.let { onEslesmeSec(it.numara) }
                    })
                } else {
                    Text(
                        text = NumaraBicimleyici.bicimle(state.numara),
                        modifier = Modifier.weight(1f),
                        style = EymenYazi.govde, color = r.metin2,
                    )
                    EymenCip(metin = stringResource(R.string.tus_ekle), olumlu = true, onClick = onKisiEkle)
                }
            }
        }
        Spacer(Modifier.height(EymenBosluk.xl))
        TusTakimi(onTus = onTus, onUzunBas = onUzunTus)
        Spacer(Modifier.height(EymenBosluk.m))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Box(
                Modifier.size(EymenBoyut.dokunmaMin)
                    .clickable(onClickLabel = kisiEkleAciklama, onClick = onKisiEkle),
                contentAlignment = Alignment.Center,
            ) {
                EymenIkonu(EymenIkon.KisiEkle, kisiEkleAciklama, r.metin3)
            }
            Box(
                Modifier.size(EymenBoyut.araButonu).background(EymenGradyan.ara(r), CircleShape)
                    .clickable(onClickLabel = araAciklama, onClick = onAra),
                contentAlignment = Alignment.Center,
            ) {
                EymenIkonu(EymenIkon.Telefon, araAciklama, r.vurguUstu, boyut = EymenBoyut.ikonBuyuk)
            }
            Box(
                Modifier.size(EymenBoyut.dokunmaMin).combinedClickable(
                    onClickLabel = silAciklama,
                    onClick = onSil,
                    onLongClick = onTemizle,
                ),
                contentAlignment = Alignment.Center,
            ) {
                EymenIkonu(EymenIkon.Sil, silAciklama, r.metin3)
            }
        }
        Spacer(Modifier.height(EymenBosluk.l))
    }
}

@Preview(name = "Koyu")
@Composable private fun TusTakimiKoyu() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        TusTakimiIcerik(TusTakimiUiState(numara = "05321234567"), {}, {}, {}, {}, {}, {}, {}, {})
    }
}

@Preview(name = "Açık")
@Composable private fun TusTakimiAcik() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        TusTakimiIcerik(TusTakimiUiState(numara = ""), {}, {}, {}, {}, {}, {}, {}, {})
    }
}
