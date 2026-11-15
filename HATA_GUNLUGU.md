# HATA GÜNLÜĞÜ — Eymen Telefon

> Kurallar: `AGENTS.md` › Bölüm 3. Önce **not et**, sonra **düzelt**, sonra **düzelttiğini yaz**.
> Kayıtlar silinmez. Numaralar tekrar kullanılmaz. Son kullanılan numara: **HATA-007**

---

## 🔴 AÇIK HATALAR

### HATA-007 · AÇIK
- Tarih: 2026-11-15
- Görev: GOREV-11
- Yer: `gorusme/EymenInCallService.kt` ve `gorusme/GorusmeBildirimi.kt`
- Belirti: Mevcut `onCallAdded` gelen aramayı her durumda `CallActivity.ac()` ile açıyor; `CallStyle` heads-up / fullScreenIntent tercihi işletilemiyor.
- Neden: GOREV-01 iskeleti, bildirim eklenmeden önce her aramada ekranı doğrudan açıyordu.
- Durum: Bildirim izni ve FSI kontrolüyle yalnızca gereken durumda doğrudan ekranı açma düzeltmesi hazırlanıyor; APK/lint/cihaz testi henüz yok.

### HATA-006 · AÇIK
- Tarih: 2026-11-15
- Görev: GOREV-11
- Yer: `gorusme/ui/GidenAramaEkrani.kt`
- Belirti: Giden arama ekranında Tuşlar kontrolü durum değiştiriyor fakat DTMF tuş takımını göstermiyor.
- Neden: Görsel kontrolde `durum.tuslarAcik` dalı henüz çizilmemişti.
- Durum: Kod düzeltmesi hazırlanıyor; `assembleDebug` ve `lint` doğrulaması yapılmadı.

### HATA-005 · AÇIK
- Tarih: 2026-11-15
- Görev: GOREV-08
- Yer: `ui/ekranlar/kisidetay/KisiDetayEkrani.kt` koyu ve açık `@Preview`.
- Belirti: `KisiDetayIcerik` 12 olay parametresi bekliyor; önizlemelerde 13 boş lambda verilmiş. Bu, derleme parametre sayısı hatası doğurur.
- Neden: Önizleme çağrıları hazırlanırken fazla bir lambda eklenmiş.
- Durum: Statik düzeltme uygulanacak; GitHub Actions `assembleDebug` ve `lint` henüz doğrulanmadı.

### HATA-004 · AÇIK
- Tarih: 2026-11-15
- Görev: GOREV-07
- Yer: `app/src/main/java/com/atasoy/eymentelefon/ui/ekranlar/rehber/RehberEkrani.kt`
- Belirti: `animateContentSize` için yanlış `androidx.compose.ui.draw` paketinden import yapılmış; Compose derlemesinde çözümlenemeyen import oluşur.
- Neden (tahmin): Animasyon uzantısının ait olduğu `androidx.compose.animation` paketi yerine çizim paketi seçilmiş.
- Durum: Düzeltme yapılacak; APK/lint kontrolü bekliyor.

### HATA-003 · AÇIK
- Tarih: 2026-11-15
- Görev: GOREV-05 (GOREV-07 çalışması öncesi kontrol)
- Yer: `app/src/main/java/com/atasoy/eymentelefon/veri/rehber/RehberDeposu.kt`
- Belirti: `val phone = ContactsContract.CommonDataKinds.Phone` kullanımı Kotlin'de Java sabit sınıfını değer olarak kullanıyor; derlemeyi engelleyebilir.
- Neden (tahmin): Android sağlayıcısının statik sabit sınıfı Kotlin nesnesi sanılmış.
- Durum: Düzeltme uygulanacak; APK ve lint henüz doğrulanmadı.

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

S-02: GOREV-08 — Numarayı "Varsayılan yap" işlemi için değiştirilemez `RehberDeposu` imzalarında `ContactsContract.Data.IS_SUPER_PRIMARY` güncelleme metodu yok. Şimdilik "Varsayılan" satırı Android kişi düzenleyicisini açıyor; tercih kullanıcının oradan yapmasına bırakıldı. Bu yeni depo metoduna izin veriliyor mu? (GOREV-08)

S-03: GOREV-08 — "Ana ekrana kısayol ekle" işleminin kişi detayını açabilmesi için `MainActivity` veya `Rotalar` içinde kişi derin bağlantısı gerekiyor; ikisi de "DEĞİŞTİRME" işaretli. Desteklenmeyen kısayol eylemi gösterilmedi. Onaylanan giriş yöntemi nedir? (GOREV-08)

S-04: GOREV-08 — "Görüntülü" eylemi yalnızca sistemde destek varsa gösterilecek; ancak `AramaBaslatici` yalnızca normal `TelecomManager.placeCall` çağırıyor ve video sağlayıcı tespiti için önceden tanımlı yöntem yok. Mevcut kodda düğme gizli. Video araması için hangi mevcut altyapı/izin kullanılmalı? (GOREV-08)

S-05: GOREV-10 — `docs/04_IZINLER.md §5` uyarınca gelen arama sırasında varsayılan telefon rolü yoksa alt sayfa Ana ekranda bir kez gösterilmeli. Ancak değiştirilemez `AramaBaslatici.izinEksik` yalnızca `ara()` sırasında istek yayımlıyor; gelen aramada rol kaybını gösteren olay/akış mimaride tanımlı değil. Gelen arama uyarısının tetikleyicisi olarak hangi mevcut bileşen kullanılmalı? Bu bölüm karar verilene kadar eklenmedi. (GOREV-10)

S-06: GOREV-10 — `docs/05_GOREV_PLANI.md` kabul koşulu "Telefon izni kaldırılmışken Ara → alt sayfa" diyor; ancak değiştirilemez `MainActivity` `ON_RESUME` sırasında `zorunlularTamam() == false` ise doğrudan tam ekran İzinler rotasına yönlendiriyor. Uygulama ön plandayken izin geri alındığında 12 numaralı alt sayfanın görünmesi için hangi öncelik geçerli? `MainActivity` veya izin açılış akışını onaysız değiştirmedim. (GOREV-10)
