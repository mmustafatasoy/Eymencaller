# HATA GÜNLÜĞÜ — Eymen Telefon

> Kurallar: `AGENTS.md` › Bölüm 3. Önce **not et**, sonra **düzelt**, sonra **düzelttiğini yaz**.
> Kayıtlar silinmez. Numaralar tekrar kullanılmaz. Son kullanılan numara: **HATA-022**

---

## 🔴 AÇIK HATALAR

_(Şu an açık hata yok.)_

<!-- ŞABLON — kopyala, doldur:
### HATA-0XX · AÇIK
- Tarih: YYYY-AA-GG SS:DD
- Görev: GOREV-NN
- Yer: dosya/yolu.kt:satır
- Belirti:
- Neden (tahmin):
-->

---

## ✅ DÜZELTİLEN HATALAR

### HATA-022 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 12:44 → Düzeltme: 2026-11-15 12:55
- Görev: V12-06 (kullanıcı cihaz testi, Tecno, giden arama ekran görüntüsü)
- Yer: gorusme/ui/AramaEkranlari.kt (GorusmeEkrani), gorusme/ui/KisiPosteri.kt
- Belirti: Posterli kişi aranınca arka plan çok bulanık/çirkin, ad normal yazı, efekt görünmüyor.
- Gerçek neden: Görüşme/giden ekranında poster 18dp bulanıklaştırılıyordu; canlı ad sadece gelen aramadaydı; Ken Burns çok yavaştı (bulanıkken hiç fark edilmiyordu).
- Yapılan düzeltme: poster net (sadece tuş takımı açıkken bulanık); posterli kişide giden/görüşme ekranında da dev `CanliAd` + durum metni, küçük yuvarlak resim kaldırıldı; ada soldan sağa parıltı + harf büyümesi ve gölge; Ken Burns 12 sn, %6→%16 + yatay kayma.
- Doğrulama: CI derlemesi; cihazda kullanıcı tekrar deneyecek
- Commit: HATA-022: arama ekranında net poster + canlı parıltılı ad

### HATA-021 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 09:13 → Düzeltme: 2026-11-15 09:25
- Görev: V12-03 (Claude)
- Yer: app/src/main/res/values/strings.xml:185 (`ayar_bilinmeyen_dipnot`)
- Belirti: CI `mergeDebugResources` → "Invalid unicode escape sequence in string" (run 43698136866)
- Gerçek neden: Metindeki kesme işareti (`Aramalar'a`) kaçışsız yazılmış.
- Yapılan düzeltme: `'` → `\'`
- Doğrulama: CI derlemesi (sonraki çalışma)
- Commit: HATA-021: strings.xml kesme işareti kaçışı

_(En yeni en üstte. HATA-001: Codex (v1.0 iskelet derlemesi) · HATA-002…013: Claude'un eksik taraması · HATA-014…017: v1.1 iOS yeniden tasarımı, 15 Kasım 2026.)_

### HATA-020 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 10:57 → Düzeltme: 2026-11-15 11:05 (Claude)
- Görev: GOREV-01
- Yer: app/src/main/java/.../ui/ekranlar/tuslar/TusTakimiEkrani.kt:129
- Belirti: `RowScope.AnimatedVisibility … cannot be called in this context with an implicit receiver`
- Gerçek neden: `AnimatedVisibility`, Row içindeki Box'ta çağrıldı; derleyici Row'un sürümünü seçti.
- Yapılan düzeltme: Sil butonu `animateFloatAsState` + `alpha` ile beliriyor (görünüm aynı).
- Doğrulama: GitHub Actions (aşağıdaki commit) · Commit: HATA-018/019/020: CI derleme hataları

### HATA-019 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 10:57 → Düzeltme: 2026-11-15 11:05 (Claude)
- Görev: GOREV-01
- Yer: app/src/main/java/.../ui/ekranlar/arama/AramaEkrani.kt:80-90
- Belirti: `Too many arguments for 'fun bicimle(numara: String)'` · `Unresolved reference 'metin'`
- Gerçek neden: Bir satırdan sonra gelen `@Composable { … }` bloğu, önceki satırdaki `bicimle(...)` çağrısının son-lambda'sı sanıldı.
- Yapılan düzeltme: Alt metin önce `numaraMetni` değişkeninde hesaplanıyor, lambda tek ifade.

### HATA-018 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 10:57 → Düzeltme: 2026-11-15 11:05 (Claude)
- Görev: GOREV-01
- Yer: app/src/main/java/.../ui/bilesenler/GuvenliAlan.kt:23-32
- Belirti: `Functions which invoke @Composable functions must be marked with the @Composable annotation`
- Gerçek neden: `WindowInsets.safeDrawing` bir @Composable özellik; normal Modifier fonksiyonundan okunamaz.
- Yapılan düzeltme: `ustGuvenli/altGuvenli/yatayGuvenli` → `Modifier.composed { … }` (davranış aynı).

### HATA-017 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: gorusme/ui/CallActivity.kt
- Belirti: Gelen aramada ekran kilitli olsun olmasın hep kaydırma rayı vardı.
- Yapılan düzeltme: `KeyguardManager.isKeyguardLocked` ile ayrım: kilitliyken "kaydırarak cevapla", açıkken Reddet / Kabul Et (iOS gibi).

### HATA-016 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: res/values/themes.xml, res/values-night/colors.xml
- Belirti: Açılış ekranı ve pencere zemini her temada lacivertti; açık temada uygulama açılırken koyu bir an görünüyordu.
- Yapılan düzeltme: Pencere/açılış zemini temaya göre (açık: beyaz, koyu: siyah).

### HATA-015 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: gorusme/ui/CallActivity.kt
- Belirti: Arama ekranında (her zaman koyu) açık sistem temasında durum çubuğu ikonları siyah kalıyordu.
- Yapılan düzeltme: `enableEdgeToEdge(SystemBarStyle.dark)` → ikonlar beyaz.

### HATA-014 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: tüm ekranlar → ui/bilesenler/GuvenliAlan.kt, res/values/themes.xml
- Belirti (kullanıcı bildirdi): Uygulama bazı cihazlarda **gezinme tuşlarının altında** kalıyor ya da **kameranın üstüne** çiziliyordu.
- Gerçek neden: Kenardan kenara çizimde sadece `statusBarsPadding`/`navigationBarsPadding` kullanılıyordu; kamera kesiği (displayCutout) ve kilit ekranı/yatay mod hesaba katılmıyordu.
- Yapılan düzeltme: Tüm ekranlar `IosSayfa` → `WindowInsets.safeDrawing` (durum çubuğu ∪ kamera kesiği ∪ gezinme ∪ klavye); tema `windowLayoutInDisplayCutoutMode=shortEdges`; sekme çubuğu zemini gezinme tuşlarının arkasına uzanıp ikonları üstte tutuyor. Ayrıntı: `docs/06_GUVENLI_ALAN.md`.
- Doğrulama: kod incelemesi ✅ · cihazda GOREV-03

### HATA-013 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: AndroidManifest.xml, gorusme/GorusmeAlicisi.kt
- Belirti: Bildirim eylemlerini (reddet / kapat) alan alıcı dışarıya açık olacaktı.
- Gerçek neden: Cevapsız arama yayını için alıcının `exported=true` olması gerekiyor; aynı alıcı iç eylemleri de alıyordu → başka bir uygulama aramayı kapatabilirdi.
- Yapılan düzeltme: İki alıcıya bölündü: `GorusmeAlicisi` (exported=false) ve sadece cevapsız aramayı işleyen `CevapsizAramaAlicisi` (exported=true).
- Doğrulama: manifest + kod incelemesi ✅

### HATA-012 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: MainActivity.kt
- Belirti: Telefon koyu temadayken uygulamada "Açık" tema seçilince durum çubuğu ikonları beyaz kalıp okunmuyordu.
- Yapılan düzeltme: Seçilen temaya göre `enableEdgeToEdge(SystemBarStyle.light/dark)`.
- Doğrulama: kod incelemesi ✅ (cihazda GOREV-03)

### HATA-011 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: gorusme/GorusmeAlicisi.kt, gorusme/GorusmeBildirimi.kt
- Belirti: Gelen aramadaki "Hatırlat" butonunun arkasında alarm/bildirim yoktu.
- Yapılan düzeltme: `hatirlatKur()` (10 dk, kesin olmayan alarm, ek izin gerekmez) + `hatirlatGoster()`; dokununca Tuşlar'da numara dolu.

### HATA-010 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: gorusme/ui/CallActivity.kt
- Belirti: Telefon çalarken ses tuşları zili susturmuyordu.
- Yapılan düzeltme: `onKeyDown` → `TelecomManager.silenceRinger()`.

### HATA-009 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: gorusme/ui/CallActivity.kt
- Belirti: Görüşmede telefon kulağa götürülünce ekran açık kalıyordu (yanak ile tuşlara basılabilir).
- Yapılan düzeltme: Aktif görüşmede + ses telefon hoparlöründeyken `PROXIMITY_SCREEN_OFF_WAKE_LOCK`; görüşme bitince bırakılır.

### HATA-008 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: MainActivity.kt, gorusme/ui/GorusmeEkrani.kt
- Belirti: Görüşme sırasında "Arama ekle" ve "Kişiler" uygulamanın doğru sekmesini açamıyordu.
- Yapılan düzeltme: `MainActivity.sekmeIntenti()` + `EK_SEKME` ekstrası; uygulama açıkken de yönlendirme.

### HATA-007 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: cekirdek/arama/AramaBaslatici.kt
- Belirti: Kişi detayındaki "Görüntülü" butonu için destek kontrolü ve görüntülü arama yolu yoktu.
- Yapılan düzeltme: `goruntuluDestekleniyor()` + `ara(..., goruntulu = true)`; destek yoksa buton gizli.

### HATA-006 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: cekirdek/arama/AramaBaslatici.kt
- Belirti: Tuş takımında "1" uzun basış (sesli mesaj) için fonksiyon yoktu.
- Yapılan düzeltme: `sesliMesaj()` (izin kontrolü dahil).

### HATA-005 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: EymenTelefonUygulamasi.kt
- Belirti: Bildirim kanalları sadece arama servisi çalışırken oluşacaktı; cevapsız arama bildirimi kanal olmadan düşmezdi.
- Yapılan düzeltme: Kanallar uygulama açılışında oluşturuluyor.

### HATA-004 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: gorusme/GorusmeBildirimi.kt, gorusme/ui/CallActivity.kt
- Belirti: Bildirimdeki "Cevapla" yayın (broadcast) üzerinden activity açacaktı → Android 12+ "trampoline" yasağına takılır.
- Yapılan düzeltme: "Cevapla" doğrudan `CallActivity`'yi `EYLEM_CEVAPLA` ile açıyor; cevaplama orada yapılıyor.

### HATA-003 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: gorusme/GorusmeBildirimi.kt, gorusme/EymenInCallService.kt
- Belirti: Ekran kapalı/kilitliyken gelen arama görünmeyebilirdi; uygulamadan çıkınca devam eden arama bildirimi yoktu.
- Yapılan düzeltme: Gelen arama: `CallStyle` + tam ekran intent. Devam eden: süre sayaçlı bildirim + "Aramayı bitir". Servis arama durumunu izleyip bildirimi günceller.

### HATA-002 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-15 → Düzeltme: 2026-11-15 (Claude)
- Yer: AndroidManifest.xml, gorusme/GorusmeAlicisi.kt
- Belirti: Uygulama varsayılan telefon uygulaması olunca **cevapsız arama bildirimi hiç görünmeyecekti.**
- Gerçek neden: Android bu bildirimi varsayılan uygulamaya bırakır (`ACTION_SHOW_MISSED_CALLS_NOTIFICATION`); alıcı yoktu.
- Yapılan düzeltme: `CevapsizAramaAlicisi` + `GorusmeBildirimi.cevapsizGoster()`; Son aramalar açılınca kapanır.

### HATA-001 · ✅ DÜZELTİLDİ
- Tarih: 2026-11-14 21:10 → Düzeltme: 2026-11-14 (Codex)
- Görev: GOREV-01 (v1.0 iskeleti)
- Yer: `gradle/wrapper/gradle-wrapper.properties` / derleme ortamı
- Belirti: Yerel ortamda Gradle 8.14.3 indirilemedi (`java.net.UnknownHostException: services.gradle.org`).
- Gerçek neden: Çalışma ortamının Gradle sunucusuna ağ erişimi yoktu; kaynak koddan kaynaklanan bir hata değildi.
- Yapılan düzeltme: Kod ve sürümler değiştirilmeden derleme GitHub Actions'ta doğrulandı ([Actions #43628558227](https://github.com/mmustafatasoy/Eymencaller/actions/runs/43628558227), SHA `c3ed9c1`).
- Doğrulama: `assembleDebug` ✅ · `lintDebug` ✅ · APK artifact ✅
- Commit: HATA-001: GitHub Actions ile derleme engeli doğrulandı

<!-- ŞABLON:
### HATA-0XX · ✅ DÜZELTİLDİ
- Tarih: ... → Düzeltme: ...
- Görev: GOREV-NN
- Yer:
- Belirti:
- Gerçek neden:
- Yapılan düzeltme:
- Doğrulama: assembleDebug ✅ · lint ✅ · cihazda ✅
- Commit: HATA-0XX: ...
-->

---

## ❓ SORULAR (Claude'a)

_(Belgede cevabı olmayan kararlar buraya: `S-01: ... (GOREV-NN)`)_
