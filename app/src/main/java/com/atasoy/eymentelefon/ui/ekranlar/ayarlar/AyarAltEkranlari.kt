package com.atasoy.eymentelefon.ui.ekranlar.ayarlar

// Ayarlar alt ekranları: Hata Kayıtları · Engellenen Numaralar · Hızlı Yanıtlar

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.yardimci.DisIslemler
import com.atasoy.eymentelefon.cekirdek.yardimci.NumaraBicimleyici
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.IosBuyukBaslik
import com.atasoy.eymentelefon.ui.bilesenler.IosEylemSatiri
import com.atasoy.eymentelefon.ui.bilesenler.IosGeriButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosGrup
import com.atasoy.eymentelefon.ui.bilesenler.IosMetinButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosNavCubugu
import com.atasoy.eymentelefon.ui.bilesenler.IosSayfa
import com.atasoy.eymentelefon.ui.bilesenler.IosSatir
import com.atasoy.eymentelefon.ui.bilesenler.IosUyari
import com.atasoy.eymentelefon.ui.bilesenler.UyariButonu
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import kotlinx.coroutines.launch

@Composable
fun HataKayitlariEkrani(onGeri: () -> Unit, vm: AyarlarViewModel = viewModel(factory = AyarlarViewModel.Fabrika)) {
    val r = EymenRenk
    val context = LocalContext.current
    val durum by vm.durum.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.yenile() }
    val kayitVar = durum.hataMetni.contains("=== ")
    IosSayfa(zemin = r.grupluArkaPlan) {
        IosNavCubugu(
            zemin = r.grupluArkaPlan,
            sol = { IosGeriButonu(stringResource(R.string.ayarlar), onGeri) },
            sag = { IosMetinButonu(stringResource(R.string.hata_paylas), { DisIslemler.metinPaylas(context, durum.hataMetni) }, etkin = kayitVar) },
        )
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            IosBuyukBaslik(stringResource(R.string.ayar_hata_kayitlari))
            IosGrup {
                if (kayitVar) {
                    Text(
                        durum.hataMetni,
                        style = EymenYazi.etiket2.copy(fontFamily = FontFamily.Monospace),
                        color = r.etiket,
                        modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = EymenBosluk.m),
                    )
                } else {
                    IosSatir(stringResource(R.string.hata_yok), baslikRengi = r.ikincilEtiket, ayirici = false)
                }
            }
            if (kayitVar) IosGrup { IosEylemSatiri(stringResource(R.string.hata_temizle), vm::hatalariTemizle, yikici = true, ayirici = false) }
        }
    }
}

@Composable
fun EngellenenlerEkrani(onGeri: () -> Unit) {
    val r = EymenRenk
    val context = LocalContext.current
    val depo = remember { context.konteyner().engelDeposu }
    val akis = remember { depo.engelliler() }
    val liste by akis.collectAsStateWithLifecycle(initialValue = emptyList())
    val kapsam = rememberCoroutineScope()
    var ekleAcik by remember { mutableStateOf(false) }

    IosSayfa(zemin = r.grupluArkaPlan) {
        IosNavCubugu(zemin = r.grupluArkaPlan, sol = { IosGeriButonu(stringResource(R.string.ayarlar), onGeri) })
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            IosBuyukBaslik(stringResource(R.string.ayar_engellenenler))
            IosGrup(dipnot = stringResource(R.string.engel_dipnot)) {
                liste.forEach { n ->
                    IosSatir(
                        baslik = NumaraBicimleyici.bicimle(n),
                        sag = { IosMetinButonu(stringResource(R.string.engel_kaldir), { kapsam.launch { depo.engeliKaldir(n) } }, renk = r.kirmizi) },
                    )
                }
                IosEylemSatiri(stringResource(R.string.engel_ekle), { ekleAcik = true }, ayirici = false)
            }
        }
    }

    if (ekleAcik) {
        var numara by remember { mutableStateOf("") }
        IosUyari(
            baslik = stringResource(R.string.engel_ekle_baslik),
            mesaj = null,
            butonlar = listOf(
                UyariButonu(stringResource(R.string.vazgec), { ekleAcik = false }),
                UyariButonu(stringResource(R.string.engelle), {
                    val n = numara.trim()
                    if (n.isNotEmpty()) kapsam.launch { depo.engelle(n) }
                    ekleAcik = false
                }, kalin = true, yikici = true),
            ),
            onKapat = { ekleAcik = false },
        ) {
            BasicTextField(
                value = numara,
                onValueChange = { numara = it },
                singleLine = true,
                textStyle = EymenYazi.govde.copy(color = r.etiket),
                cursorBrush = SolidColor(r.vurgu),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                decorationBox = { alan ->
                    if (numara.isEmpty()) Text(stringResource(R.string.engel_ipucu), style = EymenYazi.govde, color = r.ikincilEtiket)
                    alan()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(EymenSekil.kare)
                    .background(r.grupluArkaPlan)
                    .padding(EymenBosluk.s),
            )
        }
    }
}

@Composable
fun HizliYanitlarEkrani(onGeri: () -> Unit, vm: AyarlarViewModel = viewModel(factory = AyarlarViewModel.Fabrika)) {
    val r = EymenRenk
    val durum by vm.durum.collectAsStateWithLifecycle()
    val mesajlar = remember { mutableStateListOf<String>() }
    LaunchedEffect(durum.ayarlar.hizliYanitlar) { if (mesajlar.isEmpty()) mesajlar.addAll(vm.hizliYanitlar()) }
    IosSayfa(zemin = r.grupluArkaPlan) {
        IosNavCubugu(zemin = r.grupluArkaPlan, sol = { IosGeriButonu(stringResource(R.string.ayarlar), onGeri) })
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            IosBuyukBaslik(stringResource(R.string.ayar_hizli_yanit))
            IosGrup(dipnot = stringResource(R.string.ayar_hizli_dipnot)) {
                mesajlar.forEachIndexed { i, m ->
                    BasicTextField(
                        value = m,
                        onValueChange = { yeni ->
                            mesajlar[i] = yeni
                            vm.hizliYanitDegisti(mesajlar.toList())
                        },
                        textStyle = EymenYazi.govde.copy(color = r.etiket),
                        cursorBrush = SolidColor(r.vurgu),
                        modifier = Modifier.fillMaxWidth().padding(top = EymenBosluk.m, bottom = EymenBosluk.m, end = EymenBosluk.kenar),
                    )
                    if (i < mesajlar.lastIndex) com.atasoy.eymentelefon.ui.bilesenler.Ayirici()
                }
            }
        }
    }
}
