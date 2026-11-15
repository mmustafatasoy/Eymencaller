package com.atasoy.eymentelefon.ui.ekranlar.kisiduzenle

// V12-10 · Uygulama içinde Yeni Kişi / Düzenle (docs/07_V12_PLANI.md, görsel v12_08)

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.yardimci.TurkceMetin
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.Ayirici
import com.atasoy.eymentelefon.ui.bilesenler.IosAvatar
import com.atasoy.eymentelefon.ui.bilesenler.IosEylemSatiri
import com.atasoy.eymentelefon.ui.bilesenler.IosEylemSayfasi
import com.atasoy.eymentelefon.ui.bilesenler.IosGrup
import com.atasoy.eymentelefon.ui.bilesenler.IosMetinButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosNavCubugu
import com.atasoy.eymentelefon.ui.bilesenler.IosSayfa
import com.atasoy.eymentelefon.ui.bilesenler.IosUyari
import com.atasoy.eymentelefon.ui.bilesenler.SayfaSecenegi
import com.atasoy.eymentelefon.ui.bilesenler.UyariButonu
import com.atasoy.eymentelefon.ui.bilesenler.sessizTiklama
import com.atasoy.eymentelefon.ui.ekranlar.numaraTuruMetni
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.ikon.EymenIkonu
import com.atasoy.eymentelefon.ui.navigasyon.Rotalar
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenYazi
import com.atasoy.eymentelefon.veri.model.Numara
import com.atasoy.eymentelefon.veri.model.NumaraTuru
import com.atasoy.eymentelefon.veri.rehber.FotoDegisimi
import com.atasoy.eymentelefon.veri.rehber.KisiTaslak
import com.atasoy.eymentelefon.veri.rehber.KisiYazici
import com.atasoy.eymentelefon.veri.rehber.TaslakNumara
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KisiDuzenleUiState(
    val yukleniyor: Boolean = true,
    val taslak: KisiTaslak = KisiTaslak(),
    val foto: FotoDegisimi = FotoDegisimi.Yok,
    val kaydediliyor: Boolean = false,
    val hata: Boolean = false,
    /** Kaydedildi → kişi kimliği */
    val kaydedilen: Long? = null,
) {
    val yeni: Boolean get() = taslak.kisiId <= 0
    val gosterilenFoto: Uri? get() = when (val f = foto) {
        is FotoDegisimi.Yeni -> f.uri
        FotoDegisimi.Kaldir -> null
        FotoDegisimi.Yok -> taslak.fotoUri
    }
}

class KisiDuzenleViewModel(kayit: SavedStateHandle, private val yazici: KisiYazici) : ViewModel() {
    private val kisiId: Long = kayit[Rotalar.Arguman.KISI_ID] ?: -1L
    private val ilkNumara: String = kayit[Rotalar.Arguman.NUMARA] ?: ""
    private val _durum = MutableStateFlow(KisiDuzenleUiState())
    val durum: StateFlow<KisiDuzenleUiState> = _durum.asStateFlow()

    init {
        viewModelScope.launch {
            val t = if (kisiId > 0) yazici.oku(kisiId) else null
            val taslak = t ?: KisiTaslak(telefonlar = if (ilkNumara.isNotBlank()) listOf(TaslakNumara(ilkNumara)) else emptyList())
            _durum.update { it.copy(yukleniyor = false, taslak = taslak) }
        }
    }

    private fun taslak(blok: (KisiTaslak) -> KisiTaslak) = _durum.update { it.copy(taslak = blok(it.taslak)) }

    fun ad(v: String) = taslak { it.copy(ad = v) }
    fun soyad(v: String) = taslak { it.copy(soyad = v) }
    fun sirket(v: String) = taslak { it.copy(sirket = v) }
    fun telefonEkle() = taslak { it.copy(telefonlar = it.telefonlar + TaslakNumara(tur = if (it.telefonlar.isEmpty()) NumaraTuru.MOBIL else NumaraTuru.DIGER)) }
    fun telefon(i: Int, v: String) = taslak { t -> t.copy(telefonlar = t.telefonlar.mapIndexed { j, n -> if (j == i) n.copy(numara = v) else n }) }
    fun telefonTuru(i: Int, tur: NumaraTuru) = taslak { t -> t.copy(telefonlar = t.telefonlar.mapIndexed { j, n -> if (j == i) n.copy(tur = tur) else n }) }
    fun telefonSil(i: Int) = taslak { t -> t.copy(telefonlar = t.telefonlar.filterIndexed { j, _ -> j != i }) }
    fun epostaEkle() = taslak { it.copy(epostalar = it.epostalar + "") }
    fun eposta(i: Int, v: String) = taslak { t -> t.copy(epostalar = t.epostalar.mapIndexed { j, e -> if (j == i) v else e }) }
    fun epostaSil(i: Int) = taslak { t -> t.copy(epostalar = t.epostalar.filterIndexed { j, _ -> j != i }) }
    fun fotoSec(uri: Uri) = _durum.update { it.copy(foto = FotoDegisimi.Yeni(uri)) }
    fun fotoKaldir() = _durum.update { it.copy(foto = FotoDegisimi.Kaldir) }
    fun hataKapat() = _durum.update { it.copy(hata = false) }

    fun kaydet() {
        val d = _durum.value
        if (d.kaydediliyor || d.taslak.bosMu) return
        _durum.update { it.copy(kaydediliyor = true) }
        viewModelScope.launch {
            val id = yazici.kaydet(d.taslak, d.foto)
            _durum.update { it.copy(kaydediliyor = false, kaydedilen = id, hata = id == null) }
        }
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer { KisiDuzenleViewModel(createSavedStateHandle(), konteyner().kisiYazici) }
        }
    }
}

/**
 * @param onBitti kaydedilen kişinin kimliği ve yeni mi
 * @param onPoster (lookupKey, ad) — sadece kayıtlı kişide
 */
@Composable
fun KisiDuzenleEkrani(
    onVazgec: () -> Unit,
    onBitti: (Long, Boolean) -> Unit,
    onPoster: (String, String) -> Unit,
    vm: KisiDuzenleViewModel = viewModel(factory = KisiDuzenleViewModel.Fabrika),
) {
    val d by vm.durum.collectAsStateWithLifecycle()
    val r = EymenRenk
    val t = d.taslak
    var fotoMenu by rememberSaveable { mutableStateOf(false) }
    var turMenu by rememberSaveable { mutableStateOf(-1) }
    val secici = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> uri?.let(vm::fotoSec) }
    val fotoSec = { secici.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    LaunchedEffect(d.kaydedilen) { d.kaydedilen?.let { onBitti(it, d.yeni) } }

    val tamAd = listOf(t.ad, t.soyad).filter { it.isNotBlank() }.joinToString(" ")

    IosSayfa(zemin = r.grupluArkaPlan) {
        IosNavCubugu(
            zemin = r.grupluArkaPlan,
            kucukBaslik = if (d.yeni) stringResource(R.string.kd_yeni) else null,
            baslikGorunur = d.yeni,
            sol = { IosMetinButonu(stringResource(R.string.vazgec), onVazgec) },
            sag = { IosMetinButonu(stringResource(R.string.bitti), vm::kaydet, kalin = true, etkin = !t.bosMu && !d.kaydediliyor) },
        )
        if (!d.yukleniyor) Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            // Fotoğraf
            Column(Modifier.fillMaxWidth().padding(vertical = EymenBosluk.m), horizontalAlignment = Alignment.CenterHorizontally) {
                IosAvatar(TurkceMetin.basHarfler(tamAd).ifBlank { "#" }, EymenBoyut.avatarKart, fotoUri = d.gosterilenFoto)
                IosMetinButonu(
                    stringResource(if (d.gosterilenFoto == null) R.string.kd_foto_ekle else R.string.duzenle),
                    { if (d.gosterilenFoto == null) fotoSec() else fotoMenu = true },
                    Modifier.padding(top = EymenBosluk.s),
                )
            }

            IosGrup {
                Alan(t.ad, vm::ad, stringResource(R.string.kd_ad), ayirici = true, buyukHarf = true)
                Alan(t.soyad, vm::soyad, stringResource(R.string.kd_soyad), ayirici = true, buyukHarf = true)
                Alan(t.sirket, vm::sirket, stringResource(R.string.kd_sirket), ayirici = false, buyukHarf = true)
            }

            IosGrup {
                t.telefonlar.forEachIndexed { i, n ->
                    SilinebilirSatir(
                        etiket = numaraTuruMetni(Numara(n.numara, n.tur)),
                        onEtiket = { turMenu = i },
                        onSil = { vm.telefonSil(i) },
                    ) {
                        Alan(n.numara, { vm.telefon(i, it) }, stringResource(R.string.kd_telefon), ayirici = false, klavye = KeyboardType.Phone)
                    }
                    Ayirici()
                }
                EkleSatiri(stringResource(R.string.kd_telefon_ekle), vm::telefonEkle)
            }

            IosGrup {
                t.epostalar.forEachIndexed { i, e ->
                    SilinebilirSatir(etiket = stringResource(R.string.kd_eposta_etiket), onEtiket = null, onSil = { vm.epostaSil(i) }) {
                        Alan(e, { vm.eposta(i, it) }, stringResource(R.string.kd_eposta), ayirici = false, klavye = KeyboardType.Email)
                    }
                    Ayirici()
                }
                EkleSatiri(stringResource(R.string.kd_eposta_ekle), vm::epostaEkle)
            }

            val anahtar = t.lookupKey
            if (!d.yeni && anahtar != null) {
                IosGrup {
                    IosEylemSatiri(stringResource(R.string.kart_poster), { onPoster(anahtar, tamAd) }, ayirici = false)
                }
            }
            Spacer(Modifier.height(EymenBosluk.xxl))
        }
    }

    if (fotoMenu) {
        IosEylemSayfasi(
            baslik = null,
            secenekler = listOf(
                SayfaSecenegi(stringResource(R.string.poster_foto_sec), { fotoMenu = false; fotoSec() }),
                SayfaSecenegi(stringResource(R.string.poster_foto_kaldir), { fotoMenu = false; vm.fotoKaldir() }, yikici = true),
            ),
            vazgecMetni = stringResource(R.string.vazgec),
            onKapat = { fotoMenu = false },
        )
    }
    if (turMenu >= 0) {
        val i = turMenu
        IosEylemSayfasi(
            baslik = null,
            secenekler = NumaraTuru.entries.map { tur ->
                SayfaSecenegi(numaraTuruMetni(Numara("", tur)), { vm.telefonTuru(i, tur); turMenu = -1 })
            },
            vazgecMetni = stringResource(R.string.vazgec),
            onKapat = { turMenu = -1 },
        )
    }
    if (d.hata) {
        IosUyari(
            baslik = stringResource(R.string.kd_kaydedilemedi),
            mesaj = null,
            butonlar = listOf(UyariButonu(stringResource(R.string.tamam), vm::hataKapat, kalin = true)),
            onKapat = vm::hataKapat,
        )
    }
}

@Composable
private fun Alan(
    deger: String,
    onDegis: (String) -> Unit,
    yerTutucu: String,
    ayirici: Boolean,
    klavye: KeyboardType = KeyboardType.Text,
    buyukHarf: Boolean = false,
) {
    val r = EymenRenk
    Box(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = EymenBoyut.satir)
            .padding(start = EymenBosluk.kenar, end = EymenBosluk.kenar),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (deger.isEmpty()) Text(yerTutucu, style = EymenYazi.govde, color = r.ucunculEtiket, maxLines = 1)
        BasicTextField(
            value = deger,
            onValueChange = onDegis,
            singleLine = true,
            textStyle = EymenYazi.govde.copy(color = r.etiket),
            cursorBrush = SolidColor(r.vurgu),
            keyboardOptions = KeyboardOptions(
                keyboardType = klavye,
                capitalization = if (buyukHarf) KeyboardCapitalization.Words else KeyboardCapitalization.None,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    if (ayirici) Box(Modifier.padding(start = EymenBosluk.kenar)) { Ayirici() }
}

/** iOS: kırmızı ⊖  ·  "mobil ›"  ·  alan */
@Composable
private fun SilinebilirSatir(etiket: String, onEtiket: (() -> Unit)?, onSil: () -> Unit, alan: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(start = EymenBosluk.kenar), verticalAlignment = Alignment.CenterVertically) {
        val sil = stringResource(R.string.sil)
        Box(
            Modifier
                .size(EymenBoyut.duzenleIsaret)
                .clip(CircleShape)
                .background(EymenRenk.kirmizi)
                .drawBehind {
                    val y = size.height / 2
                    drawLine(AramaEkraniRenk.beyaz, Offset(size.width * CIZGI_BAS, y), Offset(size.width * (1 - CIZGI_BAS), y), size.height * CIZGI_KALIN)
                }
                .semantics { contentDescription = sil }
                .sessizTiklama(onTikla = onSil),
        )
        Row(
            Modifier
                .padding(start = EymenBosluk.m)
                .then(if (onEtiket != null) Modifier.sessizTiklama(onTikla = onEtiket) else Modifier),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EymenBosluk.xs),
        ) {
            Text(etiket, style = EymenYazi.govde, color = EymenRenk.vurgu, maxLines = 1)
            if (onEtiket != null) EymenIkonu(EymenIkon.Ileri, null, EymenRenk.ucunculEtiket, boyut = EymenBoyut.duzenleIsaret / 2)
        }
        Box(Modifier.weight(1f)) { alan() }
    }
}

@Composable
private fun EkleSatiri(metin: String, onTikla: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = EymenBoyut.satir)
            .sessizTiklama(onTikla = onTikla)
            .padding(start = EymenBosluk.kenar),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(EymenBoyut.duzenleIsaret).clip(CircleShape).background(EymenRenk.yesil),
            contentAlignment = Alignment.Center,
        ) {
            EymenIkonu(EymenIkon.Arti, null, AramaEkraniRenk.beyaz, boyut = EymenBoyut.duzenleIsaret * ARTI_ORANI)
        }
        Text(metin, style = EymenYazi.govde, color = EymenRenk.etiket, modifier = Modifier.padding(start = EymenBosluk.m))
    }
}

private const val CIZGI_BAS = 0.28f
private const val CIZGI_KALIN = 0.12f
private const val ARTI_ORANI = 0.7f
