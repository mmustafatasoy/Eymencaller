# 04 · İzin Akışı (uygulama açılmadan önce + aramaya basınca)

## 1. İzin listesi (sabit, `IzinTanimi.kt`'de hazır)

| # | Kart | Android izinleri | Zorunlu | Not |
|---|---|---|---|---|
| 1 | Varsayılan telefon uygulaması | `RoleManager.ROLE_DIALER` | ✅ | `createRequestRoleIntent` ile istenir |
| 2 | Telefon | `CALL_PHONE`, `READ_PHONE_STATE`, `ANSWER_PHONE_CALLS`, `READ_PHONE_NUMBERS` | ✅ | Tek grup halinde istenir |
| 3 | Rehber | `READ_CONTACTS`, `WRITE_CONTACTS` | ✅ | |
| 4 | Arama geçmişi | `READ_CALL_LOG`, `WRITE_CALL_LOG` | ✅ | |
| 5 | Bildirimler | `POST_NOTIFICATIONS` | ❌ | Sadece API 33+ ; altında kart gizli |

> Not: Varsayılan telefon uygulaması rolü verilince Android 2–4'ü çoğu cihazda otomatik verir. Bu yüzden **önce rol** istenir, sonra eksik kalanlar.

## 2. Açılış akışı (`MainActivity`)

```
Uygulama açıldı
  │
  ├─ ilkAcilisTamam == false ─────────────→ 01 Karşılama → 02 İzinler
  │
  ├─ IzinYoneticisi.zorunlularTamam() == false → 02 İzinler   (ana ekran HİÇ gösterilmez)
  │
  └─ hepsi tamam ──────────────────────────→ Ana ekran (son açılan sekme)
```

- Splash API (`installSplashScreen`) ile karar verilene kadar sistem açılış ekranı tutulur (`setKeepOnScreenCondition`) → kullanıcı yanlış ekranı bir an bile görmez.
- `ON_RESUME`'da tekrar kontrol: kullanıcı ayarlardan izni geri aldıysa → 02'ye döner.

## 3. 02 ekranında kart davranışı

| Durum | Çip | Dokununca |
|---|---|---|
| Verildi | `✓ Verildi` (yeşil) | hiçbir şey |
| İstenmedi | `İzin ver` | `ActivityResultContracts.RequestMultiplePermissions` |
| Rol yok | `Ayarla` | `RoleManager.createRequestRoleIntent(ROLE_DIALER)` |
| Reddedildi (1 kez) | `İzin ver` | tekrar ister |
| Kalıcı reddedildi (`shouldShowRequestPermissionRationale == false` + daha önce istendi) | `Ayarlardan aç` | `Settings.ACTION_APPLICATION_DETAILS_SETTINGS` |

"Daha önce istendi" bilgisi `AyarDeposu.istenenIzinler` setinde tutulur.

## 4. Aramaya basınca (12 numaralı alt sayfa)
- `AramaBaslatici.ara(numara)` çağrılır → `IzinYoneticisi.aramaIcinEksikler()` (rol + Telefon).
- Eksik varsa alt sayfa açılır, bekleyen numara `AramaBaslatici.bekleyenNumara`'da tutulur.
- "İzin ver ve ara" → eksikler sırayla istenir → hepsi tamam ise `placeCall` otomatik.
- Rol yoksa ama `CALL_PHONE` varsa bile arama yapılmaz, alt sayfa açılır (arama ekranı bizim uygulamada açılmalı).

## 5. Gelen arama geldiğinde
- Rol yoksa sistem aramayı bize vermez; bu durumda ilk açılışta 12 numaralı alt sayfa "Gelen aramaları görebilmek için varsayılan uygulama olun" metniyle bir kez gösterilir.
- Bildirim izni yoksa gelen arama yine **CallActivity** ile açılır (full-screen intent yerine doğrudan `startActivity`, InCallService'ten).
- Android 14+: `USE_FULL_SCREEN_INTENT` varsayılan telefon uygulamalarına otomatik verilir; `NotificationManager.canUseFullScreenIntent()` false ise HataKaydedici'ye uyarı yaz ve doğrudan activity aç.

## 6. Manifest (hazır — değiştirme)
`app/src/main/AndroidManifest.xml` içinde tüm izinler, `InCallService`, `CallScreeningService`, `ACTION_DIAL` filtreleri hazırdır. **`INTERNET` izni YOK.**
