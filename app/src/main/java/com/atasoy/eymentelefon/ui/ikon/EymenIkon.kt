package com.atasoy.eymentelefon.ui.ikon

// ✅ v1.1 İkonlar: design/ikonlar.py → res/drawable/ik_*.xml (design/ikon_ceviri.py ile üretilir).

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.ui.theme.EymenBoyut

enum class EymenIkon(@DrawableRes val res: Int) {
    Telefon(R.drawable.ik_telefon),
    YildizDolu(R.drawable.ik_yildiz_dolu),
    SaatDolu(R.drawable.ik_saat_dolu),
    KisiDolu(R.drawable.ik_kisi_dolu),
    Tuslar(R.drawable.ik_tuslar),
    MesajDolu(R.drawable.ik_mesaj_dolu),
    VideoDolu(R.drawable.ik_video_dolu),
    EpostaDolu(R.drawable.ik_eposta_dolu),
    Hoparlor(R.drawable.ik_hoparlor),
    Sessiz(R.drawable.ik_sessiz),
    Beklet(R.drawable.ik_beklet),
    Arti(R.drawable.ik_arti),
    KisilerDolu(R.drawable.ik_kisiler_dolu),
    Alarm(R.drawable.ik_alarm),
    Disli(R.drawable.ik_disli),
    Bluetooth(R.drawable.ik_bluetooth),
    Buyutec(R.drawable.ik_buyutec),
    Bilgi(R.drawable.ik_bilgi),
    Ileri(R.drawable.ik_ileri),
    Geri(R.drawable.ik_geri),
    Sil(R.drawable.ik_sil),
    Giden(R.drawable.ik_giden),
    Gelen(R.drawable.ik_gelen),
    Engelle(R.drawable.ik_engelle),
    Kalkan(R.drawable.ik_kalkan),
    Bildirim(R.drawable.ik_bildirim),
    Kart(R.drawable.ik_kart),
    Cihaz(R.drawable.ik_cihaz),
    Tema(R.drawable.ik_tema),
    Palet(R.drawable.ik_palet),
    Titresim(R.drawable.ik_titresim),
    Dosya(R.drawable.ik_dosya),
    Paylas(R.drawable.ik_paylas),
    Onay(R.drawable.ik_onay),
    Yildiz(R.drawable.ik_yildiz),
    Duzenle(R.drawable.ik_duzenle),
    DahaFazla(R.drawable.ik_daha_fazla),
    KisiEkle(R.drawable.ik_kisi_ekle),
    KapatX(R.drawable.ik_kapat_x),
}

/** @param kapatma true → telefon ikonu 135° döner (bitir / reddet). */
@Composable
fun EymenIkonu(
    ikon: EymenIkon,
    aciklama: String?,
    renk: Color,
    modifier: Modifier = Modifier,
    boyut: Dp = EymenBoyut.ikon,
    kapatma: Boolean = false,
) {
    Icon(
        painter = painterResource(ikon.res),
        contentDescription = aciklama,
        tint = renk,
        modifier = modifier.size(boyut).then(if (kapatma) Modifier.rotate(KAPATMA_ACISI) else Modifier),
    )
}

private const val KAPATMA_ACISI = 135f
