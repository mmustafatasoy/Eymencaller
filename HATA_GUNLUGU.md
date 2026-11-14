# HATA GÜNLÜĞÜ — Eymen Telefon

> Kurallar: `AGENTS.md` › Bölüm 3. Önce **not et**, sonra **düzelt**, sonra **düzelttiğini yaz**.
> Kayıtlar silinmez. Numaralar tekrar kullanılmaz. Son kullanılan numara: **HATA-001**

---

## 🔴 AÇIK HATALAR

### HATA-001 · AÇIK
- Tarih: 2026-11-14 21:10
- Görev: GOREV-01
- Yer: `gradle/wrapper/gradle-wrapper.properties` / derleme ortamı
- Belirti: `./gradlew assembleDebug` ve `./gradlew lint` çalışırken Gradle 8.14.3 indirilemedi. `java.net.UnknownHostException: services.gradle.org`.
- Neden (tahmin): Bu çalışma ortamının internet/DNS erişimi bulunmuyor. Bu, kaynak kodunda doğrulanmış bir hata değil.
- Durum: ÇÖZÜLEMEDİ — Android SDK 36 / JDK 17 içeren erişilebilir bir ortamda veya GitHub Actions ile yeniden denenmeli.


<!-- ŞABLON — kopyala, doldur:
### HATA-00X · AÇIK
- Tarih: YYYY-AA-GG SS:DD
- Görev: GOREV-NN
- Yer: dosya/yolu.kt:satır
- Belirti:
- Neden (tahmin):
-->

---

## ✅ DÜZELTİLEN HATALAR

_(En yeni en üstte.)_

<!-- ŞABLON:
### HATA-00X · ✅ DÜZELTİLDİ
- Tarih: ... → Düzeltme: ...
- Görev: GOREV-NN
- Yer:
- Belirti:
- Gerçek neden:
- Yapılan düzeltme:
- Doğrulama: assembleDebug ✅ · lint ✅ · cihazda ✅
- Commit: HATA-00X: ...
-->

---

## ❓ SORULAR (Claude'a)

_(Belgede cevabı olmayan kararlar buraya: `S-01: ... (GOREV-NN)`)_
