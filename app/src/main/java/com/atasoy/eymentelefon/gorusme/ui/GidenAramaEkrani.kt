package com.atasoy.eymentelefon.gorusme.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.gorusme.GorusmeAsamasi
import com.atasoy.eymentelefon.gorusme.GorusmeDurumu
import com.atasoy.eymentelefon.gorusme.SesYolu
import com.atasoy.eymentelefon.ui.bilesenler.AramaKontrolButonu
import com.atasoy.eymentelefon.ui.bilesenler.EymenButon
import com.atasoy.eymentelefon.ui.bilesenler.EymenButonTuru
import com.atasoy.eymentelefon.ui.bilesenler.TusTakimi
import com.atasoy.eymentelefon.ui.bilesenler.KapatButonu
import com.atasoy.eymentelefon.ui.bilesenler.KisiAvatari
import com.atasoy.eymentelefon.ui.ikon.EymenIkon
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

@Composable
fun GidenAramaEkrani(
    vm: GorusmeViewModel = viewModel(factory = GorusmeViewModel.Fabrika),
) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    GidenAramaIcerik(durum = durum, onOlay = vm::olay)
}

@Composable
private fun GidenAramaIcerik(
    durum: GorusmeUiState,
    onOlay: (GorusmeOlay) -> Unit,
) {
    val g = durum.gorusme
    val r = EymenRenk
    val ad = durum.ad.ifBlank {
        g?.numara?.takeIf(String::isNotBlank)
            ?: stringResource(R.string.gorusme_bilinmeyen)
    }
    val anim = rememberInfiniteTransition(label = "giden_arama_nabzi")
    val olcek by anim.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Restart),
        label = "giden_halka_olcek",
    )
    val gorunurluk by anim.animateFloat(
        initialValue = .35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Restart),
        label = "giden_halka_alpha",
    )

    Column(
        modifier = Modifier.fillMaxSize().background(r.aramaZemini)
            .background(Brush.verticalGradient(listOf(
                AramaEkraniRenk.isikMor, r.aramaZemini, r.aramaZemini,
                AramaEkraniRenk.isikMavi.copy(alpha = .20f),
            )))
            .padding(horizontal = EymenBosluk.kenar, vertical = EymenBosluk.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.s),
        ) {
            g?.simSirasi?.let {
                Text(
                    stringResource(R.string.gorusme_sim, it),
                    style = EymenYazi.kucuk,
                    color = AramaEkraniRenk.beyaz,
                )
            }
            Text(
                stringResource(
                    if (g?.asama == GorusmeAsamasi.BAGLANIYOR) R.string.gorusme_baglaniyor
                    else R.string.gorusme_araniyor
                ),
                style = EymenYazi.govde,
                color = r.metin2,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.xl),
        ) {
            Box(
                modifier = Modifier.size(EymenBoyut.avatarGiden + EymenBosluk.xxl),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier.size(EymenBoyut.avatarGiden)
                        .graphicsLayer { scaleX = olcek; scaleY = olcek }
                        .alpha(gorunurluk)
                        .background(r.vurgu, CircleShape)
                )
                KisiAvatari(ad, fotoUri = durum.fotoUri, boyut = EymenBoyut.avatarGiden)
            }
            Text(ad, style = EymenYazi.aramaIsim, color = AramaEkraniRenk.beyaz)
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.xxl),
        ) {
            if (durum.tuslarAcik) {
                TusTakimi(
                    onTus = { onOlay(GorusmeOlay.Dtmf(it)) },
                    onUzunBas = { onOlay(GorusmeOlay.Dtmf(it)) },
                )
                EymenButon(
                    metin = stringResource(R.string.kontrol_tuslar),
                    onClick = { onOlay(GorusmeOlay.TuslariDegistir) },
                    tur = EymenButonTuru.IKINCIL,
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AramaKontrolButonu(
                        EymenIkon.Sessiz, stringResource(R.string.kontrol_sessiz),
                        durum.ses.sessiz, { onOlay(GorusmeOlay.SessizDegistir) },
                    )
                    AramaKontrolButonu(
                        EymenIkon.Tuslar, stringResource(R.string.kontrol_tuslar),
                        false, { onOlay(GorusmeOlay.TuslariDegistir) },
                    )
                    AramaKontrolButonu(
                        EymenIkon.Hoparlor, stringResource(R.string.kontrol_hoparlor),
                        durum.ses.yol == SesYolu.HOPARLOR,
                        { onOlay(GorusmeOlay.HoparlorDegistir) },
                    )
                }
            }
            KapatButonu(onClick = { onOlay(GorusmeOlay.Kapat) })
        }
    }
}

@Preview(name = "Koyu")
@Composable private fun GidenKoyuOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU) {
        GidenAramaIcerik(
            GorusmeUiState(
                gorusme = GorusmeDurumu(
                    "g1", "05321234567", "Ayşe Demir", GorusmeAsamasi.ARANIYOR,
                    null, false, true, false, 1,
                ),
                ad = "Ayşe Demir",
            ),
            onOlay = {},
        )
    }
}

@Preview(name = "Acik")
@Composable private fun GidenAcikOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK, zorlaKoyu = true) {
        GidenAramaIcerik(GorusmeUiState(), onOlay = {})
    }
}
