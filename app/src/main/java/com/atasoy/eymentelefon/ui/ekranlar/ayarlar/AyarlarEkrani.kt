package com.atasoy.eymentelefon.ui.ekranlar.ayarlar

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.BuildConfig
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.cekirdek.yardimci.DisIslemler
import com.atasoy.eymentelefon.ui.bilesenler.IosAnahtar
import com.atasoy.eymentelefon.ui.bilesenler.IosAyarSatiri
import com.atasoy.eymentelefon.ui.bilesenler.IosBuyukBaslik
import com.atasoy.eymentelefon.ui.bilesenler.IosGeriButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosGrup
import com.atasoy.eymentelefon.ui.bilesenler.IosNavCubugu
import com.atasoy.eymentelefon.ui.bilesenler.IosSayfa
import com.atasoy.eymentelefon.ui.bilesenler.IosSegment
import com.atasoy.eymentelefon.ui.bilesenler.sessizTiklama
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.KareRenk
import com.atasoy.eymentelefon.ui.theme.VurguSecenekleri

/** 18 · Ayarlar (iOS Ayarlar uygulaması düzeni). */
@Composable
fun AyarlarEkrani(
    onGeri: () -> Unit,
    onIzinler: () -> Unit,
    onHataKayitlari: () -> Unit,
    onEngellenenler: () -> Unit,
    onHizliYanitlar: () -> Unit,
    vm: AyarlarViewModel = viewModel(factory = AyarlarViewModel.Fabrika),
) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val r = EymenRenk
    val a = durum.ayarlar
    val rolIstegi = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { vm.yenile() }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.yenile() }
    val temalar = listOf(TemaSecimi.SISTEM, TemaSecimi.ACIK, TemaSecimi.KOYU)

    IosSayfa(zemin = r.grupluArkaPlan) {
        IosNavCubugu(zemin = r.grupluArkaPlan, sol = { IosGeriButonu(stringResource(R.string.geri), onGeri) })
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            IosBuyukBaslik(stringResource(R.string.ayarlar))

            IosGrup(baslik = stringResource(R.string.ayar_gorunum)) {
                IosAyarSatiri(EymenIkon.Tema, KareRenk.mor, stringResource(R.string.ayar_tema), ok = false) {
                    IosSegment(
                        listOf(stringResource(R.string.tema_sistem), stringResource(R.string.tema_acik), stringResource(R.string.tema_koyu)),
                        temalar.indexOf(a.tema),
                        { vm.temaSec(temalar[it]) },
                        kucuk = true,
                    )
                }
                IosAyarSatiri(EymenIkon.Palet, KareRenk.turuncu, stringResource(R.string.ayar_vurgu), ok = false, ayirici = false) {
                    Row(horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs + EymenBosluk.cokKucuk)) {
                        VurguSecenekleri.forEachIndexed { i, (acik, koyu) ->
                            val secili = i == a.vurguIndex
                            Box(
                                Modifier
                                    .size(EymenBoyut.ikon)
                                    .then(if (secili) Modifier.border(EymenBosluk.cokKucuk, if (r.koyuMu) koyu else acik, CircleShape) else Modifier)
                                    .padding(if (secili) EymenBosluk.xs else EymenBosluk.cokKucuk / 2)
                                    .clip(CircleShape)
                                    .background(if (r.koyuMu) koyu else acik)
                                    .sessizTiklama { vm.vurguSec(i) },
                            )
                        }
                    }
                }
            }

            IosGrup(baslik = stringResource(R.string.ayar_izinler_guvenlik)) {
                IosAyarSatiri(EymenIkon.Kalkan, KareRenk.yesil, stringResource(R.string.izinler), deger = "${durum.verilenIzin} / ${durum.toplamIzin}", onTikla = onIzinler)
                IosAyarSatiri(
                    EymenIkon.Cihaz, KareRenk.yesil, stringResource(R.string.ayar_varsayilan),
                    deger = stringResource(if (durum.varsayilanUygulama) R.string.uygulama_adi else R.string.ayar_varsayilan_degil),
                    onTikla = {
                        val intent = vm.rolIntenti()
                        if (!durum.varsayilanUygulama && intent != null) rolIstegi.launch(intent)
                        else DisIslemler.baslat(context, "varsayilanUygulamalar") { Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS) }
                    },
                )
                IosAyarSatiri(EymenIkon.Engelle, KareRenk.kirmizi, stringResource(R.string.ayar_engellenenler), deger = durum.engelliSayisi.toString(), ayirici = false, onTikla = onEngellenenler)
            }

            IosGrup(baslik = stringResource(R.string.ayar_aramalar)) {
                IosAyarSatiri(EymenIkon.Titresim, KareRenk.pembe, stringResource(R.string.ayar_titresim), ok = false) {
                    IosAnahtar(a.tusTitresimi, { vm.titresim(it) })
                }
                IosAyarSatiri(EymenIkon.Hoparlor, KareRenk.turuncu, stringResource(R.string.ayar_tus_sesi), ok = false) {
                    IosAnahtar(a.tusSesi, { vm.tusSesi(it) })
                }
                IosAyarSatiri(
                    EymenIkon.MesajDolu, KareRenk.mavi, stringResource(R.string.ayar_hizli_yanit),
                    deger = (a.hizliYanitlar?.size ?: VARSAYILAN_YANIT).toString(), ayirici = false, onTikla = onHizliYanitlar,
                )
            }

            IosGrup(baslik = stringResource(R.string.ayar_destek)) {
                IosAyarSatiri(EymenIkon.Dosya, KareRenk.gri, stringResource(R.string.ayar_hata_kayitlari), deger = durum.hataSayisi.toString(), onTikla = onHataKayitlari)
                IosAyarSatiri(
                    EymenIkon.Bilgi, KareRenk.gri, stringResource(R.string.ayar_hakkinda),
                    deger = stringResource(R.string.ayar_surum, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE), ok = false, ayirici = false,
                )
            }
            Spacer(Modifier.height(EymenBosluk.xxl))
        }
    }
}

private const val VARSAYILAN_YANIT = 4
