# AGENTS.md — CODEX İÇİN KESİN KURALLAR (Eymen Telefon)

> Bu dosyayı **her görevden önce baştan sona oku.** Buradaki kurallar her şeyin üstündedir.
> Tasarım, mimari **ve kodun tamamı** Claude tarafından yazılmıştır (15 Kasım 2026).
> Senin görevin: **derlemek, test etmek, çıkan hataları kayıt kurallarıyla düzeltmek.**
> Yeni özellik, yeni ekran, yeni dosya, yeniden yazma (refactor) **YOK.** Böyle bir ihtiyaç → `HATA_GUNLUGU.md › SORULAR`.
> **Tek istisna:** `docs/07_V12_PLANI.md` içindeki V12-xx görevleri (kullanıcı onaylı, Claude tanımlı). Sadece orada yazanlar, orada yazdığı gibi.

---

## 0. Okuma sırası (her oturumda)

1. `AGENTS.md` (bu dosya) + **`DEVAM_NOTLARI.md`** (Claude'dan devir: durum, sıra, izin verilen düzeltmeler)
2. `HATA_GUNLUGU.md` → açık hata var mı? Varsa **önce onu çöz.**
3. `docs/05_GOREV_PLANI.md` → sıradaki görev hangisi?
4. Görevin ilgili dokümanı: `docs/01_TASARIM_SISTEMI.md`, `docs/02_EKRANLAR.md`, `docs/03_MIMARI.md`, `docs/04_IZINLER.md`
5. **v1.2:** `docs/07_V12_PLANI.md` (onaylı görev listesi) + `docs/mockups/v12/` (tek geçerli tasarım; `arsiv_v11/` eskidir)

---

## 1. YASAKLAR (kesinlikle yapma)

0. **Çalışan kodu yeniden yazma.** Sadece hatanın olduğu satır(lar) düzeltilir. "Daha iyi olur" diye değişiklik yasak.
1. **Tasarımı değiştirme.** Renk, yazı boyutu, köşe yuvarlaklığı, boşluk, ikon, metin, ekran sırası → `docs/01_TASARIM_SISTEMI.md` ve mockup görselleri neyse o.
2. **Yeni renk / yeni dp değeri uydurma.** Sadece `ui/theme/` içindeki token'ları kullan (`EymenRenk`, `KareRenk`, `AramaEkraniRenk`, `AvatarRenk`, `EymenBosluk`, `EymenBoyut`, `EymenSekil`, `EymenDolgu`, `EymenYazi`). İkonlar sadece `EymenIkonu(EymenIkon.Xxx, ...)`. Kod içinde `Color(0xFF...)`, `16.dp` gibi çıplak değer **YASAK** (tema dosyaları hariç).
3. **Paket yapısını değiştirme.** Dosya taşıma, yeniden adlandırma, yeni üst paket açma yok. Yeni dosya yalnızca `docs/03_MIMARI.md`'de adı geçen yere eklenir.
4. **Yeni kütüphane ekleme.** `gradle/libs.versions.toml`'daki listenin dışına çıkma. (Hilt, Room, Koin, Accompanist, Glide vb. EKLEME.)
5. **Sürüm yükseltme yok.** AGP, Kotlin, Compose BOM, SDK sürümlerine dokunma. Derleme bu sürümlerle hata verirse → HATA_GUNLUGU'na yaz, sürümü değiştirmeden çöz.
6. **Kural dosyalarını değiştirme:** `AGENTS.md`, `docs/*.md`, `docs/mockups/*`, `design/*` salt okunurdur. Tek istisna: `HATA_GUNLUGU.md` ve `docs/05_GOREV_PLANI.md` içindeki onay kutuları `[ ]` → `[x]`.
7. **Görevi atlama / birleştirme yok.** `05_GOREV_PLANI.md` sırasıyla, bir seferde **tek görev**.
8. **Reklam, analitik, internet izni, takip kodu yok.** `INTERNET` izni manifestte YOKTUR ve eklenmez.
9. **Arama kaydı (ses kaydı) özelliği yok.** Hiçbir koşulda ekleme.
10. **`TODO`, sahte veri veya boş `onClick = {}` bırakarak görevi "bitti" sayma.** (Mockup önizlemeleri `@Preview` içindeki örnek veri hariç.)
11. **Hata yutma yok:** `catch (e: Exception) {}` boş blok YASAK. Her yakalanan hata `HataKaydedici.kaydet(...)` ile kaydedilir.
12. **Metinleri koda gömme.** Tüm kullanıcı metinleri `res/values/strings.xml`'de. Metinler mockup'takiyle birebir aynı.

---

## 2. ZORUNLULAR (her zaman yap)

1. Dil: **Kotlin**, arayüz: **Jetpack Compose + Material 3**. XML layout YOK.
2. Mimari: **MVVM + tek yönlü veri akışı** (`UiState` data class + `ViewModel` + `StateFlow`). Ayrıntı: `docs/03_MIMARI.md`.
3. **Güvenli alan:** Her ekranın kökü `IosSayfa` (ya da arama ekranında `AramaZemini`). `statusBarsPadding`, `navigationBarsPadding`, `imePadding` ve sabit üst/alt boşluk YASAK. Ayrıntı: `docs/06_GUVENLI_ALAN.md`.
4. Her ekran cihazda `docs/mockups/` içindeki görseliyle **göz ile karşılaştırılır.** Fark varsa HATA olarak kaydedilir.
5. İzinler `IzinYoneticisi` üzerinden kontrol edilir. Bir ekranda doğrudan `ContextCompat.checkSelfPermission` çağırma.
6. Arama başlatmak yalnızca `AramaBaslatici.ara(numara)` ile yapılır (izin eksikse 17 numaralı uyarıyı açar).
6b. İkon eklemek/değiştirmek: sadece `design/ikonlar.py` düzenlenir, sonra `python3 design/ikon_ceviri.py` çalıştırılır. `ik_*.xml` elle değiştirilmez.
7. Her görev sonunda: `./gradlew assembleDebug` **ve** `./gradlew lint` çalıştır. İkisi de temiz değilse görev bitmemiştir.
8. Her görev sonunda `docs/05_GOREV_PLANI.md`'de ilgili kutuyu `[x]` yap ve commit at.
9. Commit mesajı formatı: `GOREV-NN: kısa açıklama` (hata düzeltmede `HATA-NNN: kısa açıklama`).
10. Minimum dokunma hedefi: tüm tıklanabilir öğeler ≥ 44dp (iOS ölçüsü). Her ikon butonunda `contentDescription` (strings.xml'den).

---

## 3. HATA GÜNLÜĞÜ KURALI (çok önemli)

Bir hata ile karşılaştığında (derleme hatası, lint hatası, çökme, yanlış davranış, tasarımdan sapma):

**ADIM A — Önce not et.** Düzeltmeye başlamadan ÖNCE `HATA_GUNLUGU.md` → "AÇIK HATALAR" bölümüne şu şablonla ekle:

```
### HATA-001 · AÇIK
- Tarih: 2026-11-15 14:20
- Görev: GOREV-04
- Yer: app/src/main/java/.../TusTakimiEkrani.kt:87
- Belirti: (ne oldu? hata mesajının ilk 3 satırı)
- Neden (tahmin): ...
```

**ADIM B — Düzelt.** Sadece hatanın olduğu yeri düzelt. Tasarımı veya mimariyi bozan "kestirme" çözüm YASAK.

**ADIM C — Düzelttiğini yaz.** Düzeltme derlenip doğrulandıktan SONRA kaydı "AÇIK HATALAR"dan kes, "DÜZELTİLEN HATALAR" bölümünün **en üstüne** taşı ve şöyle güncelle:

```
### HATA-001 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 14:20 → Düzeltme: 2026-11-15 14:41
- Görev: GOREV-04
- Yer: app/src/main/java/.../TusTakimiEkrani.kt:87
- Belirti: ...
- Gerçek neden: ...
- Yapılan düzeltme: (hangi dosyada ne değişti, 1-3 madde)
- Doğrulama: assembleDebug ✅ · lint ✅ · (varsa) cihazda denendi ✅
- Commit: HATA-001: ...
```

Kurallar:
- Numara artan gider (HATA-001, HATA-002…). Numara asla tekrar kullanılmaz, kayıt asla silinmez.
- Aynı hata tekrar ederse yeni numara aç ve "Tekrar: HATA-00X" yaz.
- Çözemediğin hata "AÇIK" kalır ve altına `- Durum: ÇÖZÜLEMEDİ — Claude'a sor` yazılır. Bu durumda yeni göreve GEÇME.
- Uygulamanın kendi içindeki çökme kayıtları (`HataKaydedici`) ayrı bir şeydir; Ayarlar › Hata kayıtları ekranında görünür. Kullanıcı bir çökme bildirirse o kaydı da HATA_GUNLUGU'na aktar.

---

## 4. Emin olmadığında

Belgede yazmayan bir karar gerekiyorsa **tahmin etme.** `HATA_GUNLUGU.md` sonundaki "SORULAR" bölümüne yaz, o kısmı yapmadan bırak ve sıradaki maddeye geç. Soru: `S-01: ... (GOREV-NN)` formatında.
