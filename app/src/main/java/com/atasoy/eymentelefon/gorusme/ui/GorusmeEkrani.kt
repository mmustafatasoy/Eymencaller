package com.atasoy.eymentelefon.gorusme.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.PowerManager
import android.provider.ContactsContract
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.gorusme.GorusmeAsamasi
import com.atasoy.eymentelefon.gorusme.GorusmeDurumu
import com.atasoy.eymentelefon.gorusme.SesYolu
import com.atasoy.eymentelefon.ui.bilesenler.AltSayfa
import com.atasoy.eymentelefon.ui.bilesenler.AramaKontrolButonu
import com.atasoy.eymentelefon.ui.bilesenler.KapatButonu
import com.atasoy.eymentelefon.ui.bilesenler.KisiAvatari
import com.atasoy.eymentelefon.ui.bilesenler.TusTakimi
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GorusmeEkrani(
    vm: GorusmeViewModel = viewModel(factory = GorusmeViewModel.Fabrika),
) {
    val context = LocalContext.current
    val durum by vm.durum.collectAsStateWithLifecycle()
    YakinlikEkranKilidi(durum)

    fun ac(intent: Intent, ad: String) {
        try {
            context.startActivity(intent)
        } catch (hata: RuntimeException) {
            HataKaydedici.kaydet(ad, hata)
        }
    }

    GorusmeIcerik(
        durum = durum,
        onOlay = vm::olay,
        onAramaEkle = {
            // ACTION_DIAL sadece numara giriş ekranını açar; çağrı burada başlatılmaz.
            ac(Intent(Intent.ACTION_DIAL), "GorusmeEkrani.aramaEkle")
        },
        onKisiler = {
            ac(
                Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI),
                "GorusmeEkrani.kisiler",
            )
        },
    )
}

/** Sadece kulaklık/ahize görüşmesinde yakınlık sensörü ekranı söndürür. */
@SuppressLint("WakelockTimeout")
@Composable
private fun YakinlikEkranKilidi(durum: GorusmeUiState) {
    val context = LocalContext.current
    val pm = remember(context) { context.getSystemService(PowerManager::class.java) }
    val kilit = remember(pm) {
        try {
            if (pm?.isWakeLockLevelSupported(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK) == true)
                pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "EymenTelefon:Yakinlik")
            else null
        } catch (hata: RuntimeException) {
            HataKaydedici.kaydet("Gorusme.yakinlikKur", hata)
            null
        }
    }
    val acikOlmali = durum.gorusme?.asama == GorusmeAsamasi.AKTIF &&
        durum.ses.yol == SesYolu.TELEFON
    DisposableEffect(kilit, acikOlmali) {
        if (acikOlmali && kilit != null) {
            try {
                if (!kilit.isHeld) kilit.acquire()
            } catch (hata: RuntimeException) {
                HataKaydedici.kaydet("Gorusme.yakinlikAc", hata)
            }
        }
        onDispose {
            if (kilit != null) {
                try {
                    if (kilit.isHeld) kilit.release()
                } catch (hata: RuntimeException) {
                    HataKaydedici.kaydet("Gorusme.yakinlikKapat", hata)
                }
            }
        }
    }
}

@Composable
private fun GorusmeIcerik(
    durum: GorusmeUiState,
    onOlay: (GorusmeOlay) -> Unit,
    onAramaEkle: () -> Unit,
    onKisiler: () -> Unit,
) {
    val renk = EymenRenk
    val g = durum.gorusme
    val ad = durum.ad.ifBlank {
        g?.numara?.takeIf(String::isNotBlank)
            ?: stringResource(R.string.gorusme_bilinmeyen)
    }
    val toplam = durum.sureSaniye
    val saat = toplam / 3600L
    val dakika = (toplam / 60L) % 60L
    val saniye = toplam % 60L
    val sure = if (saat > 0L) {
        "%d:%02d:%02d".format(saat, dakika, saniye)
    } else {
        "%02d:%02d".format(dakika, saniye)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(renk.aramaZemini)
            .padding(horizontal = EymenBosluk.kenar, vertical = EymenBosluk.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.s),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (g?.hd == true) {
                    Box(
                        Modifier.background(renk.vurgu.copy(alpha = .18f), EymenSekil.etiket)
                            .padding(horizontal = EymenBosluk.m, vertical = EymenBosluk.xs),
                    ) {
                        Text(stringResource(R.string.gorusme_hd), style = EymenYazi.etiket, color = renk.vurgu)
                    }
                }
                g?.simSirasi?.let {
                    Text(
                        stringResource(R.string.gorusme_sim, it),
                        style = EymenYazi.kucuk, color = renk.metin2,
                    )
                }
            }
            if (g?.asama == GorusmeAsamasi.BEKLEMEDE) {
                Text(
                    stringResource(R.string.gorusme_beklemede),
                    style = EymenYazi.govde, color = renk.uyari,
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.m),
        ) {
            KisiAvatari(ad, fotoUri = durum.fotoUri, boyut = EymenBoyut.avatarGorusme)
            Text(ad, style = EymenYazi.aramaIsim, color = AramaEkraniRenk.beyaz)
            Text(sure, style = EymenYazi.sayac, color = renk.metin2)
            SusDalgasi()
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.xl),
        ) {
            if (durum.tuslarAcik) {
                TusTakimi(
                    onTus = { onOlay(GorusmeOlay.Dtmf(it)) },
                    onUzunBas = { onOlay(GorusmeOlay.Dtmf(it)) },
                )
                Box(
                    modifier = Modifier.heightIn(min = EymenBoyut.dokunmaMin)
                        .clickable { onOlay(GorusmeOlay.TuslariDegistir) }
                        .padding(EymenBosluk.s),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        stringResource(R.string.kontrol_tuslar),
                        color = AramaEkraniRenk.beyaz, style = EymenYazi.govde,
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(EymenBosluk.xl)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        AramaKontrolButonu(
                            EymenIkon.Sessiz, stringResource(R.string.kontrol_sessiz),
                            durum.ses.sessiz, { onOlay(GorusmeOlay.SessizDegistir) },
                        )
                        AramaKontrolButonu(
                            EymenIkon.Tuslar, stringResource(R.string.kontrol_tus_takimi),
                            false, { onOlay(GorusmeOlay.TuslariDegistir) },
                        )
                        AramaKontrolButonu(
                            EymenIkon.Hoparlor,
                            stringResource(
                                if (durum.ses.bluetoothVar) R.string.kontrol_ses
                                else R.string.kontrol_hoparlor
                            ),
                            durum.ses.yol == SesYolu.HOPARLOR,
                            {
                                onOlay(
                                    if (durum.ses.bluetoothVar) GorusmeOlay.SesMenusuDegistir
                                    else GorusmeOlay.HoparlorDegistir
                                )
                            },
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        if (g?.beklemeDesteklenir == true) {
                            AramaKontrolButonu(
                                EymenIkon.Beklet, stringResource(R.string.kontrol_beklet),
                                g.asama == GorusmeAsamasi.BEKLEMEDE,
                                { onOlay(GorusmeOlay.BekletDegistir) },
                            )
                        }
                        AramaKontrolButonu(
                            EymenIkon.Arti, stringResource(R.string.kontrol_arama_ekle),
                            false, onAramaEkle,
                        )
                        AramaKontrolButonu(
                            EymenIkon.Kisiler, stringResource(R.string.kontrol_kisiler),
                            false, onKisiler,
                        )
                    }
                }
            }
            KapatButonu(onClick = { onOlay(GorusmeOlay.Kapat) })
        }
    }

    if (durum.sesMenusuAcik) {
        AltSayfa(onKapat = { onOlay(GorusmeOlay.SesMenusuDegistir) }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = EymenBosluk.kenar),
                verticalArrangement = Arrangement.spacedBy(EymenBosluk.m),
            ) {
                Text(
                    stringResource(R.string.kontrol_ses),
                    style = EymenYazi.baslik2,
                    color = renk.metin1,
                )
                val yollar = buildList {
                    add(SesYolu.TELEFON to R.string.ses_telefon)
                    add(SesYolu.HOPARLOR to R.string.kontrol_hoparlor)
                    if (durum.ses.bluetoothVar) add(SesYolu.BLUETOOTH to R.string.ses_bluetooth)
                    if (durum.ses.kulaklikVar) add(SesYolu.KULAKLIK to R.string.ses_kulaklik)
                }
                yollar.forEach { (yol, metin) ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = EymenBoyut.anaButon)
                            .clickable { onOlay(GorusmeOlay.SesYoluSec(yol)) }
                            .padding(horizontal = EymenBosluk.m),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                    ) {
                        EymenIkonu(
                            when (yol) {
                                SesYolu.BLUETOOTH -> EymenIkon.Bluetooth
                                SesYolu.HOPARLOR -> EymenIkon.Hoparlor
                                else -> EymenIkon.Telefon
                            },
                            aciklama = stringResource(metin),
                            renk = renk.metin2,
                        )
                        Text(
                            stringResource(metin),
                            style = EymenYazi.govde,
                            color = if (durum.ses.yol == yol) renk.vurgu else renk.metin1,
                        )
                    }
                }
                Spacer(Modifier.height(EymenBosluk.l))
            }
        }
    }
}

/** Görsel süs: mikrofon okumaz ve ses kaydetmez. */
@Composable
private fun SusDalgasi() {
    val renk = EymenRenk
    val uzunluklar = remember { mutableStateListOf<Int>().apply { repeat(15) { add(1) } } }
    LaunchedEffect(Unit) {
        while (true) {
            for (i in uzunluklar.indices) uzunluklar[i] = Random.nextInt(1, 4)
            delay(120L)
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        uzunluklar.forEach { uzunluk ->
            Box(
                modifier = Modifier.size(
                    EymenBosluk.xs,
                    when (uzunluk) {
                        1 -> EymenBosluk.s
                        2 -> EymenBosluk.l
                        else -> EymenBosluk.xl
                    }
                ).clip(CircleShape).background(renk.vurgu.copy(alpha = .7f)),
            )
        }
    }
}

@Preview(name = "Koyu")
@Composable private fun GorusmeKoyuOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU, zorlaKoyu = true) {
        GorusmeIcerik(
            GorusmeUiState(
                gorusme = GorusmeDurumu(
                    "g3", "05321234567", "Ayşe Demir", GorusmeAsamasi.AKTIF,
                    System.currentTimeMillis(), true, true, false, 1,
                ),
                ad = "Ayşe Demir",
                sureSaniye = 80L,
            ), onOlay = {}, onAramaEkle = {}, onKisiler = {},
        )
    }
}

@Preview(name = "Acik")
@Composable private fun GorusmeAcikOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK, zorlaKoyu = true) {
        GorusmeIcerik(GorusmeUiState(), onOlay = {}, onAramaEkle = {}, onKisiler = {})
    }
}
