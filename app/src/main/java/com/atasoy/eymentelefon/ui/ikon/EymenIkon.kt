package com.atasoy.eymentelefon.ui.ikon

// ✅ HAZIR — DEĞİŞTİRME. Mockup'lardaki ikonların birebir aynısı (res/drawable/ik_*.xml).
// material-icons KULLANMA. Yeni ikon gerekirse HATA_GUNLUGU › SORULAR'a yaz.

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.layout.size
import com.atasoy.eymentelefon.R
import com.atasoy.eymentelefon.ui.theme.EymenBoyut

enum class EymenIkon(@DrawableRes val res: Int) {
    Telefon(R.drawable.ik_telefon),
    Buyutec(R.drawable.ik_buyutec),
    Ayarlar(R.drawable.ik_ayarlar),
    Yildiz(R.drawable.ik_yildiz),
    YildizDolu(R.drawable.ik_yildiz_dolu),
    Saat(R.drawable.ik_saat),
    Kisiler(R.drawable.ik_kisiler),
    Tuslar(R.drawable.ik_tuslar),
    Sil(R.drawable.ik_sil),
    Sessiz(R.drawable.ik_sessiz),
    Hoparlor(R.drawable.ik_hoparlor),
    Beklet(R.drawable.ik_beklet),
    KisiEkle(R.drawable.ik_kisi_ekle),
    Mesaj(R.drawable.ik_mesaj),
    Video(R.drawable.ik_video),
    Kalkan(R.drawable.ik_kalkan),
    Bildirim(R.drawable.ik_bildirim),
    Kart(R.drawable.ik_kart),
    Onay(R.drawable.ik_onay),
    Ileri(R.drawable.ik_ileri),
    Geri(R.drawable.ik_geri),
    Gelen(R.drawable.ik_gelen),
    Giden(R.drawable.ik_giden),
    DahaFazla(R.drawable.ik_daha_fazla),
    Duzenle(R.drawable.ik_duzenle),
    Engelle(R.drawable.ik_engelle),
    Tema(R.drawable.ik_tema),
    Cihaz(R.drawable.ik_cihaz),
    Bilgi(R.drawable.ik_bilgi),
    Dosya(R.drawable.ik_dosya),
    Paylas(R.drawable.ik_paylas),
    Arti(R.drawable.ik_arti),
    Eposta(R.drawable.ik_eposta),
    Titresim(R.drawable.ik_titresim),
    Palet(R.drawable.ik_palet),
    Alarm(R.drawable.ik_alarm),
    Bluetooth(R.drawable.ik_bluetooth),
}

/**
 * Tek ikon çizim fonksiyonu.
 * @param kapatma true ise telefon ikonu 135° döner (aramayı bitir / reddet butonu).
 */
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
        modifier = modifier.size(boyut).then(if (kapatma) Modifier.rotate(135f) else Modifier),
    )
}
