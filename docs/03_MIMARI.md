# 03 · Mimari ve Kod Yapısı (DEĞİŞTİRİLEMEZ)

## 1. Teknoloji
| Konu | Seçim |
|---|---|
| Dil | Kotlin 2.1 |
| Arayüz | Jetpack Compose + Material 3 |
| Mimari | MVVM, tek yönlü veri akışı (UiState + Olay) |
| Asenkron | Coroutines + Flow |
| Ayar saklama | DataStore Preferences |
| Navigasyon | Navigation Compose |
| Bağımlılık enjeksiyonu | **Elle** — `UygulamaKonteyneri` (Hilt/Koin YOK) |
| Veri kaynağı | Android sistem sağlayıcıları: `ContactsContract`, `CallLog`, `BlockedNumberContract` (kendi veritabanı YOK) |
| Arama altyapısı | `android.telecom` — `InCallService`, `TelecomManager`, `RoleManager.ROLE_DIALER` |
| minSdk / targetSdk / compileSdk | 29 / 35 / 36 |
| Paket adı | `com.atasoy.eymentelefon` |

## 2. Paket ağacı (kesin — ✅ = hazır yazıldı, ⬜ = Codex yazacak)

```
app/src/main/java/com/atasoy/eymentelefon/
├── EymenTelefonUygulamasi.kt            ✅ Application, konteyner + HataKaydedici kurulumu
├── UygulamaKonteyneri.kt                ✅ Elle DI (tüm depolar burada tek örnek)
├── MainActivity.kt                      ✅ Başlangıç yönlendirmesi (karşılama/izin/ana)
│
├── cekirdek/
│   ├── hata/HataKaydedici.kt            ✅ Çökme + yakalanan hata kaydı (dosyaya)
│   ├── izin/IzinYoneticisi.kt           ✅ Tüm izin ve rol kontrolleri
│   ├── izin/IzinTanimi.kt               ✅ 5 iznin tanımı (sıra, zorunluluk, metin)
│   ├── arama/AramaBaslatici.kt          ✅ Tek arama giriş noktası
│   ├── ayar/AyarDeposu.kt               ✅ DataStore
│   └── yardimci/
│       ├── NumaraBicimleyici.kt         ⬜ TR numara biçimi
│       ├── T9Eslestirici.kt             ✅ Türkçe T9 tablosu
│       └── TurkceMetin.kt               ✅ Türkçe sıralama/arama yardımcıları
│
├── veri/
│   ├── model/Kisi.kt                    ✅
│   ├── model/AramaKaydi.kt              ✅
│   ├── rehber/RehberDeposu.kt           ⬜ ContactsContract okuma (+ContentObserver ile Flow)
│   ├── gecmis/AramaGecmisiDeposu.kt     ⬜ CallLog okuma/silme (+ContentObserver)
│   └── engel/EngelDeposu.kt             ⬜ BlockedNumberContract
│
├── gorusme/                             (arama motoru)
│   ├── EymenInCallService.kt            ✅ Sistem bu servise aramaları verir
│   ├── GorusmeYoneticisi.kt             ✅ Aktif aramalar StateFlow (tek kaynak)
│   ├── GorusmeDurumu.kt                 ✅ Arayüz için sade durum modeli
│   ├── GorusmeBildirimi.kt              ⬜ Gelen/devam eden arama bildirimi (CallStyle)
│   ├── EymenCallScreeningService.kt     ⬜ Engelli numaraları reddet
│   └── ui/
│       ├── CallActivity.kt              ✅ Kilit ekranı üstü, durumdan ekrana yönlendirir
│       ├── GorusmeViewModel.kt          ⬜
│       ├── GidenAramaEkrani.kt          ⬜ mockup 08
│       ├── GelenAramaEkrani.kt          ⬜ mockup 09
│       └── GorusmeEkrani.kt             ⬜ mockup 10
│
└── ui/
    ├── theme/                           ✅ Renk.kt · Yazi.kt · Olcu.kt (EymenBosluk · EymenBoyut · EymenSekil) · Tema.kt
    ├── ikon/EymenIkon.kt                ✅ Tüm ikonlar (res/drawable/ik_*.xml) tek yerde
    ├── navigasyon/Rotalar.kt            ✅
    ├── navigasyon/EymenNavHost.kt       ✅ (ekranları bağlar)
    ├── bilesenler/                      ⬜ 01_TASARIM_SISTEMI.md §5'teki 15 bileşen, her biri ayrı dosya
    └── ekranlar/
        ├── karsilama/KarsilamaEkrani.kt             ⬜ 01
        ├── izinler/IzinlerEkrani.kt + IzinlerViewModel.kt   ⬜ 02
        ├── anaekran/AnaEkran.kt                     ⬜ alt menülü iskelet (4 sekme)
        ├── favoriler/FavorilerEkrani.kt + VM        ⬜ (görsel: 05'teki favori şeridinin ızgara hali)
        ├── tuslar/TusTakimiEkrani.kt + VM           ⬜ 03
        ├── sonaramalar/SonAramalarEkrani.kt + VM    ⬜ 04
        ├── rehber/RehberEkrani.kt + VM              ⬜ 05
        ├── kisidetay/KisiDetayEkrani.kt + VM        ⬜ 06
        ├── arama/AramaEkrani.kt + VM                ⬜ 07
        ├── izineksik/IzinEksikAltSayfa.kt           ⬜ 12
        └── ayarlar/AyarlarEkrani.kt + VM            ⬜ 11
              HataKayitlariEkrani.kt, EngellenenlerEkrani.kt, HizliYanitlarEkrani.kt  ⬜
```

**Yeni dosya sadece bu ağaçta adı geçen yere açılır.** Gereken yardımcı fonksiyon, ait olduğu dosyanın içine `private` olarak yazılır.

## 3. Ekran şablonu (her ekran bu kalıpta)

```kotlin
// 1) Durum
data class TusTakimiUiState(val numara: String = "", val eslesme: Kisi? = null)

// 2) Olaylar
sealed interface TusTakimiOlay {
    data class TusaBasildi(val tus: Char) : TusTakimiOlay
    data object Sil : TusTakimiOlay
    data object Ara : TusTakimiOlay
}

// 3) ViewModel — sadece StateFlow yayınlar, Context tutmaz
class TusTakimiViewModel(private val rehber: RehberDeposu, ...) : ViewModel() {
    private val _durum = MutableStateFlow(TusTakimiUiState())
    val durum: StateFlow<TusTakimiUiState> = _durum.asStateFlow()
    fun olay(o: TusTakimiOlay) { ... }
    companion object { val Fabrika = viewModelFactory { initializer { TusTakimiViewModel(konteyner().rehber) } } }
}

// 4) Durumlu ekran
@Composable fun TusTakimiEkrani(vm: TusTakimiViewModel = viewModel(factory = TusTakimiViewModel.Fabrika)) {
    val durum by vm.durum.collectAsStateWithLifecycle()
    TusTakimiIcerik(durum, vm::olay)
}

// 5) Durumsuz içerik + 6) @Preview (koyu ve açık)
```

## 4. Arama akışı (telecom)

```
Kullanıcı "Ara" → AramaBaslatici.ara(numara)
   ├─ IzinYoneticisi.aramaIcinEksikler() boş değil → IzinEksikAltSayfa (12)
   └─ hepsi tamam → TelecomManager.placeCall(tel:numara)
                     ↓
Sistem → EymenInCallService.onCallAdded(call)
                     ↓
GorusmeYoneticisi.ekle(call)  →  StateFlow<List<GorusmeDurumu>>
                     ↓                         ↓
CallActivity açılır          GorusmeBildirimi güncellenir
 durum = DIALING   → GidenAramaEkrani (08)
 durum = RINGING   → GelenAramaEkrani (09)
 durum = ACTIVE/HOLDING → GorusmeEkrani (10)
 tüm aramalar bitti → CallActivity kendini kapatır (finishAndRemoveTask)
```

- **Telecom `Call` nesnesine sadece `GorusmeYoneticisi` dokunur.** Ekranlar `GorusmeYoneticisi.cevapla()`, `reddet()`, `kapat()`, `sessiz(Boolean)`, `beklet(Boolean)`, `sesYolu(Int)`, `dtmf(Char)` fonksiyonlarını çağırır.
- Sessiz/hoparlör `InCallService.setMuted / setAudioRoute` ile yapılır (`GorusmeYoneticisi` servise referans tutar).

## 5. Veri akışı
- Depolar `Flow` döndürür, `ContentObserver` ile sistem verisi değişince yeniden yayınlar.
- Tüm sağlayıcı sorguları `Dispatchers.IO`'da.
- İzin yoksa depo **boş liste** döndürür, çökmez (`SecurityException` → `HataKaydedici.kaydet`).
- Rehber 5000+ kişide akıcı olmalı: `LazyColumn` + `key = kisi.id`, avatar baş harfleri önceden hesaplanır.

## 6. Hata yönetimi
- `HataKaydedici.kur(app)` → yakalanmamış istisnaları dosyaya yazar (`files/hata_kayitlari/`), sonra sistemi çökertir.
- Yakalanan hatalar: `HataKaydedici.kaydet("Etiket", e)`.
- Son 7 günün kaydı tutulur, eskiler silinir.
- Ayarlar › Hata kayıtları ekranı bu dosyaları listeler.
