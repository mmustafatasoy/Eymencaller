package com.atasoy.eymentelefon.ui.ekranlar.anaekran

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.bilesenler.IosSayfa
import com.atasoy.eymentelefon.ui.bilesenler.IosSekmeCubugu
import com.atasoy.eymentelefon.ui.ekranlar.favoriler.FavorilerEkrani
import com.atasoy.eymentelefon.ui.ekranlar.rehber.RehberEkrani
import com.atasoy.eymentelefon.ui.ekranlar.sonaramalar.SonAramalarEkrani
import com.atasoy.eymentelefon.ui.ekranlar.tuslar.TusTakimiEkrani
import com.atasoy.eymentelefon.ui.navigasyon.AnaSekme

/** Ana iskelet: üstte sekme içeriği (güvenli alan içinde), altta iOS sekme çubuğu (zemin gezinme tuşlarının arkasına uzanır). */
@Composable
fun AnaEkran(
    baslangicSekme: AnaSekme,
    baslangicNumara: String,
    onKisiDetay: (kisiId: Long, numara: String?) -> Unit,
    onArama: () -> Unit,
    onAyarlar: () -> Unit,
) {
    val context = LocalContext.current
    val k = remember { context.konteyner() }
    var sekme by rememberSaveable { mutableStateOf(baslangicSekme) }
    LaunchedEffect(sekme) { k.ayarDeposu.sonSekme(sekme.name) }

    IosSayfa(altDahil = false) {
        Box(Modifier.weight(1f)) {
            when (sekme) {
                AnaSekme.FAVORILER -> FavorilerEkrani(onKisiDetay, onAyarlar, onKisiler = { sekme = AnaSekme.REHBER })
                AnaSekme.SON -> SonAramalarEkrani(onKisiDetay, onAyarlar)
                AnaSekme.REHBER -> RehberEkrani(onKisiDetay, onArama, onAyarlar)
                AnaSekme.TUSLAR -> TusTakimiEkrani(baslangicNumara)
            }
        }
        IosSekmeCubugu(secili = sekme, onSec = { sekme = it })
    }
}
