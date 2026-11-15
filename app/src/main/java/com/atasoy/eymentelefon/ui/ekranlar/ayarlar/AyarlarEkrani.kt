package com.atasoy.eymentelefon.ui.ekranlar.ayarlar

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.BuildConfig
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.yardimci.DisIslemler
import com.atasoy.eymentelefon.ui.bilesenler.IosAnahtar
import com.atasoy.eymentelefon.ui.bilesenler.IosAyarSatiri
import com.atasoy.eymentelefon.ui.bilesenler.IosBuyukBaslik
import com.atasoy.eymentelefon.ui.bilesenler.IosGeriButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosGrup
import com.atasoy.eymentelefon.ui.bilesenler.IosNavCubugu
import com.atasoy.eymentelefon.ui.bilesenler.IosSayfa
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.KareRenk

/** 18 · Ayarlar (iOS Ayarlar uygulaması düzeni). */
@Composable
fun AyarlarEkrani(
    onGeri: () -> Unit,
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

    IosSayfa(zemin = r.grupluArkaPlan) {
        IosNavCubugu(zemin = r.grupluArkaPlan, sol = { IosGeriButonu(stringResource(R.string.geri), onGeri) })
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            IosBuyukBaslik(stringResource(R.string.ayarlar))

            // V12-03: sade iOS "Telefon" ayarları — İzinler/Tema/Vurgu kaldırıldı (tema sistemden, izinler girişte)
            IosGrup(baslik = stringResource(R.string.ayar_aramalar)) {
                IosAyarSatiri(
                    EymenIkon.Cihaz, KareRenk.yesil, stringResource(R.string.ayar_varsayilan),
                    deger = stringResource(if (durum.varsayilanUygulama) R.string.uygulama_adi else R.string.ayar_varsayilan_degil),
                    onTikla = {
                        val intent = vm.rolIntenti()
                        if (!durum.varsayilanUygulama && intent != null) rolIstegi.launch(intent)
                        else DisIslemler.baslat(context, "varsayilanUygulamalar") { Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS) }
                    },
                )
                IosAyarSatiri(EymenIkon.MesajDolu, KareRenk.mavi, stringResource(R.string.ayar_hizli_yanit), ayirici = false, onTikla = onHizliYanitlar)
            }

            IosGrup(baslik = stringResource(R.string.ayar_ses_titresim)) {
                IosAyarSatiri(EymenIkon.Titresim, KareRenk.pembe, stringResource(R.string.ayar_titresim), ok = false) {
                    IosAnahtar(a.tusTitresimi, { vm.titresim(it) })
                }
                IosAyarSatiri(EymenIkon.Hoparlor, KareRenk.turuncu, stringResource(R.string.ayar_tus_sesi), ok = false, ayirici = false) {
                    IosAnahtar(a.tusSesi, { vm.tusSesi(it) })
                }
            }

            IosGrup(baslik = stringResource(R.string.ayar_engelleme), dipnot = stringResource(R.string.ayar_bilinmeyen_dipnot)) {
                IosAyarSatiri(EymenIkon.Engelle, KareRenk.kirmizi, stringResource(R.string.ayar_engellenenler), deger = durum.engelliSayisi.toString(), onTikla = onEngellenenler)
                IosAyarSatiri(EymenIkon.Bildirim, KareRenk.mor, stringResource(R.string.ayar_bilinmeyen), ok = false, ayirici = false) {
                    IosAnahtar(a.bilinmeyenSustur, { vm.bilinmeyenSustur(it) })
                }
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

