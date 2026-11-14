# 02 · Ekranlar (görsel + davranış)

Her ekran için: **görsel** = `docs/mockups/NN_*.png` (birebir uyulur), **davranış** = aşağıdaki maddeler.
Ekran dosyası yolu `docs/03_MIMARI.md`'deki tablodadır.

---

## 01 · Karşılama — `01_karsilama.png`
- Sadece **ilk açılışta** gösterilir (`Ayarlar.ilkAcilisTamam == false`).
- Arka planda iki bulanık ışık (vurgu yeşil sol üst, mavi sağ alt).
- Simge 132dp, isim, açıklama, 3 noktalı sayfa göstergesi (1. aktif).
- "Başlayalım" → İzinler ekranı. Geri tuşu → uygulamadan çık.

## 02 · İzinler (uygulama açılmadan önce) — `02_izinler.png`
Ayrıntılı akış: `docs/04_IZINLER.md`.
- 5 kart, sabit sıra: Varsayılan telefon uygulaması · Telefon · Rehber · Arama geçmişi · Bildirimler.
- İlk 4 kart "Zorunlu", Bildirimler "İsteğe bağlı" (Android 13 altında bu kart gizlenir).
- Durum çipi: `Verildi` (yeşil çerçeveli kart) · `İzin ver` · `Ayarla` (rol için) · `Ayarlardan aç` (kalıcı reddedildiyse → uygulama ayar sayfasını açar).
- İlerleme çubuğu: verilen / toplam.
- "Devam et": tüm zorunlular verilene kadar pasif. Verilince → Ana ekran (Son sekmesi).
- Uygulama arka plandan dönünce (`ON_RESUME`) durumlar yeniden okunur.
- "Neden bu izinler gerekli?" → her iznin 1 cümlelik açıklamasını gösteren alt sayfa.

## 03 · Tuş takımı — `03_tus_takimi.png`
- Üstte arama çubuğu (dokununca 07'ye gider).
- Numara alanı: Türkiye formatı `0532 123 45 67` (yazdıkça biçimlenir, `PhoneNumberFormattingTextWatcher` mantığı, ülke `TR`).
- Numara 3+ hane olunca **T9 eşleşme kartı**: rehberde numara veya T9 harf eşleşmesi (Türkçe harf tablosu `T9Eslestirici`'de). En iyi 1 sonuç gösterilir; dokununca numara onunla değişir. Eşleşme yoksa "Ekle" çipi → yeni kişi (sistem kişi ekleme ekranı `Intent.ACTION_INSERT`).
- Tuş harfleri: `2 ABCÇ · 3 DEF · 4 GĞHIİ · 5 JKL · 6 MNOÖ · 7 PQRSŞ · 8 TUÜV · 9 WXYZ`, `0` altında `+`.
- `0` uzun bas → `+`. `1` uzun bas → sesli mesaj (`voicemail:` URI). Silme uzun bas → hepsini sil.
- Tuş sesi (DTMF `ToneGenerator`) ve titreşim → Ayarlar'a bağlı.
- Ara butonu → `AramaBaslatici.ara(numara)`. Numara boşsa son aranan numarayı doldurur.

## 04 · Son aramalar — `04_son_aramalar.png`
- Filtre çipleri: Tümü · Cevapsız (kırmızı sayı) · Gelen · Giden.
- Gruplama: Bugün · Dün · Bu hafta · Daha eski. Aynı numaradan art arda aramalar tek satır + `(n)`.
- Tür ikonu renkleri: gelen `vurgu`, giden `vurgu2`, cevapsız `cevapsiz` (isim de kırmızı).
- Satıra dokun → Kişi detayı (rehberde yoksa numara detayı, aynı ekran isimsiz). Yeşil buton → ara.
- Uzun bas → alt menü: Numarayı kopyala · Mesaj gönder · Kişilere ekle · Engelle · Kaydı sil.
- Sağ alt FAB (tuş takımı ikonu) → Tuşlar sekmesi.
- Ekrana girilince cevapsız arama bildirimi temizlenir, cevapsızlar "görüldü" yapılır.

## 05 · Rehber — `05_rehber.png`
- Arama çubuğu metni: "`N` kişi arasında ara".
- Favoriler yatay şerit (54dp avatar + ad). Uzun bas → favoriden çıkar.
- "Benim kartım" (ContactsContract.Profile), yoksa gizli.
- Harf başlıkları Türkçe alfabe sırasıyla (`Collator(Locale("tr"))`). Başlıklar listede yapışkan (`stickyHeader`).
- Sağda harf dizini; sürükleyince ilgili harfe atlar + büyük harf balonu.
- "Yeni kişi" genişletilmiş FAB → sistem kişi ekleme (`ACTION_INSERT`), liste kaydırılınca daralır (sadece ikon).

## Favoriler sekmesi (ayrı mockup yok — 05'teki stilin ızgara hali)
- Arama çubuğu (05 ile aynı) + 3 sütunlu ızgara: `KisiAvatari` 72dp + ad (`isim` stili) + altında numara türü (`kucuk`).
- Dokun → ara. Uzun bas → Kişi detayı. Boşsa ortada yıldız ikonu + "Favori kişi yok" + "Rehberden ekle" ikincil buton.

## 06 · Kişi detayı — `06_kisi_detay.png`
- Üst: geri · favori yıldızı (doluysa `uyari` rengi) · düzenle (`ACTION_EDIT`) · daha fazla (Sil, Kısayol ekle).
- Avatar 104dp, isim, şirket/not satırı. Üst arka planda avatar renginde radyal ışık.
- 4 aksiyon: Ara (gradyan) · Mesaj (`smsto:`) · Görüntülü (varsa sistem görüntülü arama, yoksa gizle) · E-posta (yoksa gizle).
- Numaralar kartı: her numara satırı → dokun ara, sağdaki ikon → mesaj. Uzun bas → kopyala / varsayılan yap.
- Son aramalar kartı: bu kişiyle son 3 arama, "Tümü" → filtrelenmiş Son aramalar.
- Kişiyi paylaş (vCard) · Numarayı engelle (`BlockedNumberContract`, kırmızı).

## 07 · Arama ekranı (kişi / numara arama) — `07_arama_ekrani.png`
- Açılınca klavye otomatik açılır, çubuk odaklı (1.5dp vurgu çerçeve).
- Sorgu: isim (Türkçe büyük/küçük harf duyarsız, `ı/i` doğru), numara (boşluksuz parça eşleşmesi), not alanı.
- Eşleşen kısım `vurgu` renkli + %22 zemin ile vurgulanır.
- Bölümler: Kişiler · N  /  Son aramalarda. Sorgu tamamen rakamsa en üstte "**Ara: 0532…**" satırı.
- 150ms debounce. Boş sorguda son 5 arama gösterilir.
- Klavyedeki "ara" tuşu → ilk sonucu arar.

## 08 · Giden arama — `08_giden_arama.png`
- `CallActivity` içinde, durum `DIALING / CONNECTING`.
- Zemin her temada koyu (`aramaZemini` + radyal ışıklar). Kilit ekranı üstünde de açılır.
- "SIM 1 · Aranıyor…" (çift SIM'de hangi hat).
- Avatar 120dp + nabız halkaları. İsim yoksa numara büyük yazılır.
- 3 kontrol: Sessiz · Tuşlar · Hoparlör. Kapat butonu altta ortada.
- Karşı taraf açınca → 10'a animasyonla geçer.

## 09 · Gelen arama — `09_gelen_arama.png`
- Ekran kilitliyse/kapalıysa: tam ekran bildirim (`fullScreenIntent`) → `CallActivity` (showWhenLocked + turnScreenOn).
- Uygulama kullanılırken: üstte "heads-up" bildirim (Cevapla / Reddet butonlu, `CallStyle`).
- Kaydırarak cevapla: sağa → cevapla, sola → reddet. Erişilebilirlik açıksa iki ayrı buton gösterilir.
- "Mesajla reddet" → Ayarlar'daki hızlı yanıt mesajları listesi. "Hatırlat" → 10 dk sonra bildirim.
- Ses kısma tuşu → zil sesini sustur.

## 10 · Görüşme sırasında — `10_gorusme.png`
- Üstte HD çipi (VoLTE ise) + SIM. Avatar 96dp, isim, süre sayacı `mm:ss` (1 saati geçerse `s:mm:ss`).
- Ses dalgası süsü (gerçek ses değil, animasyon).
- 2×3 kontrol: Sessiz · Tuş takımı · Hoparlör · Beklet · Arama ekle · Kişiler.
  Bluetooth bağlıysa "Hoparlör" yerine "Ses" (alt menü: Telefon / Hoparlör / Bluetooth).
- Tuş takımı açılınca kontrol ızgarasının yerine DTMF tuş takımı gelir (`Call.playDtmfTone`).
- Yakınlık sensörü: kulakta ekran kapanır (`PROXIMITY_SCREEN_OFF_WAKE_LOCK`).
- Uygulamadan çıkınca devam eden arama bildirimi + üstte yeşil "Aramaya dön" şeridi.

## 11 · Ayarlar + İzin ayarları — `11_ayarlar.png`
- **Görünüm:** Tema (Sistem/Koyu/Açık, segment) · Vurgu rengi (5 renk).
- **İzinler ve güvenlik:** İzin ayarları (→ 02 ekranını "ayar modunda" açar: üstte geri oku, alt buton "Tamam") · Varsayılan telefon uygulaması · Engellenen numaralar (liste + ekle/sil).
- **Aramalar:** Tuşlarda titreşim · Tuş sesleri · Hızlı yanıt mesajları (4 varsayılan, düzenlenebilir).
- **Destek:** Hata kayıtları (`HataKaydedici` kayıtları, paylaş ve temizle) · Hakkında (sürüm, `BuildConfig`).
- Ayarlar `DataStore Preferences` ile saklanır.

## 12 · Aramaya basınca izin eksikse — `12_izin_eksik.png`
- `AramaBaslatici.ara()` eksik izin bulursa açılan **alt sayfa**.
- Eksik olanlar kırmızı "Eksik", tamamlar yeşil "Hazır".
- "İzin ver ve ara" → eksik izinleri sırayla ister; hepsi verilirse **aramayı otomatik başlatır**.
- "Şimdi değil" → kapanır, numara silinmez.
- Aynı alt sayfa: gelen arama geldiğinde rol yoksa da bildirim yerine bu uyarı Ana ekranda bir kez gösterilir.
