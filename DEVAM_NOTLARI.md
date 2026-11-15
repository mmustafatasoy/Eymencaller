# DEVAM NOTLARI — Claude'dan Codex'e devir (15 Kasım 2026)

> **GÜNCEL (v1.2 — 15 Kasım 2026):** `docs/07_V12_PLANI.md` içindeki **V12-01 … V12-13 hepsini Claude yazdı** (`[x]`).
> Son yeşil derleme: V12-09 (run 43699479656). V12-07 commit'i sonrası Actions'a bak; kırmızıysa ilk iş o hatayı
> HATA_GUNLUGU kuralıyla düzelt. Sıradaki numara **HATA-024**.
>
> **Codex'in v1.2 işi (yeni özellik YOK, sadece doğrulama/düzeltme):**
> 1. Actions yeşil mi? Değilse derleme hatalarını düzelt (sadece hatalı satır).
> 2. Cihazda `docs/mockups/v12/` görselleriyle karşılaştır: gelen arama (canlı ad + poster), görüşme (bulanık poster),
>    arama bitince "Arama bitti · süre", Kişiler (avatar + "N Kişi"), uzun basma önizleme, Yeni Kişi formu, Poster düzenleyici,
>    Ayarlar "Telefon", Engellenen Kişiler, izinler "Devam (x/n)". Fark → HATA kaydı.
> 3. Bilinen sınırlar (hata değil): Android 12 altında bulanıklık yok (sadece karartma); sistemin kendi Telefon/Rehber simgesi
>    Android tarafından yönlendirilmez (kullanıcı "Her zaman Eymen" seçerse kişi aç/ekle/düzenle bize gelir).
> 4. Bunlar bitince aşağıdaki v1.1 bölümü (GOREV-02/03/04).

> Claude'un günlük kullanım sınırı dolmak üzere. Bundan sonra işi **Codex** sürdürecek.
> Bu dosya **AGENTS.md ile birlikte** her oturumun başında okunur. Buradaki sıra ve sınırlar dışına çıkılmaz.

---

## 1. Şu anki durum (v1.1 — iOS tarzı)

| Bölüm | Durum |
|---|---|
| Tasarım (20 ekran görseli, açık + koyu) | ✅ `docs/mockups/` |
| Tüm ekranların kodu | ✅ Claude yazdı (65 Kotlin dosyası) |
| Arama motoru, bildirimler, engelleme | ✅ |
| Güvenli alan (kamera deliği + gezinme tuşları) | ✅ `ui/bilesenler/GuvenliAlan.kt` |
| Gerçek derleme (assembleDebug + lint) | ✅ GitHub Actions'ta başarılı (GOREV-01, HATA-018…020) — [43692314487](https://github.com/mmustafatasoy/Eymencaller/actions/runs/43692314487) |
| Telefonda deneme | ⏳ GOREV-03 |

v1.0 iskeleti 14 Kasım'da GitHub Actions'ta derlendi (HATA-001). Sürümler (AGP 8.10.1, Kotlin 2.1.21, Compose BOM 2025.05.01) **doğrulandı, değiştirilmez.**

---

## 2. Codex'in yapacakları (sırayla, tek tek)

1. ~~GOREV-01 · Derleme~~ ✅ tamam. Her yeni düzeltmeden sonra yine: GitHub Actions › **APK Derle** (her `main` push'unda kendiliğinden çalışır) ya da `./gradlew assembleDebug lintDebug`.
   Her hata → `HATA_GUNLUGU.md`'ye **önce AÇIK** (sıradaki numara **HATA-021**) → sadece o satırı düzelt → **DÜZELTİLDİ** yaz → commit `HATA-021: …`.
2. **GOREV-02 · Görsel karşılaştırma (SIRADAKİ):** APK'yı kur, her ekranı `docs/mockups/01…20` ile karşılaştır. Fark = HATA kaydı.
3. **GOREV-03 · Cihaz testi:** `docs/05_GOREV_PLANI.md` içindeki 20+ maddelik liste (Samsung Android 14 + Tecno Android 16). Güvenli alan maddeleri **ilk** test edilir.
4. **GOREV-04 · Sürüm APK'sı:** `assembleRelease` (R8) ile açılış ve arama denenir.

Her görev sonunda: kutu `[x]` → commit `GOREV-NN: …` → **dur**, kullanıcıya özet ver.

---

## 3. KODUN DIŞINA ÇIKMA — kesin sınırlar

**İzin verilen tek değişiklik:** derleme/lint/çalışma hatasını gideren **en küçük** düzeltme.

**YASAK:**
- Ekranları, bileşenleri, renkleri, yazıları, ölçüleri "iyileştirmek" ya da yeniden yazmak
- Yeni dosya, yeni ekran, yeni özellik, yeni kütüphane, sürüm yükseltme
- Dosya taşıma / yeniden adlandırma, paket yapısını değiştirme
- `statusBarsPadding`, `navigationBarsPadding`, `imePadding` ya da sabit üst/alt boşluk (güvenli alan **sadece** `IosSayfa` / `ustGuvenli` / `altGuvenli`)
- Kod içinde çıplak `Color(0xFF…)`, `16.dp`, metin (`"…"`) — hepsi `ui/theme/` ve `strings.xml`'de
- `res/drawable/ik_*.xml`'i elle değiştirmek (ikonlar: `design/ikonlar.py` → `python3 design/ikon_ceviri.py`)
- Boş `catch {}` — her hata `HataKaydedici.kaydet(...)`
- `INTERNET` izni, reklam, analitik, arama kaydı

---

## 4. Derlemede çıkabilecek hatalar ve İZİN VERİLEN düzeltmeler

Claude tam derleme yapamadığı için aşağıdaki API farkları çıkabilir. Bunlar için **sadece** yazılan düzeltme yapılır, yine HATA kaydı açılır:

| Olası hata | Dosya | İzin verilen düzeltme |
|---|---|---|
| `rememberSwipeToDismissBoxState(confirmValueChange=…)` imza/kullanımdan kaldırma | `SonAramalarEkrani.kt` | Material3 sürümündeki eşdeğer imzaya uyarla; davranış aynı kalsın (sola kaydır = sil) |
| `TextStyle(brush = …)` bulunamadı | `IosCagri.kt` | `EymenYazi.kaydir.copy(brush = …)` |
| `DialogProperties(usePlatformDefaultWidth=…)` | `IosUyari.kt` | Parametre adı uyarlanır, tam genişlik davranışı korunur |
| `HorizontalDivider` / `VerticalDivider` parametre sırası | birkaç dosya | İsimli parametre (`thickness =`, `color =`) yap |
| `Modifier.composed` kullanımdan kalktı uyarısı | `Tiklama.kt` | Uyarıdır, dokunma |
| `@OptIn` gereksiz / experimental uyarısı | birkaç dosya | Uyarıdır, dokunma |
| Lint `MissingPermission` | çekirdek/görüşme | Sadece izin `IzinYoneticisi` ile zaten kontrol ediliyorsa `@SuppressLint("MissingPermission")` |
| Lint `UnusedResources` / `Typos` / kullanılmayan import | — | Uyarıdır; hata değilse dokunma |
| `@Composable` lambda tür çıkarımı (`altBaslik = …`) | ekranlar | Lambda'nın önüne `@Composable` ekle ya da `val x: (@Composable () -> Unit)? = …` |

Listede olmayan bir hata ve emin değilsen: **düzeltme**, `HATA_GUNLUGU.md` › **SORULAR**'a `S-01: …` yaz, kullanıcıya bildir.

---

## 5. Claude'a geri dönülecek durumlar

- Aynı hata 2 kez düzeltilip yine çıkıyorsa
- Düzeltme bir ekranın görünümünü / davranışını değiştirecekse
- Telefonda çökme var ve `Ayarlar › Hata Kayıtları`'ndaki kayıt anlaşılmıyorsa (kaydı HATA_GUNLUGU'na kopyala)
- Kullanıcı yeni özellik isterse (Codex yeni özellik **yazmaz**)

Claude'a dönünce bu dosyayı ve `HATA_GUNLUGU.md`'yi göster yeter.

---

## 6. Codex'e yapıştırılacak mesaj

```
Bu depoda önce AGENTS.md ve DEVAM_NOTLARI.md dosyalarını baştan sona oku, sonra HATA_GUNLUGU.md
ve docs/05_GOREV_PLANI.md'yi oku. Kodun tamamı Claude tarafından yazıldı; tasarımı, dosya yapısını,
renkleri, metinleri ve sürümleri DEĞİŞTİRME, yeni özellik ekleme. Sadece sıradaki tek görevi
(docs/07_V12_PLANI.md içindeki ilk [ ] V12 görevi) yap. Her hata için önce HATA_GUNLUGU.md'ye HATA-021'den başlayarak AÇIK kayıt aç,
sadece o satırı düzelt (izin verilen düzeltmeler DEVAM_NOTLARI.md §4), sonra DÜZELTİLDİ yaz.
Derlemeyi GitHub Actions "APK Derle" ile doğrula. Görev bitince kutuyu [x] yap,
"V12-NN: ..." diye commit at ve dur; bana kısa özet ver.
```

Sonraki görevlerde sadece `GOREV-02` → `GOREV-03`, `GOREV-04` değişir.
