package com.atasoy.eymentelefon.gorusme.ui

import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.cekirdek.ayar.TemaSecimi
import com.atasoy.eymentelefon.gorusme.GorusmeAsamasi
import com.atasoy.eymentelefon.gorusme.GorusmeDurumu
import com.atasoy.eymentelefon.ui.bilesenler.AltSayfa
import com.atasoy.eymentelefon.ui.bilesenler.EymenButon
import com.atasoy.eymentelefon.ui.bilesenler.EymenButonTuru
import com.atasoy.eymentelefon.ui.bilesenler.KaydirarakCevapla
import com.atasoy.eymentelefon.ui.bilesenler.KisiAvatari
import com.atasoy.eymentelefon.ui.theme.AramaEkraniRenk
import com.atasoy.eymentelefon.ui.theme.EymenBosluk
import com.atasoy.eymentelefon.ui.theme.EymenBoyut
import com.atasoy.eymentelefon.ui.theme.EymenRenk
import com.atasoy.eymentelefon.ui.theme.EymenTelefonTemasi
import com.atasoy.eymentelefon.ui.theme.EymenYazi

@Composable
fun GelenAramaEkrani(
    vm: GorusmeViewModel = viewModel(factory = GorusmeViewModel.Fabrika),
) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val erisilebilirlik = context.getSystemService(AccessibilityManager::class.java)
    val ayriDugmeler = erisilebilirlik?.isTouchExplorationEnabled == true

    GelenAramaIcerik(
        durum = durum,
        onOlay = vm::olay,
        ayriDugmeler = ayriDugmeler,
    )
}

@Composable
private fun GelenAramaIcerik(
    durum: GorusmeUiState,
    onOlay: (GorusmeOlay) -> Unit,
    ayriDugmeler: Boolean,
) {
    val renk = EymenRenk
    val g = durum.gorusme
    val ad = durum.ad.ifBlank {
        g?.numara?.takeIf(String::isNotBlank)
            ?: stringResource(R.string.gorusme_bilinmeyen)
    }
    Column(
        modifier = Modifier.fillMaxSize().background(renk.aramaZemini)
            .padding(horizontal = EymenBosluk.kenar, vertical = EymenBosluk.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.s),
        ) {
            Text(
                stringResource(R.string.gorusme_gelen),
                style = EymenYazi.govde,
                color = renk.metin2,
            )
            g?.simSirasi?.let {
                Text(
                    stringResource(R.string.gorusme_sim, it),
                    style = EymenYazi.kucuk,
                    color = renk.metin3,
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.l),
        ) {
            KisiAvatari(ad, fotoUri = durum.fotoUri, boyut = EymenBoyut.avatarGelen)
            Text(ad, color = AramaEkraniRenk.beyaz, style = EymenYazi.aramaIsim)
            if (!durum.ad.isBlank() && !g?.numara.isNullOrBlank()) {
                Text(g?.numara.orEmpty(), color = renk.metin2, style = EymenYazi.govde)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EymenBosluk.l),
        ) {
            if (ayriDugmeler) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(EymenBosluk.m),
                ) {
                    EymenButon(
                        stringResource(R.string.gelen_reddet),
                        onClick = { onOlay(GorusmeOlay.Reddet) },
                        modifier = Modifier.weight(1f),
                        tur = EymenButonTuru.IKINCIL,
                    )
                    EymenButon(
                        stringResource(R.string.gelen_cevapla),
                        onClick = { onOlay(GorusmeOlay.Cevapla) },
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                KaydirarakCevapla(
                    onCevapla = { onOlay(GorusmeOlay.Cevapla) },
                    onReddet = { onOlay(GorusmeOlay.Reddet) },
                )
                Text(
                    stringResource(R.string.gelen_kaydir_ipucu),
                    style = EymenYazi.kucuk,
                    color = renk.metin3,
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .heightIn(min = EymenBoyut.dokunmaMin)
                    .clickable { onOlay(GorusmeOlay.HizliYanitMenusuDegistir) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(R.string.gelen_mesajla_reddet),
                    style = EymenYazi.govde,
                    color = renk.metin2,
                )
            }
        }
    }

    if (durum.hizliYanitAcik) {
        AltSayfa(onKapat = { onOlay(GorusmeOlay.HizliYanitMenusuDegistir) }) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = EymenBosluk.kenar),
                verticalArrangement = Arrangement.spacedBy(EymenBosluk.m),
            ) {
                Text(
                    stringResource(R.string.gelen_mesajla_reddet),
                    style = EymenYazi.baslik2, color = renk.metin1,
                )
                durum.hizliYanitlar.forEach { mesaj ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = EymenBoyut.anaButon)
                            .clickable { onOlay(GorusmeOlay.MesajlaReddet(mesaj)) }
                            .padding(EymenBosluk.s),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(mesaj, style = EymenYazi.govde, color = renk.metin1)
                    }
                }
                Spacer(Modifier.size(EymenBosluk.xl))
            }
        }
    }
}

@Preview(name = "Koyu")
@Composable private fun GelenKoyuOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.KOYU, zorlaKoyu = true) {
        GelenAramaIcerik(
            GorusmeUiState(
                gorusme = GorusmeDurumu(
                    "g2", "05321234567", "Ayşe Demir", GorusmeAsamasi.CALIYOR,
                    null, false, true, false, 2,
                ),
                ad = "Ayşe Demir",
            ),
            onOlay = {},
            ayriDugmeler = false,
        )
    }
}

@Preview(name = "Acik")
@Composable private fun GelenAcikOnizleme() {
    EymenTelefonTemasi(tema = TemaSecimi.ACIK, zorlaKoyu = true) {
        GelenAramaIcerik(GorusmeUiState(), onOlay = {}, ayriDugmeler = true)
    }
}
