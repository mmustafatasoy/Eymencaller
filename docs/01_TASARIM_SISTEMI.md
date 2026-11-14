# 01 · Tasarım Sistemi (DEĞİŞTİRİLEMEZ)

Tüm değerler `app/src/main/java/com/atasoy/eymentelefon/ui/theme/` içinde **hazır yazılmıştır**. Codex bu değerleri kullanır, yenisini eklemez.
Görsel referans: `docs/mockups/00_genel_bakis.png`.

## 1. Renkler — `EymenRenk`

| Token | Koyu tema (varsayılan) | Açık tema | Kullanım |
|---|---|---|---|
| `arkaPlan` | `#0A0F1C` | `#F5F7FB` | Ekran zemini |
| `yuzey1` | `#121A2B` | `#FFFFFF` | Kart, arama çubuğu, tuş |
| `yuzey2` | `#1A2438` | `#EEF2F8` | Kart içi kutu, ikon zemini |
| `yuzey3` | `#24304A` | `#DDE3EE` | Pasif anahtar, ilerleme zemini |
| `metin1` | `#EAF0FF` | `#0B1220` | Başlık, isim |
| `metin2` | `#A9B4CC` | `#46536E` | Açıklama |
| `metin3` | `#7C89A6` | `#6B7894` | Saat, alt bilgi, bölüm başlığı |
| `vurgu` | `#19D3A2` | `#0FB58A` | Seçili sekme, onay, link |
| `vurgu2` | `#3D7BFF` | `#2F6BFF` | Giden arama, gradyan sonu |
| `araYesil` | `#22C55E` | `#16A34A` | Ara butonu |
| `kapatKirmizi` | `#E11D48` | `#E11D48` | Aramayı bitir/reddet |
| `cevapsiz` | `#FF5A5F` | `#E5484D` | Cevapsız arama, engelle |
| `uyari` | `#FFB547` | `#B7791F` | "Zorunlu" etiketi |
| `aramaZemini` | `#080C18` | `#080C18` | Arama ekranları (her iki temada koyu) |

**Gradyanlar**
- `vurguGradyan`: 135°, `#19D3A2 → #3D7BFF` (ana buton, FAB, Ara aksiyonu, ilerleme çubuğu, simge)
- `araGradyan`: 135°, `#22C55E → #19D3A2` (tuş takımı Ara butonu, gelen arama Cevapla)
- `kapatGradyan`: 135°, `#FF5A5F → #E11D48`
- Avatar gradyanları (kişinin adının hash'i % 8 ile seçilir): `EymenRenk.avatarGradyanlari` listesi.

**Vurgu rengi seçimi (Ayarlar):** 5 seçenek → `#19D3A2` (varsayılan), `#3D7BFF`, `#8B5CF6`, `#FF8A5B`, `#F857A6`. Seçim sadece `vurgu` token'ını değiştirir.

## 2. Boşluk — `EymenBosluk`
`xs=4dp · s=8dp · m=12dp · l=16dp · kenar=18dp · xl=24dp · xxl=32dp`
- Ekran yan boşluğu: **18dp** (`kenar`)
- Liste satırı dikey: 8dp, avatar–metin arası: 12dp
- Kartlar arası: 10dp → `EymenBosluk.kartArasi`
- Tüm bileşen ölçüleri (buton yüksekliği, avatar boyları, tuş ölçüsü…) → `EymenBoyut` (ui/theme/Olcu.kt)

## 3. Şekiller — `EymenSekil`
| Öğe | Köşe |
|---|---|
| Kart | 20dp |
| İzin kartı | 18dp |
| Ana buton (54dp yükseklik) | 18dp |
| Arama çubuğu (50dp) | tam yuvarlak (25dp) |
| Tuş (76×62dp) | tam yuvarlak |
| FAB (58dp) | 20dp |
| Alt sayfa | üst 30dp |
| Çip | 12dp |
| Aksiyon kutusu (58dp) | 20dp |

## 4. Yazı — `EymenYazi`
Font: sistem (Roboto). Ek font dosyası YOK.

| Stil | Boyut / kalınlık | Nerede |
|---|---|---|
| `girisBaslik` | 30sp / ExtraBold | Karşılama, arama ekranlarındaki isim (28–32sp) |
| `baslik1` | 24sp / ExtraBold, -0.4sp | Ekran başlığı |
| `baslik2` | 20sp / Bold | Ayarlar, alt sayfa başlığı |
| `isim` | 14.5sp / SemiBold | Liste isimleri |
| `govde` | 13.5sp / Normal | Açıklamalar |
| `kucuk` | 12sp / Normal | Saat, alt bilgi |
| `bolum` | 12sp / Bold, BÜYÜK HARF, 0.6sp aralık | "BUGÜN", "FAVORİLER" |
| `numaraEkrani` | 34sp / Light, 1sp aralık | Tuş takımı numarası |
| `tus` | 26sp / Medium | Tuş rakamı |
| `tusHarf` | 9sp / Normal, 1sp aralık | Tuş altındaki harfler |
| `sayac` | 16sp / Bold, tabular rakam | Görüşme süresi |

## 5. Bileşenler (ui/bilesenler/) — 15 bileşen, her biri tek dosya
| Bileşen | Mockup | Not |
|---|---|---|
| `EymenAramaCubugu` | 03,04,05,07 | 50dp, solda büyüteç, sağda profil avatarı (30dp). Odaklıyken 1.5dp `vurgu` çerçeve |
| `EymenAltMenu` | 03,04,05 | 4 sekme: Favoriler, Son, Rehber, Tuşlar. Seçili: 60×32dp `vurgu` hap içinde ikon |
| `KisiAvatari` | hepsi | Baş harfler, gradyan zemin, boyut parametreli. Fotoğraf varsa fotoğraf |
| `AramaSatiri` | 04 | Avatar · isim (+tekrar sayısı) · tür ikonu · saat · yeşil ara butonu (38dp) |
| `KisiSatiri` | 05 | Avatar 40dp · isim · numara |
| `IzinKarti` | 02 | İkon kutusu 42dp · başlık + etiket · açıklama · durum çipi |
| `EymenButon` | 01,02,12 | Birincil (gradyan), pasif (%45 opak), ikincil (sadece metin) |
| `EymenCip` | 02,04,11,12 | Durum çipi (Verildi/İzin ver/Eksik/Hazır), filtre çipi, "Zorunlu" etiketi |
| `TusTakimi` | 03,12 | 3×4 ızgara, tuş 76×62dp, yatay 26dp / dikey 10dp aralık |
| `AramaKontrolButonu` | 08,10 | 64dp daire, %8 beyaz zemin; aktifken `metin1` zemin + koyu ikon |
| `KapatButonu` | 08,10 | 76dp, `kapatGradyan`, telefon ikonu 135° döndürülmüş |
| `KaydirarakCevapla` | 09 | 76dp yükseklik ray; sol kırmızı (reddet), sağ yeşil (cevapla), sürükleme eşiği %35 |
| `HarfDizini` | 05 | Sağ kenar A–Z Türkçe alfabe; dokunup kaydırınca listeye atlar |
| `EymenAnahtar` | 11 | 44×26dp |
| `AltSayfa` | 12 | Üst 30dp köşe, tutamaç 42×5dp |

## 6. Hareket (animasyon)
- Ekran geçişi: 280ms, `FastOutSlowInEasing`, yatay kayma + solma.
- Tuşa basma: 90ms ölçek 1 → 0.94 + titreşim (`HapticFeedbackType.TextHandleMove`, ayar açıksa).
- Giden/gelen aramada avatar halkaları: 1600ms sonsuz nabız, ölçek 1 → 1.18, opaklık 0.35 → 0.
- Görüşme ses dalgası: 15 çubuk, 3dp genişlik, rastgele 8–28dp, 120ms'de bir güncellenir.
- Gelen aramada sağ yeşil düğme: 1200ms nabız halkası.

## 7. Simge
- Kaynak: `design/icon/icon.svg` · Play Store: `design/icon/icon_playstore_512.png`
- Android adaptive icon: `res/drawable/ic_launcher_foreground.xml` + `ic_launcher_background.xml` (hazır). **Değiştirme.**

## 8. Erişilebilirlik
- Kontrast: metin1/arkaPlan ≥ 7:1, metin3/arkaPlan ≥ 4.5:1 (değerler buna göre seçildi).
- Yazı boyutu ölçeklenince (1.3x) tuş takımı taşmamalı → tuş yazıları `maxLines = 1`.
