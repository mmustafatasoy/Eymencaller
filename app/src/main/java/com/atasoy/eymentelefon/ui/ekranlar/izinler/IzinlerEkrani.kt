package com.atasoy.eymentelefon.ui.ekranlar.izinler

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.izin.IzinDurumu
import com.atasoy.eymentelefon.cekirdek.izin.IzinListesi
import com.atasoy.eymentelefon.cekirdek.izin.IzinTuru
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.EymenButon
import com.atasoy.eymentelefon.ui.bilesenler.IzinKarti
import com.atasoy.eymentelefon.ui.bilesenler.IzinKartiDurumu
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenGradyan
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

/** 02: Android izin akışı; izin kontrolü yalnızca IzinYoneticisi üzerinden yapılır. */
@Composable
fun IzinlerEkrani(
    ayarModu: Boolean,
    onDevam: () -> Unit,
    onGeri: () -> Unit,
    vm: IzinlerViewModel = viewModel(factory = IzinlerViewModel.Fabrika),
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val yonetici = context.konteyner().izinYoneticisi
    val state by vm.durum.collectAsStateWithLifecycle()

    val androidIzinIste = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        activity?.let(vm::yenile)
    }
    val rolIste = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        activity?.let(vm::yenile)
    }
    val ayarlarAc = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        activity?.let(vm::yenile)
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        activity?.let(vm::yenile)
    }

    IzinlerIcerik(
        state = state,
        ayarModu = ayarModu,
        onIzinIste = { tur ->
            val tanim = IzinListesi.bul(tur)
            val izinDurumu = state.kartlar.firstOrNull { it.tanim.tur == tur }?.durum
            when {
                tur == IzinTuru.VARSAYILAN_UYGULAMA -> {
                    val rolIntent = yonetici.rolIstemeIntenti()
                    if (rolIntent == null) {
                        ayarlarAc.launch(yonetici.uygulamaAyarlariIntenti())
                    } else {
                        rolIste.launch(rolIntent)
                    }
                }
                izinDurumu == IzinDurumu.KALICI_REDDEDILDI ->
                    ayarlarAc.launch(yonetici.uygulamaAyarlariIntenti())
                else -> vm.istegiKaydet(tur) {
                    androidIzinIste.launch(tanim.androidIzinleri.toTypedArray())
                }
            }
        },
        onDevam = {
            if (ayarModu) onGeri() else vm.tamamla(onDevam)
        },
        onGeri = onGeri,
    )
}

@Composable
fun IzinlerIcerik(
    state: IzinlerUiState,
    ayarModu: Boolean,
    onIzinIste: (IzinTuru) -> Unit,
    onDevam: () -> Unit,
    onGeri: () -> Unit,
) {
    val r = EymenRenk
    var nedenAcik by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().background(r.arkaPlan).safeDrawingPadding()
            .padding(horizontal = EymenBosluk.kenar),
    ) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.kartArasi),
        ) {
            Spacer(Modifier.height(EymenBosluk.xl))
            if (ayarModu) {
                Box(
                    Modifier.size(EymenBoyut.dokunmaMin).clickable(onClick = onGeri),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    EymenIkonu(EymenIkon.Geri, stringResource(R.string.ae_geri), r.metin1)
                }
            } else {
                Text(stringResource(R.string.izin_adim), style = EymenYazi.bolum, color = r.vurgu)
            }
            Text(stringResource(R.string.izin_baslik), style = EymenYazi.baslik1, color = r.metin1)
            Text(stringResource(R.string.izin_aciklama), style = EymenYazi.govde, color = r.metin2)
            Spacer(Modifier.height(EymenBosluk.xs))
            val oran = if (state.toplam == 0) 0f else state.verilen.toFloat() / state.toplam
            Box(
                Modifier.fillMaxWidth().height(EymenBoyut.ilerlemeCubugu)
                    .clip(EymenSekil.hap).background(r.yuzey3),
            ) {
                Box(
                    Modifier.fillMaxWidth(oran).height(EymenBoyut.ilerlemeCubugu)
                        .background(EymenGradyan.vurgu(r)),
                )
            }
            Text(
                stringResource(R.string.izin_ilerleme, state.verilen, state.toplam),
                style = EymenYazi.kucuk, color = r.metin3,
            )
            Spacer(Modifier.height(EymenBosluk.xs))
            state.kartlar.forEach { bilgi ->
                IzinKarti(
                    tanim = bilgi.tanim,
                    durum = when {
                        bilgi.durum == IzinDurumu.VERILDI -> IzinKartiDurumu.VERILDI
                        bilgi.tanim.tur == IzinTuru.VARSAYILAN_UYGULAMA -> IzinKartiDurumu.ROL_YOK
                        bilgi.durum == IzinDurumu.KALICI_REDDEDILDI -> IzinKartiDurumu.AYARLARDAN_AC
                        else -> IzinKartiDurumu.ISTENMEDI
                    },
                    onIzinIste = { onIzinIste(bilgi.tanim.tur) },
                )
            }
            Spacer(Modifier.height(EymenBosluk.xl))
        }
        Spacer(Modifier.height(EymenBosluk.m))
        EymenButon(
            metin = stringResource(if (ayarModu) R.string.izin_tamam else R.string.izin_devam),
            onClick = onDevam,
            etkin = ayarModu || state.zorunlularTamam,
        )
        Box(
            Modifier.fillMaxWidth().height(EymenBoyut.anaButon).clickable { nedenAcik = true },
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.izin_neden), style = EymenYazi.govde, color = r.vurgu)
        }
    }

    if (nedenAcik) {
        ModalBottomSheet(
            onDismissRequest = { nedenAcik = false },
            containerColor = r.yuzey1,
            contentColor = r.metin1,
            shape = EymenSekil.altSayfa,
        ) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = EymenBosluk.kenar)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(EymenBosluk.m),
            ) {
                Text(stringResource(R.string.izin_neden), style = EymenYazi.baslik2)
                IzinListesi.gecerli().forEach { tanim ->
                    Text(stringResource(tanim.baslik), style = EymenYazi.isim)
                    Text(stringResource(tanim.neden), style = EymenYazi.govde, color = r.metin2)
                }
                Spacer(Modifier.height(EymenBosluk.xxl))
            }
        }
    }
}

@Preview(name = "Koyu", showBackground = true)
@Composable private fun IzinlerKoyu() {
    val kartlar = IzinListesi.gecerli().mapIndexed { index, t ->
        IzinKartiBilgisi(t, if (index in 1..3) IzinDurumu.VERILDI else IzinDurumu.ISTENMEDI)
    }
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        IzinlerIcerik(IzinlerUiState(kartlar, 3, kartlar.size, false), false, {}, {}, {})
    }
}

@Preview(name = "Acik", showBackground = true)
@Composable private fun IzinlerAcik() {
    val kartlar = IzinListesi.gecerli().map { IzinKartiBilgisi(it, IzinDurumu.VERILDI) }
    EymenTelefonTemasi(tema = TemaSecimi.ACIK) {
        IzinlerIcerik(IzinlerUiState(kartlar, kartlar.size, kartlar.size, true), true, {}, {}, {})
    }
}
