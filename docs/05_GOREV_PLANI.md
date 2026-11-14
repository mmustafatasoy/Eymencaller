# 05 · Görev Planı (sırayla, tek tek)

> Kurallar: bir seferde **tek görev**. Her görev sonunda `assembleDebug` + `lint` temiz → kutuyu `[x]` yap → commit `GOREV-NN: ...`.
> Açık hata varken yeni göreve geçme (`HATA_GUNLUGU.md`).

---

### [ ] GOREV-01 · İskeleti derle
- Projeyi olduğu gibi derle: `./gradlew assembleDebug`.
- Hazır (✅) dosyalara dokunma. Derleme hatası varsa HATA_GUNLUGU'na yaz, en küçük değişiklikle düzelt.
- **Kabul:** APK oluşuyor, uygulama açılıyor, karşılama yerine geçici metin görünüyor.

### [ ] GOREV-02 · Bileşenler
- `ui/bilesenler/` altına `01_TASARIM_SISTEMI.md §5`'teki 15 bileşeni yaz (her biri ayrı dosya + `@Preview`).
- İkonlar sadece `EymenIkon` üzerinden.
- **Kabul:** Her bileşenin önizlemesi mockup'taki karşılığıyla aynı ölçü/renkte.

### [ ] GOREV-03 · Karşılama + İzinler (01, 02)
- `KarsilamaEkrani`, `IzinlerEkrani`, `IzinlerViewModel`. `docs/04_IZINLER.md`'deki tüm durumlar.
- "Ayar modu" parametresi (Ayarlar'dan açılınca üstte geri oku, alt buton "Tamam").
- **Kabul:** Temiz kurulumda: Karşılama → İzinler → 5 izin verilince "Devam et" aktif → Ana ekran. Bir izni ayarlardan kaldırıp dönünce yine İzinler açılıyor.

### [ ] GOREV-04 · Ana ekran iskeleti + Tuş takımı (03)
- `AnaEkran` (4 sekme, `EymenAltMenu`), `TusTakimiEkrani` + VM, `NumaraBicimleyici`.
- T9 eşleşme kartı, DTMF ses, titreşim, uzun basma davranışları.
- **Kabul:** `0532…` yazınca biçimleniyor, rehberdeki kişi kartta çıkıyor, Ara → `AramaBaslatici`.

### [ ] GOREV-05 · Depolar
- `RehberDeposu`, `AramaGecmisiDeposu`, `EngelDeposu` (Flow + ContentObserver, IO thread, izin yoksa boş liste).
- **Kabul:** 2000 kişilik rehberde ilk yükleme < 500ms (Logcat'e süre yaz, sonra kaldır).

### [ ] GOREV-06 · Son aramalar (04)
- Filtreler, gruplama, (n) birleştirme, uzun bas menüsü, FAB.
- **Kabul:** Cevapsızlar kırmızı, sayıları çipte doğru.

### [ ] GOREV-07 · Rehber + Favoriler (05)
- Türkçe sıralama, yapışkan harf başlıkları, harf dizini, Benim kartım, daralan FAB, Favoriler sekmesi.
- **Kabul:** "Çağla" C'den sonra Ç başlığında; "İsmail" I'dan sonra İ başlığında.

### [ ] GOREV-08 · Kişi detayı (06)
- **Kabul:** Ara/Mesaj/E-posta/Paylaş/Engelle çalışıyor; olmayan alanların butonu gizli.

### [ ] GOREV-09 · Arama ekranı (07)
- Türkçe duyarsız arama, vurgulama, numara satırı, 150ms debounce.
- **Kabul:** "ay" yazınca "Ayşe" ve "Aydın" vurgulu; "ISIK" yazınca "Işık" bulunuyor.

### [ ] GOREV-10 · İzin eksik alt sayfası (12)
- **Kabul:** Telefon izni kaldırılmışken Ara → alt sayfa; izin verilince arama kendiliğinden başlıyor.

### [ ] GOREV-11 · Arama ekranları (08, 09, 10) + bildirim
- `GorusmeViewModel`, 3 ekran, `GorusmeBildirimi` (CallStyle), kaydırarak cevapla, DTMF, yakınlık sensörü, ses yolu.
- **Kabul (gerçek cihazda):** giden arama → 08 → açılınca 10; gelen arama ekran kilitliyken 09 tam ekran; reddet/cevapla/kapat/sessiz/hoparlör/beklet çalışıyor; arama bitince ekran kapanıyor.

### [ ] GOREV-12 · Engelleme
- `EymenCallScreeningService` + Ayarlar › Engellenen numaralar.
- **Kabul:** Engelli numara aradığında hiç çalmıyor, Son aramalarda "Engellendi" olarak görünüyor.

### [ ] GOREV-13 · Ayarlar (11) + alt sayfalar
- Tema, vurgu rengi (anında uygulanır), titreşim, tuş sesi, hızlı yanıtlar, hata kayıtları, hakkında.
- **Kabul:** Açık tema seçilince tüm ekranlar (arama ekranları hariç) açık tema oluyor.

### [ ] GOREV-14 · Son kontrol
- Tüm ekranları mockup'larla yan yana karşılaştır; farkları HATA olarak kaydet ve düzelt.
- Yazı ölçeği 1.3x, açık tema, 5000 kişi, çift SIM testleri.
- `./gradlew lint` 0 hata. `assembleRelease` imzasız APK oluşuyor.
- **Kabul:** HATA_GUNLUGU'nda açık hata yok.
