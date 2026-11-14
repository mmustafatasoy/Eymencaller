# AGENTS.md — CODEX İÇİN KESİN KURALLAR (Eymen Telefon)

> Bu dosyayı **her görevden önce baştan sona oku.** Buradaki kurallar her şeyin üstündedir.
> Tasarım ve mimari Claude tarafından belirlenmiştir. Senin görevin **uygulamak**, değiştirmek değil.

---

## 0. Okuma sırası (her oturumda)

1. `AGENTS.md` (bu dosya)
2. `HATA_GUNLUGU.md` → açık hata var mı? Varsa **önce onu çöz.**
3. `docs/05_GOREV_PLANI.md` → sıradaki görev hangisi?
4. Görevin ilgili dokümanı: `docs/01_TASARIM_SISTEMI.md`, `docs/02_EKRANLAR.md`, `docs/03_MIMARI.md`, `docs/04_IZINLER.md`
5. İlgili tasarım görseli: `docs/mockups/NN_*.png`

---

## 1. YASAKLAR (kesinlikle yapma)

1. **Tasarımı değiştirme.** Renk, yazı boyutu, köşe yuvarlaklığı, boşluk, ikon, metin, ekran sırası → `docs/01_TASARIM_SISTEMI.md` ve mockup görselleri neyse o.
2. **Yeni renk / yeni dp değeri uydurma.** Sadece `ui/theme/` içindeki token'ları kullan (`EymenRenk`, `EymenGradyan`, `EymenBosluk`, `EymenBoyut`, `EymenSekil`, `EymenYazi`). İkonlar sadece `EymenIkonu(EymenIkon.Xxx, ...)`. Kod içinde `Color(0xFF...)`, `16.dp` gibi çıplak değer **YASAK** (tema dosyaları hariç).
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
3. Her ekran dosyası şu sırayla yazılır: `XxxEkrani(viewModel)` → `XxxIcerik(state, olaylar)` (durumsuz) → `@Preview` (koyu + açık tema).
4. Her `@Composable` ekranın önizlemesi mockup görseliyle **göz ile karşılaştırılır.** Fark varsa düzeltilir.
5. İzinler `IzinYoneticisi` üzerinden kontrol edilir. Bir ekranda doğrudan `ContextCompat.checkSelfPermission` çağırma.
6. Arama başlatmak yalnızca `AramaBaslatici.ara(numara)` ile yapılır (izin eksikse 12 numaralı alt sayfayı açar).
7. Her görev sonunda: `./gradlew assembleDebug` **ve** `./gradlew lint` çalıştır. İkisi de temiz değilse görev bitmemiştir.
8. Her görev sonunda `docs/05_GOREV_PLANI.md`'de ilgili kutuyu `[x]` yap ve commit at.
9. Commit mesajı formatı: `GOREV-NN: kısa açıklama` (hata düzeltmede `HATA-NNN: kısa açıklama`).
10. Minimum dokunma hedefi: tüm tıklanabilir öğeler ≥ 48dp. Her ikon butonunda `contentDescription` (strings.xml'den).

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
