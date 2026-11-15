package com.atasoy.eymentelefon.ui.ekranlar.izineksik

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.arama.IzinEksikIstegi
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.izin.IzinDurumu
import com.atasoy.eymentelefon.cekirdek.izin.IzinListesi
import com.atasoy.eymentelefon.cekirdek.izin.IzinTuru
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.AltSayfa
import com.atasoy.eymentelefon.ui.bilesenler.EymenButon
import com.atasoy.eymentelefon.ui.bilesenler.EymenButonTuru
import com.atasoy.eymentelefon.ui.bilesenler.EymenCip
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import kotlinx.coroutines.flow.first

/**
 * Mockup 12. Bekleyen numara AramaBaslatici içinde kalır.
 * Bu bileşen TelecomManager çağırmaz; tüm izinler alındığında onTamam()
 * AramaBaslatici.izinlerTamamlandi() fonksiyonunu çağırır.
 */
@Composable
fun IzinEksikAltSayfa(
    istek: IzinEksikIstegi,
    onTamam: () -> Unit,
    onVazgec: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val k = context.konteyner()
    val yonetici = k.izinYoneticisi

    var denemeBasladi by remember(istek.numara) { mutableStateOf(false) }
    var sistemBekleniyor by remember(istek.numara) { mutableStateOf(false) }
    var sonrakiAdim by remember(istek.numara) { mutableIntStateOf(0) }
    var durumSurumu by remember(istek.numara) { mutableIntStateOf(0) }
    var ayarlardanIstenen by remember(istek.numara) { mutableStateOf<IzinTuru?>(null) }

    // Rol, Telefon ve ayarlardan dönüşte yalnızca başarıyla verilen izinlere ilerle.
    val rolIste = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        sistemBekleniyor = false
        durumSurumu++
        if (denemeBasladi && yonetici.varsayilanUygulamaMi()) {
            sonrakiAdim++
        } else {
            denemeBasladi = false
        }
    }

    val telefonIste = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        sistemBekleniyor = false
        durumSurumu++
        if (denemeBasladi && yonetici.verildiMi(IzinListesi.bul(IzinTuru.TELEFON))) {
            sonrakiAdim++
        } else {
            denemeBasladi = false
        }
    }

    val ayarlariAc = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        sistemBekleniyor = false
        durumSurumu++
        val tur = ayarlardanIstenen
        ayarlardanIstenen = null
        if (denemeBasladi && tur != null && yonetici.verildiMi(IzinListesi.bul(tur))) {
            sonrakiAdim++
        } else {
            denemeBasladi = false
        }
    }

    // Sistem ayarlarından dönünce "Hazır"/"Eksik" işaretlerini yenile.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { durumSurumu++ }
    val eksikler = remember(istek, durumSurumu) { yonetici.aramaIcinEksikler() }
    val telefonHazir = eksikler.none { it.tur == IzinTuru.TELEFON }
    val rolHazir = eksikler.none { it.tur == IzinTuru.VARSAYILAN_UYGULAMA }

    LaunchedEffect(istek.numara, sonrakiAdim) {
        if (!denemeBasladi || sistemBekleniyor) return@LaunchedEffect

        // Sıra IzinTanimi.kt içindeki sabit liste: önce rol, sonra Telefon.
        val eksik = yonetici.aramaIcinEksikler().firstOrNull()
        if (eksik == null) {
            denemeBasladi = false
            onTamam()
            return@LaunchedEffect
        }

        sistemBekleniyor = true
        when (eksik.tur) {
            IzinTuru.VARSAYILAN_UYGULAMA -> {
                val rolIntenti = yonetici.rolIstemeIntenti()
                if (rolIntenti != null) {
                    rolIste.launch(rolIntenti)
                } else {
                    ayarlardanIstenen = IzinTuru.VARSAYILAN_UYGULAMA
                    ayarlariAc.launch(
                        yonetici.uygulamaAyarlariIntenti().apply {
                            removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                }
            }
            IzinTuru.TELEFON -> {
                val izinler = yonetici.eksikAndroidIzinleri(eksik)
                if (izinler.isEmpty()) {
                    sistemBekleniyor = false
                    sonrakiAdim++
                } else {
                    val oncedenIstenenler = k.ayarDeposu.ayarlar.first().istenenIzinler
                    val durum = activity?.let {
                        yonetici.durum(eksik, it, oncedenIstenenler)
                    } ?: IzinDurumu.ISTENMEDI
                    if (durum == IzinDurumu.KALICI_REDDEDILDI) {
                        ayarlardanIstenen = IzinTuru.TELEFON
                        ayarlariAc.launch(
                            yonetici.uygulamaAyarlariIntenti().apply {
                                removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                    } else {
                        // Kullanıcıya istemeden önce DataStore'a kaydet (kalıcı ret hesabı).
                        k.ayarDeposu.izinlerIstendi(izinler.toList())
                        telefonIste.launch(izinler)
                    }
                }
            }
            else -> {
                // Arama için sadece rol ve Telefon beklenir.
                denemeBasladi = false
                sistemBekleniyor = false
            }
        }
    }

    AltSayfa(onKapat = onVazgec) {
        IzinEksikIcerik(
            telefonHazir = telefonHazir,
            rolHazir = rolHazir,
            onBasla = {
                if (!denemeBasladi && !sistemBekleniyor) {
                    denemeBasladi = true
                    sonrakiAdim++
                }
            },
            onVazgec = onVazgec,
            etkin = !sistemBekleniyor,
        )
    }
}

/** Sadece durum/olaylar: Compose önizlemeleri Android izin isteme penceresi açmaz. */
@Composable
private fun IzinEksikIcerik(
    telefonHazir: Boolean,
    rolHazir: Boolean,
    onBasla: () -> Unit,
    onVazgec: () -> Unit,
    etkin: Boolean = true,
) {
    val renk = EymenRenk
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = EymenBosluk.kenar),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EymenBosluk.l),
    ) {
        Box(
            modifier = Modifier.size(EymenBoyut.avatarFavoriIzgara)
                .clip(EymenSekil.aksiyon)
                .background(renk.vurgu.copy(alpha = .12f)),
            contentAlignment = Alignment.Center,
        ) {
            EymenIkonu(
                EymenIkon.Kalkan,
                stringResource(R.string.eksik_baslik),
                renk.vurgu,
                boyut = EymenBoyut.ikonBuyuk,
            )
        }
        Text(
            text = stringResource(R.string.eksik_baslik),
            style = EymenYazi.baslik1,
            color = renk.metin1,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.eksik_aciklama),
            style = EymenYazi.govde,
            color = renk.metin2,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(EymenBosluk.xs))
        Column(
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.s),
            modifier = Modifier.fillMaxWidth(),
        ) {
            IzinDurumSatiri(
                ikon = EymenIkon.Telefon,
                baslik = stringResource(R.string.eksik_telefon_izni),
                hazir = telefonHazir,
            )
            IzinDurumSatiri(
                ikon = EymenIkon.Cihaz,
                baslik = stringResource(R.string.eksik_varsayilan),
                hazir = rolHazir,
            )
        }
        Spacer(Modifier.size(EymenBosluk.xs))
        EymenButon(
            metin = stringResource(R.string.eksik_izin_ver_ara),
            onClick = onBasla,
            etkin = etkin,
        )
        EymenButon(
            metin = stringResource(R.string.eksik_simdi_degil),
            onClick = onVazgec,
            tur = EymenButonTuru.IKINCIL,
        )
        Spacer(Modifier.size(EymenBosluk.l))
    }
}

@Composable
private fun IzinDurumSatiri(
    ikon: EymenIkon,
    baslik: String,
    hazir: Boolean,
) {
    val renk = EymenRenk
    Row(
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = EymenBoyut.kaydirRay)
            .clip(EymenSekil.aksiyon)
            .background(renk.yuzey2)
            .padding(horizontal = EymenBosluk.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
    ) {
        EymenIkonu(
            ikon = ikon,
            aciklama = baslik,
            renk = if (hazir) renk.vurgu else renk.cevapsiz,
        )
        Text(
            text = baslik,
            style = EymenYazi.isim,
            color = renk.metin1,
            modifier = Modifier.weight(1f),
        )
        EymenCip(
            metin = stringResource(if (hazir) R.string.eksik_hazir else R.string.eksik_eksik),
            olumlu = hazir,
            uyari = !hazir,
        )
    }
}

@Preview(name = "Koyu", showBackground = true)
@Composable private fun IzinEksikKoyuOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        IzinEksikIcerik(telefonHazir = false, rolHazir = true, onBasla = {}, onVazgec = {})
    }
}

@Preview(name = "Acik", showBackground = true)
@Composable private fun IzinEksikAcikOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        IzinEksikIcerik(telefonHazir = true, rolHazir = false, onBasla = {}, onVazgec = {})
    }
}
