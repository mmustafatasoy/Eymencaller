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

## 2. Paket ağacı (kesin — v1.1 iOS tarzı, hepsi ✅ Claude tarafından yazıldı, 15 Kasım 2026)

```
app/src/main/java/com/atasoy/eymentelefon/
├── EymenTelefonUygulamasi.kt            Application: HataKaydedici + konteyner + bildirim kanalları
├── UygulamaKonteyneri.kt                Elle DI (tüm depolar tek örnek)
├── MainActivity.kt                      Açılış kararı, tel: / sekme intent'leri, durum çubuğu teması
│
├── cekirdek/
│   ├── hata/HataKaydedici.kt            Çökme + yakalanan hata kaydı (dosyaya, 7 gün)
│   ├── izin/IzinYoneticisi.kt           Tüm izin ve rol kontrolleri
│   ├── izin/IzinTanimi.kt               5 iznin tanımı (sıra, zorunluluk, metin)
│   ├── arama/AramaBaslatici.kt          Tek arama giriş noktası (+ sesli mesaj, görüntülü)
│   ├── ayar/AyarDeposu.kt               DataStore
│   └── yardimci/
│       ├── NumaraBicimleyici.kt         TR numara biçimi + normalize
│       ├── ZamanBicimleyici.kt          "20:31", "Pzt 11:20", "4 dk 12 sn", sayaç
│       ├── T9Eslestirici.kt             Türkçe T9 tablosu + puanlama
│       ├── TurkceMetin.kt               Türkçe sıralama / gruplama / aksan-duyarsız arama
│       └── DisIslemler.kt               Sistem intent'leri (mesaj, kişi ekle/düzenle, paylaş, kopyala)
│
├── (v1.2 eklenen dosyalar — docs/07_V12_PLANI.md)
│   ├── veri/poster/PosterDeposu.kt          Kişi Posteri (filesDir/posterler)
│   ├── veri/rehber/KisiYazici.kt            uygulama içi kişi ekle/düzenle (ContactsContract toplu işlem)
│   ├── gorusme/ui/KisiPosteri.kt            PosterFotografi · PosterKucukResim · CanliAd
│   ├── ui/ekranlar/poster/PosterEkrani.kt   poster düzenleyici (rota poster/{lookupKey})
│   ├── ui/ekranlar/kisiduzenle/KisiDuzenleEkrani.kt  Yeni Kişi / Düzenle (rota kisi-duzenle/{kisiId})
│   └── ui/ekranlar/rehber/KisiOnizleme.kt   uzun basma önizlemesi
├── veri/
│   ├── IcerikGozlemci.kt                ContentObserver → Flow
│   ├── model/Kisi.kt · AramaKaydi.kt
│   ├── rehber/RehberDeposu.kt           ContactsContract
│   ├── gecmis/AramaGecmisiDeposu.kt     CallLog
│   └── engel/EngelDeposu.kt             BlockedNumberContract
│
├── gorusme/                             (arama motoru)
│   ├── EymenInCallService.kt            Sistem aramaları buraya verir; bildirimi güncel tutar
│   ├── GorusmeYoneticisi.kt             Aktif aramalar StateFlow (tek kaynak)
│   ├── GorusmeDurumu.kt                 Arayüz için sade durum modeli
│   ├── GorusmeBildirimi.kt              Gelen (CallStyle + tam ekran), devam eden, cevapsız, hatırlatma
│   ├── GorusmeAlicisi.kt                GorusmeAlicisi (iç: reddet/kapat/hatırlat) + CevapsizAramaAlicisi (Telecom)
│   ├── EymenCallScreeningService.kt     Engelli numaraları çaldırmadan reddeder
│   └── ui/
│       ├── CallActivity.kt              Kilit ekranı üstü; kilitli mi?; yakınlık sensörü; ses tuşu zili susturur
│       ├── GorusmeViewModel.kt
│       └── AramaEkranlari.kt            11–16: GorusmeEkrani (giden + görüşme + tuşlar), GelenAramaEkrani (kilitli / açık)
│
└── ui/
    ├── theme/                           Renk.kt · Yazi.kt · Olcu.kt · Tema.kt (iOS renk/yazı/ölçü)
    ├── ikon/EymenIkon.kt                39 ikon (design/ikonlar.py → res/drawable/ik_*.xml)
    ├── navigasyon/Rotalar.kt · EymenNavHost.kt
    ├── bilesenler/                      GuvenliAlan · IosNavCubugu · IosListe · IosKontroller · IosSekmeCubugu
    │                                    IosTusTakimi · IosUyari · IosCagri · Tiklama (01_TASARIM_SISTEMI.md §4)
    └── ekranlar/
        ├── Ortak.kt                                       numara/arama türü metinleri, kalın soyad, kalın eşleşme
        ├── karsilama/KarsilamaEkrani.kt                   01
        ├── izinler/IzinlerEkrani.kt + IzinlerViewModel.kt 02
        ├── anaekran/AnaEkran.kt                           sekme iskeleti
        ├── favoriler/FavorilerEkrani.kt + VM              03
        ├── sonaramalar/SonAramalarEkrani.kt + VM          04, 05, 19
        ├── rehber/RehberEkrani.kt + VM                    06 (Kişiler)
        ├── kisidetay/KisiDetayEkrani.kt + VM              07
        ├── tuslar/TusTakimiEkrani.kt + VM                 08, 09
        ├── arama/AramaEkrani.kt + VM                      10
        ├── izineksik/IzinEksikUyarisi.kt                  17
        └── ayarlar/AyarlarEkrani.kt + VM + AyarAltEkranlari.kt   18 (+ hata kayıtları, engellenenler, hızlı yanıtlar)
```

**Yeni dosya açılmaz.** Değişiklik gerekiyorsa HATA_GUNLUGU › SORULAR'a yazılır.

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

// 5) Kök her zaman IosSayfa (güvenli alan) — bkz. 06_GUVENLI_ALAN.md
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
