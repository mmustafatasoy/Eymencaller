# HATA GÜNLÜĞÜ — Eymen Telefon

> Kurallar: `AGENTS.md` › Bölüm 3. Önce **not et**, sonra **düzelt**, sonra **düzelttiğini yaz**.
> Kayıtlar silinmez. Numaralar tekrar kullanılmaz. Son kullanılan numara: **HATA-002**

---

## 🔴 AÇIK HATALAR

### HATA-002 · AÇIK
- Tarih: 2026-11-15
- Görev: GOREV-02
- Yer: `ui/bilesenler/EymenCip.kt`, `ui/bilesenler/EymenAramaCubugu.kt`
- Belirti: Tıklanabilir çipler ve arama çubuğundaki 30dp profil avatarı için asgari 48dp dokunma hedefi garanti edilmiyor (`AGENTS.md` §2.10).
- Neden (tahmin): Görsel bileşene doğrudan `Modifier.clickable` uygulanması, görselin dokunma alanını büyütmüyor.
- Durum: Düzeltme hazırlanıyor; `assembleDebug` ve `lint` GOREV-02 dalında henüz doğrulanmadı.


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

### HATA-001 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-14 21:10 → Düzeltme: 2026-11-14
- Görev: GOREV-01
- Yer: `gradle/wrapper/gradle-wrapper.properties` / derleme ortamı
- Belirti: Yerel ortamda Gradle 8.14.3 indirilemedi (`java.net.UnknownHostException: services.gradle.org`).
- Gerçek neden: Önceki çalışma ortamının Gradle dağıtım sunucusuna DNS/ağ erişimi yoktu; uygulama kaynak kodundan kaynaklanan bir derleme hatası değildi.
- Yapılan düzeltme:
  - Kaynak kodunu veya bağımlılık sürümlerini değiştirmeden GitHub Actions ortamında derleme ve lint doğrulandı.
  - [Actions #43628558227](https://github.com/mmustafatasoy/Eymencaller/actions/runs/43628558227), `main`, SHA `c3ed9c15b8bbbf9099419ab87788dcd6d9d6ab02`.
- Doğrulama: `assembleDebug` ✅ · `lintDebug` ✅ · `EymenTelefon-debug` APK artifact ✅ · fiziksel cihazda açılış henüz kontrol edilmedi.
- Commit: HATA-001: GitHub Actions ile derleme engeli doğrulandı


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

S-01: GOREV-04 — `docs/02_EKRANLAR.md` uzun 1 basmayı `voicemail:` URI ile ister; ancak değiştirilemez `AramaBaslatici.temizle()` harfli URI'yi çıkarır. Şimdilik cihazın `TelephonyManager.voiceMailNumber` değeri varsa bu numara `AramaBaslatici.ara()` ile aranıyor, yoksa tuşa 1 ekleniyor. Tam `voicemail:` davranışı için hazır `AramaBaslatici` içinde istisna tanımlanmasına izin var mı? (GOREV-04)
