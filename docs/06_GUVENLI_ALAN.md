# 06 · Güvenli Alan — kamera deliği ve gezinme tuşları (OTOMATİK)

**Sorun (v1.0):** Bazı cihazlarda içerik gezinme tuşlarının altında kalıyor ya da kameranın (çentik / delik) üstüne çiziliyordu.
Nedeni: Android 15+ (targetSdk 35) uygulamayı kenardan kenara çizer; v1.0 sadece `statusBarsPadding` / `navigationBarsPadding`
kullanıyordu. Kilit ekranında, yatay modda ya da bazı Tecno/Samsung cihazlarda **durum çubuğu yüksekliği kamera kesiğinden
küçük** olabiliyor → içerik kameranın arkasına giriyordu.

**Çözüm (v1.1):** Her ekran tek bir yerden geçer: `ui/bilesenler/GuvenliAlan.kt`

```
WindowInsets.safeDrawing = durum çubuğu ∪ kamera kesiği (displayCutout) ∪ gezinme çubuğu ∪ klavye
```

| Parça | Ne yapar | Nerede |
|---|---|---|
| `IosSayfa(...)` | Zemin rengi ekranın tamamını boyar; içerik güvenli alanda | Tüm ekranların kökü |
| `Modifier.ustGuvenli()` | Üst + yanlar (dikeyde kamera deliği, yatayda yandaki kamera) | `IosSayfa`, arama ekranı |
| `Modifier.altGuvenli()` | Alt (3 tuşlu gezinme / hareket çubuğu / açık klavye) | `IosSayfa`, sekme çubuğu, arama ekranı |
| `Modifier.yatayGuvenli()` | Sadece yanlar | Sekme çubuğu |
| Tema: `windowLayoutInDisplayCutoutMode = shortEdges` | Pencere kamera kesiğinin yanına kadar uzanır, Compose boşluğu hesaplar | `res/values/themes.xml` |
| `enableEdgeToEdge(SystemBarStyle)` | Durum/gezinme çubuğu saydam; ikon rengi temaya göre (açık temada koyu ikon) | `MainActivity`, `CallActivity` |

### Davranış
- **Hareket çubuğu (gesture):** Sekme çubuğu zemini çubuğun arkasına uzanır, ikonlar üstünde kalır.
- **3 tuşlu gezinme (◁ ○ ▢):** Sekme çubuğu tuşların hemen üstünde biter; hiçbir buton tuşların arkasına girmez.
- **Kamera deliği (ortada / köşede / damla çentik):** Büyük başlık ve gezinme çubuğu deliğin altından başlar.
- **Yatay mod:** Yandaki kamera kesiği kadar içerik içeri çekilir.
- **Klavye:** Arama ekranında liste klavyenin üstünde biter (`safeDrawing` klavyeyi de içerir).
- **Kilit ekranı (gelen arama):** Durum çubuğu gizli olsa da kamera kesiği hesaba katılır → ad kameranın altında başlar.
- İç içe kullanımda aynı boşluk iki kez eklenmez (`windowInsetsPadding` tüketir).

### Kural (AGENTS.md)
Hiçbir ekranda `statusBarsPadding()`, `navigationBarsPadding()`, `imePadding()` ya da sabit üst/alt boşluk kullanılmaz.
Yeni bir kök gerekiyorsa `IosSayfa` kullanılır.

### Test (GOREV-03)
Samsung (Android 14, hareket + 3 tuş ayrı ayrı) ve Tecno (Android 16): her ekranda üstte kamera deliğine, altta gezinme
tuşlarına hiçbir metin/buton değmemeli; telefonu yan çevirip tekrar bak.
