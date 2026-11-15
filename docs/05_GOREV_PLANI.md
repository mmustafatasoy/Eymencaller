# 05 · Görev Planı

> **Durum (15 Kasım 2026, v1.1):** Arayüz iOS tarzında baştan yazıldı (20 ekran görseli, her biri çalışır), güvenli alan (kamera deliği + gezinme tuşları) otomatik. Arama motoru, veri katmanı ve bildirimler v1.0'dan aynen devam.
> Codex'in işi artık **derlemek, doğrulamak ve çıkan hataları kayıt kurallarıyla düzeltmek.** Yeni özellik eklemek yok.
> Kurallar: bir seferde **tek görev** · her hata önce `HATA_GUNLUGU.md`'ye · görev sonunda `[x]` + commit `GOREV-NN: ...`

---

## ✅ Claude'un tamamladıkları
- [x] Tasarım sistemi, 37 ikon, simge, 12 mockup
- [x] 16 bileşen dosyası (`ui/bilesenler/`)
- [x] Karşılama + İzinler (açılıştan önce) + İzin eksik alt sayfası
- [x] Ana ekran: Favoriler · Son aramalar · Rehber · Tuş takımı
- [x] Kişi detayı · Kişi/numara arama · Ayarlar (+ hata kayıtları, engellenenler, hızlı yanıtlar)
- [x] Depolar: rehber, arama geçmişi, engellenen numaralar
- [x] Arama motoru: InCallService, giden/gelen/görüşme ekranları, kaydırarak cevapla, DTMF, ses yolu, beklet, yakınlık sensörü
- [x] Bildirimler: gelen arama (tam ekran), devam eden arama, cevapsız arama, hatırlatma
- [x] Engelleme servisi (CallScreeningService)
- [x] Kotlin sözdizimi denetimi + import / string / drawable çapraz kontrolü
- [x] **v1.1:** iOS tarzı tema (açık + koyu), 9 iOS bileşen dosyası, tüm ekranlar yeniden yazıldı
- [x] **v1.1:** Güvenli alan altyapısı (`GuvenliAlan.kt`, `shortEdges`, `SystemBarStyle`) — HATA-014
- [x] **v1.1:** Son Aramalar'da kaydırarak sil + Düzenle/Temizle, Kişiler'de harf dizini, kilitliyken "kaydırarak cevapla"

---

## Codex görevleri

### [x] GOREV-01 · v1.1'i derle ve lint'i temizle
- ✅ 15 Kasım 2026: 3 derleme hatası düzeltildi (HATA-018…020), `assembleDebug` + `lintDebug` başarılı — [Actions](https://github.com/mmustafatasoy/Eymencaller/actions/runs/43692314487), commit `cd8783d`. APK: Actions › bu çalıştırma › **EymenTelefon-debug**.
- v1.0 iskeleti 14 Kasım'da GitHub Actions'ta derlendi (HATA-001). **v1.1 yeni koddur, yeniden derlenmeli.**
- Yerel ortamda Gradle indirilemiyorsa (HATA-001) derlemeyi **GitHub Actions › APK Derle** ile yap; her `main` push'unda otomatik çalışır.
- `./gradlew assembleDebug lintDebug`
- Claude'un ortamında Android SDK olmadığı için tam derleme yapılamadı (sadece Kotlin sözdizimi denetimi ✅). Çıkan **her** derleme / lint hatası:
  önce HATA_GUNLUGU'na AÇIK → en küçük düzeltme → DÜZELTİLDİ.
- Düzeltirken tasarım, isim, imza, paket yapısı değişmez. Bir API gerçekten yoksa (sürüm farkı) eşdeğer API kullanılır ve kayda yazılır.
- **Kabul:** APK oluşuyor, lint 0 hata.

### [ ] GOREV-02 · Görsel karşılaştırma
- Uygulamayı aç, her ekranı `docs/mockups/01…20` ile yan yana karşılaştır (açık ve koyu tema).
- Ölçü/renk/metin farkı = HATA kaydı. Düzeltme sadece `ui/theme` token'ları veya ilgili bileşen içinde yapılır.
- **Kabul:** Her ekran mockup'a göz ile aynı.

### [ ] GOREV-03 · Cihaz testi (Samsung Android 14 + Tecno Android 16)
Her madde ayrı denenir; başarısız olan HATA olarak yazılır:
- [ ] **Güvenli alan:** her ekranda üstte kamera deliğine, altta gezinme tuşlarına hiçbir şey değmiyor (hareket çubuğu + 3 tuş ayrı ayrı; telefon yan çevrilince de) — `06_GUVENLI_ALAN.md`
- [ ] Gelen arama kilit ekranında: ad kameranın altında, "kaydırarak cevapla" gezinme çubuğunun üstünde
- [ ] Son Aramalar: sola kaydır → Sil · Düzenle → (–) · Temizle → onay → hepsi siliniyor
- [ ] Kilit açıkken gelen arama: Reddet / Kabul Et düğmeleri; kilitliyken kaydırma rayı
- [ ] Temiz kurulum: Karşılama → İzinler → 5 izin → Ana ekran
- [ ] Ayarlardan bir izni kaldır, uygulamaya dön → İzinler ekranı açılıyor
- [ ] Tuşlar: `0532…` biçimleniyor, rehber eşleşmesi mavi, yoksa "Numara Ekle", `0` uzun bas = `+`, sil uzun bas = hepsini sil
- [ ] Telefon iznini kaldır → Ara → 17 numaralı uyarı → izin ver → arama kendiliğinden başlıyor
- [ ] Giden arama → 11 ("arıyor…") → karşı açınca 12, süre sayıyor
- [ ] Çalarken ses tuşu zili susturuyor
- [ ] Mesajla reddet → SMS gidiyor · Hatırlat → 10 dk sonra bildirim
- [ ] Görüşmede: sessiz, hoparlör, beklet, tuş takımı (DTMF), kulakta ekran kapanıyor
- [ ] Bluetooth kulaklık bağlıyken "Ses" menüsü çıkıyor
- [ ] Arama ekle / Kişiler → uygulamaya dönüyor, arama bildirimi duruyor
- [ ] Cevapsız arama → bildirim; Son aramalar açılınca bildirim kapanıyor
- [ ] Engellenen numara aradığında çalmıyor, Son aramalarda "Engellendi"
- [ ] Kişiler: "Çağla" Ç başlığında, "İsmail" İ başlığında; soyad kalın; harf dizini sürükleme çalışıyor
- [ ] Arama: "ISIK" yazınca "Işık" bulunuyor, eşleşen kısım kalın; klavye listeyi örtmüyor
- [ ] Ayarlar: Açık tema / vurgu rengi anında uygulanıyor; durum çubuğu ikonları okunuyor
- [ ] Çift SIM'de "SIM 1 / SIM 2" yazıyor
- [ ] Yazı boyutu en büyükte tuş takımı taşmıyor
- **Kabul:** Tüm maddeler ✅, HATA_GUNLUGU'nda açık hata yok.

### [ ] GOREV-04 · Sürüm
- `assembleRelease` (R8 açık) → uygulama açılıyor, arama yapılabiliyor (ProGuard kaynaklı çökme yok).
- **Kabul:** imzasız release APK çalışıyor.
