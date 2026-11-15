package com.atasoy.eymentelefon.ui.ekranlar.poster

// V12-08 · Kişi Posteri düzenleyici (docs/07_V12_PLANI.md, görsel v12_09)
// Fotoğraf seçimi PickVisualMedia ile (izin gerekmez). Değişiklikler "Bitti"de kaydedilir.

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.hata.HataKaydedici
import com.atasoy.eymentelefon.gorusme.ui.CanliAd
import com.atasoy.eymentelefon.gorusme.ui.PosterFotografi
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.IosAnahtar
import com.atasoy.eymentelefon.ui.bilesenler.IosAyarSatiri
import com.atasoy.eymentelefon.ui.bilesenler.IosGrup
import com.atasoy.eymentelefon.ui.bilesenler.IosMetinButonu
import com.atasoy.eymentelefon.ui.bilesenler.IosNavCubugu
import com.atasoy.eymentelefon.ui.bilesenler.IosSayfa
import com.atasoy.eymentelefon.ui.bilesenler.sessizTiklama
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.navigasyon.Rotalar
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenSekil
import com.atasoy.eymentelefon.ui.theme.KareRenk
import com.atasoy.eymentelefon.veri.poster.PosterDeposu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PosterUiState(
    val ad: String = "",
    /** Kayıtlı fotoğraf (dosya yolu) */
    val kayitliFoto: String? = null,
    /** Yeni seçilen, henüz kaydedilmemiş */
    val yeniFoto: Uri? = null,
    val fotoKaldir: Boolean = false,
    val renkIndex: Int? = null,
    val canli: Boolean = true,
    val bitti: Boolean = false,
)

class PosterViewModel(kayit: SavedStateHandle, private val depo: PosterDeposu) : ViewModel() {
    private val anahtar: String = kayit[Rotalar.Arguman.LOOKUP] ?: ""
    private val _durum = MutableStateFlow(PosterUiState(ad = kayit[Rotalar.Arguman.AD] ?: ""))
    val durum: StateFlow<PosterUiState> = _durum.asStateFlow()

    init {
        viewModelScope.launch {
            depo.oku(anahtar)?.let { p ->
                _durum.update { it.copy(kayitliFoto = p.dosya?.path, renkIndex = p.renkIndex, canli = p.canli) }
            }
        }
    }

    fun fotoSec(uri: Uri) = _durum.update { it.copy(yeniFoto = uri, fotoKaldir = false) }
    fun fotoKaldir() = _durum.update { it.copy(yeniFoto = null, fotoKaldir = true) }
    fun renk(i: Int?) = _durum.update { it.copy(renkIndex = i) }
    fun canli(a: Boolean) = _durum.update { it.copy(canli = a) }

    fun kaydet() = viewModelScope.launch {
        val d = _durum.value
        if (anahtar.isBlank()) {
            HataKaydedici.kaydet("PosterViewModel.kaydet", mesaj = "lookupKey boş")
        } else {
            d.yeniFoto?.let { depo.fotoKaydet(anahtar, it) }
            if (d.fotoKaldir) depo.fotoSil(anahtar)
            depo.ayarKaydet(anahtar, d.canli, d.renkIndex)
        }
        _durum.update { it.copy(bitti = true) }
    }

    companion object {
        val Fabrika = viewModelFactory {
            initializer { PosterViewModel(createSavedStateHandle(), konteyner().posterDeposu) }
        }
    }
}

@Composable
fun PosterEkrani(onGeri: () -> Unit, vm: PosterViewModel = viewModel(factory = PosterViewModel.Fabrika)) {
    val d by vm.durum.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(d.bitti) { if (d.bitti) onGeri() }
    val secici = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> uri?.let(vm::fotoSec) }

    // Önizleme fotoğrafı: yeni seçilen > kayıtlı > yok
    val resim by produceState<ImageBitmap?>(null, d.yeniFoto, d.kayitliFoto, d.fotoKaldir) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                when {
                    d.fotoKaldir -> null
                    d.yeniFoto != null -> context.contentResolver.openInputStream(d.yeniFoto!!)?.use {
                        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = ONIZLEME_ORNEK })
                    }?.asImageBitmap()
                    d.kayitliFoto != null -> BitmapFactory.decodeFile(d.kayitliFoto)?.asImageBitmap()
                    else -> null
                }
            }.onFailure { HataKaydedici.kaydet("PosterEkrani.onizleme", it) }.getOrNull()
        }
    }

    IosSayfa(zemin = AramaEkraniRenk.siyah) {
        IosNavCubugu(
            zemin = AramaEkraniRenk.siyah,
            kucukBaslik = stringResource(R.string.poster_baslik),
            baslikGorunur = true,
            sol = { IosMetinButonu(stringResource(R.string.vazgec), onGeri, renk = AramaEkraniRenk.beyaz) },
            sag = { IosMetinButonu(stringResource(R.string.bitti), vm::kaydet, kalin = true, renk = AramaEkraniRenk.beyaz) },
        )
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            // Mini gelen arama önizlemesi
            Box(
                Modifier
                    .padding(vertical = EymenBosluk.l)
                    .size(EymenBoyut.posterOnizlemeEn, EymenBoyut.posterOnizlemeBoy)
                    .clip(EymenSekil.sayfa)
                    .background(paletFircasi(d.renkIndex, d.ad)),
                contentAlignment = Alignment.TopCenter,
            ) {
                resim?.let { PosterFotografi(it, d.canli, bulanik = false) }
                Column(Modifier.padding(top = EymenBosluk.xl), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.numara_mobil), style = com.atasoy.eymentelefon.ui.theme.EymenYazi.etiket1, color = AramaEkraniRenk.ikincil)
                    CanliAd(d.ad, Modifier.padding(horizontal = EymenBosluk.s).fillMaxWidth())
                }
            }

            IosGrup(baslik = stringResource(R.string.poster_foto)) {
                IosAyarSatiri(
                    EymenIkon.KisiDolu, KareRenk.mavi, stringResource(R.string.poster_foto_sec),
                    ayirici = resim != null,
                    onTikla = { secici.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                )
                if (resim != null) {
                    IosAyarSatiri(EymenIkon.Sil, KareRenk.kirmizi, stringResource(R.string.poster_foto_kaldir), ok = false, ayirici = false, onTikla = vm::fotoKaldir)
                }
            }

            IosGrup(baslik = stringResource(R.string.poster_renk)) {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = EymenBosluk.m),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    (0 until AramaEkraniRenk.paletSayisi).forEach { i ->
                        val secili = d.renkIndex == i
                        Box(
                            Modifier
                                .size(EymenBoyut.dokunmaMin)
                                .clip(CircleShape)
                                .background(paletFircasi(i, d.ad))
                                .then(if (secili) Modifier.border(EymenBoyut.cerceve, EymenRenk.vurgu, CircleShape) else Modifier)
                                .sessizTiklama { vm.renk(if (secili) null else i) },
                        )
                    }
                }
            }

            IosGrup {
                IosAyarSatiri(EymenIkon.Palet, KareRenk.mor, stringResource(R.string.poster_canli), ok = false, ayirici = false) {
                    IosAnahtar(d.canli, vm::canli)
                }
            }
            Spacer(Modifier.height(EymenBosluk.xxl))
        }
    }
}

private fun paletFircasi(i: Int?, ad: String): Brush {
    val renkler = i?.let(AramaEkraniRenk::paletSirayla) ?: AramaEkraniRenk.palet(ad)
    return Brush.verticalGradient(renkler)
}

private const val ONIZLEME_ORNEK = 4
