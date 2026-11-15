# 08 · v1.3 Planı — kullanıcı onayladı (15 Kasım 2026, 14:30)

Kullanıcı isteği: "arama tuşlarını büyüt, rehberleri ve rehber içlerini daha profesyonel, efektli, gerçekçi, renkli yap; cam efektleri; release APK".
Kullanıcı yeni kütüphaneleri onayladı: **Haze** (cam/bulanıklık), **androidx.palette** (fotoğraftan renk), **androidx.glance** (ana ekran bileşeni).
AGENTS.md "yeni kütüphane yok" kuralının tek istisnası bu üçüdür. Durum: `[x]` Claude yaptı · `[ ]` sırada.

### [x] V13-01 · Release APK
- CI: `assembleRelease` (R8 + küçültme) → artifact **EymenTelefon-release**. Debug artifact da kalır.
- İmza: GitHub secret **EYMEN_IMZA** (keystore'un base64'ü; kullanıcıya dosya olarak verildi, şifreler workflow'da). Secret yoksa release debug anahtarıyla imzalanır (her derlemede farklı → üstüne kurulmaz; uyarı adımı yazar).
- Keystore repoya KONMAZ (repo herkese açık).

### [x] V13-02 · Büyük tuşlar
- `EymenBoyut.tus` 78→86dp, `tusRakam` 34→38sp, tuş harfleri 9.5→10.5sp. Görüşme butonları zaten 86/88 (HATA-024).

### [x] V13-03 · Renkli avatarlar
- `IosAvatar`: monogramlı avatarlar ada göre 8 canlı gradyandan biri (`AvatarRenk.renkli(anahtar)`), "#" gri kalır. Fotoğraflı avatar değişmez.

### [x] V13-04 · Cam sekme çubuğu
- Ana ekran içeriği sekme çubuğunun altına uzanır (`hazeSource`), sekme çubuğu buzlu cam (`hazeEffect`). Listeler sonuna sekme çubuğu yüksekliği kadar boşluk (`LocalAltCamBoslugu`).

### [x] V13-05 · Cam arama düğmeleri + fotoğraf rengi
- Arama ekranı zemini `hazeSource`; `IosCagriButonu` cam (arka poster bulanık + %18 beyaz + ince ışık kenarı).
- Fotoğraftan baskın canlı renk (`Palette`) → fotoğrafsız zeminde değil, posterli/fotoğraflı ekranda düğme cam tonu ve kişi kartı başlık gradyanı.

### [x] V13-06 · Cam pencereler
- `IosUyari`, `IosEylemSayfasi`: Android 12+ pencere arkası bulanık (`FLAG_BLUR_BEHIND`) + yarı saydam yüzey.

### [x] V13-07 · Yaylı basma
- `Modifier.yayliBasma()` (Tiklama.kt): basınca 0.92 ölçek, yay ile geri döner. Arama düğmeleri, tuş takımı, eylem kutuları.

### [ ] V13-08 · Ortak geçiş (avatar → kişi kartı)
- `SharedTransitionLayout` NavHost'u sarar; Kişiler/Favoriler satır avatarı ile kişi kartı başlığı `sharedElement("avatar-<id>")`.

### [ ] V13-09 · Kişi kartı v2
- Posteri olmayan kişide başlık: avatar renginde (fotoğraf varsa Palette renginde) dikey gradyan, büyük avatar, beyaz ad.
- "Görüşmeler" grubu: bu ay görüşme sayısı, toplam konuşma süresi, son görüşme.

### [ ] V13-10 · Favoriler ana ekran bileşeni (Glance)
- 4 favori, yuvarlak fotoğraf/monogram + ad; dokununca arar (`WidgetAramaActivity` → `AramaBaslatici.ara`). Favori değişince güncellenir.

### Yapılmayan (bilerek)
- **Derinlik efekti (kişi adın önüne geçer):** cihazda kişi ayırma için ML Kit gerekiyor; ML Kit Google'a kullanım kaydı gönderir ve INTERNET izni ekler → projenin "internet yok / analitik yok" kuralına aykırı. Kullanıcıya bildirildi.
