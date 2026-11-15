# Eymen Telefon 📞 — v1.1 (iOS tarzı)

iOS Telefon uygulaması düzeninde; rehber, son aramalar, tuş takımı ve kendi arama ekranları olan, varsayılan telefon uygulaması olabilen Android uygulaması.
Kamera deliği ve gezinme tuşlarına göre ekran **otomatik** ayarlanır (`docs/06_GUVENLI_ALAN.md`).
Tasarım, mimari ve kodun tamamı: **Claude** · Derleme, test ve hata düzeltme: **Codex** (kurallar: `AGENTS.md`)

![Genel bakış](docs/mockups/00_genel_bakis.png)

## Klasör yapısı
```
EymenTelefon/
├── AGENTS.md               ← Codex kuralları (her görevden önce okunur)
├── DEVAM_NOTLARI.md        ← Claude → Codex devir notu: durum, sıra, izin verilen düzeltmeler
├── HATA_GUNLUGU.md         ← Hatalar: önce not → düzelt → "DÜZELTİLDİ" yaz
├── docs/
│   ├── 01_TASARIM_SISTEMI.md   renk, yazı, ölçü, bileşen
│   ├── 02_EKRANLAR.md          20 ekran: görsel → dosya → çalışan her şey (tek tek)
│   ├── 03_MIMARI.md            paket ağacı (74 Kotlin dosyası)
│   ├── 04_IZINLER.md           açılış öncesi izin akışı + aramaya basınca izin
│   ├── 05_GOREV_PLANI.md       Claude'un tamamladıkları + Codex'in 4 test/düzeltme görevi
│   ├── 06_GUVENLI_ALAN.md      kamera deliği / gezinme tuşları çözümü
│   └── mockups/                20 ekran PNG (her biri ayrı) + genel bakış
├── design/
│   ├── icon/                   simge SVG + Play Store 512px + 1024px
│   ├── ikonlar.py              tüm ikonlar (tek kaynak)
│   ├── ikon_ceviri.py          ikonlar.py → res/drawable/ik_*.xml
│   └── mockup_uretici_ios.py   20 ekran görselini yeniden üretir
├── app/                        Android projesi (Kotlin + Compose)
└── .github/workflows/apk-derle.yml   her push'ta APK + lint raporu
```

## Ekranlar (her biri `docs/mockups/` içinde ayrı görsel)
| # | Ekran | # | Ekran |
|---|---|---|---|
| 01 | Karşılama | 11 | Giden arama |
| 02 | İzinler (açılıştan önce) | 12 | Görüşme |
| 03 | Favoriler | 13 | Görüşmede tuş takımı |
| 04/05 | Son Aramalar (açık/koyu) | 14 | Gelen arama (kilit açık) |
| 06 | Kişiler | 15 | Gelen arama (kilitli, kaydır) |
| 07 | Kişi kartı | 16 | Mesajla yanıtla |
| 08/09 | Tuş takımı (açık/koyu) | 17 | İzin uyarısı |
| 10 | Arama | 18 | Ayarlar |
| 19 | Kaydırarak sil | 20 | Güvenli alan rehberi |

## Codex'e ilk mesaj
`DEVAM_NOTLARI.md` › §6'daki mesajı yapıştır.

## Derleme
- Yerel: `./gradlew assembleDebug` → `app/build/outputs/apk/debug/`
- GitHub: Actions › **APK Derle** › son çalıştırma › `EymenTelefon-debug` dosyası
- Gereken: JDK 17, Android SDK 36

## Teknik özet
Kotlin 2.1 · Compose BOM 2025.05 · Material 3 · minSdk 29 (Android 10) · targetSdk 35 · compileSdk 36
`InCallService` + `RoleManager.ROLE_DIALER` · `ContactsContract` · `CallLog` · `BlockedNumberContract` · DataStore
İnternet izni yok, reklam yok, arama kaydı yok.
