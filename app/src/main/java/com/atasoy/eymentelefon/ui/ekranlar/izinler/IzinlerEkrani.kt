package com.atasoy.eymentelefon.ui.ekranlar.izinler

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.cekirdek.izin.IzinDurumu
import com.atasoy.eymentelefon.cekirdek.izin.IzinTuru
import com.atasoy.eymentelefon.ui.bilesenler.IkonKaresi
import com.atasoy.eymentelefon.ui.bilesenler.IosBuyukBaslik
import com.atasoy.eymentelefon.ui.bilesenler.IosButon
import com.atasoy.eymentelefon.ui.bilesenler.IosGeriButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosGrup
import com.atasoy.eymentelefon.ui.bilesenler.IosKapsulButon
import com.atasoy.eymentelefon.ui.bilesenler.IosNavCubugu
import com.atasoy.eymentelefon.ui.bilesenler.IosSayfa
import com.atasoy.eymentelefon.ui.bilesenler.IosSatir
import com.atasoy.eymentelefon.ui.bilesenler.IosUyari
import com.atasoy.eymentelefon.ui.bilesenler.UyariButonu
import com.atasoy.eymentelefon.ui.bilesenler.sessizTiklama
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.ui.theme.KareRenk

/** 02 · İzinler — açılıştan önce ve Ayarlar › İzinler'den (ayar modu). */
@Composable
fun IzinlerEkrani(
    ayarModu: Boolean,
    onDevam: () -> Unit,
    onGeri: () -> Unit,
    vm: IzinlerViewModel = viewModel(factory = IzinlerViewModel.Fabrika),
) {
    val context = LocalContext.current
    val activity = remember(context) { context.bulActivity() }
    val durum by vm.durum.collectAsStateWithLifecycle()
    val rolIstegi = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { vm.yenile(activity) }
    val izinIstegi = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { vm.yenile(activity) }
    LaunchedEffect(Unit) { vm.yenile(activity) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.yenile(activity) }

    val izinIste: (IzinKartDurumu) -> Unit = { kart ->
        when {
            kart.durum == IzinDurumu.VERILDI -> Unit
            kart.tanim.tur == IzinTuru.VARSAYILAN_UYGULAMA -> vm.rolIntenti()?.let { rolIstegi.launch(it) }
            kart.durum == IzinDurumu.KALICI_REDDEDILDI -> try {
                context.startActivity(vm.ayarlarIntenti())
            } catch (e: ActivityNotFoundException) {
                HataKaydedici.kaydet("IzinlerEkrani.ayarlar", e)
            }
            else -> vm.eksikler(kart.tanim).takeIf { it.isNotEmpty() }?.let {
                vm.istendi(it)
                izinIstegi.launch(it)
            }
        }
    }

    val r = EymenRenk
    val zorunlu = durum.kartlar.filter { it.tanim.zorunlu }
    val istege = durum.kartlar.filterNot { it.tanim.zorunlu }

    IosSayfa(zemin = r.grupluArkaPlan) {
        IosNavCubugu(zemin = r.grupluArkaPlan, sol = { if (ayarModu) IosGeriButonu(stringResource(R.string.ayarlar), onGeri) })
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            IosBuyukBaslik(stringResource(R.string.izinler))
            Text(
                stringResource(R.string.izin_aciklama),
                style = EymenYazi.dipnot,
                color = r.ikincilEtiket,
                modifier = Modifier.padding(horizontal = EymenBosluk.kenar),
            )
            IosGrup(baslik = stringResource(R.string.izin_zorunlu_baslik, zorunlu.count { it.durum == IzinDurumu.VERILDI }, zorunlu.size)) {
                zorunlu.forEachIndexed { i, kart -> IzinSatiri(kart, i < zorunlu.lastIndex) { izinIste(kart) } }
            }
            if (istege.isNotEmpty()) {
                IosGrup(baslik = stringResource(R.string.izin_istege_bagli), dipnot = stringResource(R.string.izin_dipnot)) {
                    istege.forEachIndexed { i, kart -> IzinSatiri(kart, i < istege.lastIndex) { izinIste(kart) } }
                }
            }
            Text(
                stringResource(R.string.izin_neden),
                style = EymenYazi.altBaslik,
                color = r.vurgu,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(EymenBosluk.l).sessizTiklama { vm.nedenGoster(true) },
            )
        }
        IosButon(
            stringResource(if (ayarModu) R.string.tamam else R.string.devam),
            onDevam,
            Modifier.padding(EymenBosluk.kenar),
            etkin = durum.devamAktif || ayarModu,
        )
    }

    if (durum.nedenAcik) {
        IosUyari(
            baslik = stringResource(R.string.izin_neden),
            mesaj = durum.kartlar.joinToString("\n\n") { context.getString(it.tanim.baslik) + ": " + context.getString(it.tanim.neden) },
            butonlar = listOf(UyariButonu(stringResource(R.string.tamam), { vm.nedenGoster(false) }, kalin = true)),
            onKapat = { vm.nedenGoster(false) },
        )
    }
}

@Composable
private fun IzinSatiri(kart: IzinKartDurumu, ayirici: Boolean, onIste: () -> Unit) {
    val renk = when (kart.tanim.tur) {
        IzinTuru.VARSAYILAN_UYGULAMA, IzinTuru.TELEFON -> KareRenk.yesil
        IzinTuru.REHBER -> KareRenk.gri
        IzinTuru.ARAMA_GECMISI -> KareRenk.mavi
        IzinTuru.BILDIRIMLER -> KareRenk.kirmizi
    }
    IosSatir(
        baslik = stringResource(kart.tanim.baslik),
        altBaslik = stringResource(kart.tanim.aciklama),
        ayirici = ayirici,
        sol = { IkonKaresi(kart.tanim.ikon, renk) },
        onTikla = onIste,
        sag = {
            when {
                kart.durum == IzinDurumu.VERILDI -> EymenIkonu(EymenIkon.Onay, stringResource(R.string.izin_verildi), EymenRenk.yesil, boyut = EymenBoyut.ikon)
                kart.tanim.tur == IzinTuru.VARSAYILAN_UYGULAMA -> IosKapsulButon(stringResource(R.string.izin_ayarla), onIste)
                kart.durum == IzinDurumu.KALICI_REDDEDILDI -> IosKapsulButon(stringResource(R.string.izin_ayarlardan_ac), onIste)
                else -> IosKapsulButon(stringResource(R.string.izin_ver), onIste)
            }
        },
    )
}

private fun Context.bulActivity(): Activity {
    var c: Context = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    error("Activity bulunamadı")
}
