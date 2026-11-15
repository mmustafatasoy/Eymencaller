# 02 · Ekranlar — her parça tek tek (v1.1)

Her satır: **görsel** (`docs/mockups/`) → **dosya** → **ne çalışır**.

| # | Görsel | Dosya | Çalışan her şey |
|---|---|---|---|
| 01 | `01_karsilama.png` | `ui/ekranlar/karsilama/KarsilamaEkrani.kt` | Sadece ilk açılış · 3 özellik · **Devam** → İzinler |
| 02 | `02_izinler.png` | `ui/ekranlar/izinler/IzinlerEkrani.kt` | 4 zorunlu + 1 isteğe bağlı izin · satıra veya **İzin Ver / Ayarla / Ayarlar** kapsülüne dokun · verilen ✓ · "Zorunlu · 3/4" sayacı · uygulamaya dönünce yenilenir · **Neden?** uyarısı · zorunlular tamam olmadan **Devam** pasif · Ayarlar'dan açılınca "‹ Ayarlar" + **Tamam** |
| 03 | `03_favoriler.png` | `ui/ekranlar/favoriler/FavorilerEkrani.kt` | Satıra dokun = ara · ⓘ = kişi kartı · uzun bas = favorilerden çıkar · ⚙ = Ayarlar · + = Kişiler sekmesi · büyük başlık kayınca küçük başlık |
| 04/05 | `04_son_aramalar.png`, `05_…_koyu.png` | `ui/ekranlar/sonaramalar/SonAramalarEkrani.kt` | **Tümü / Cevapsız** segmenti · cevapsız kırmızı · aynı numara art arda (n) · saat iOS biçimi (20:31 · Dün · Cumartesi · 12.11.26) · dokun = ara · ⓘ = kart · uzun bas = eylem sayfası (kopyala, mesaj, kişilere ekle, engelle, sil) |
| 19 | `19_kaydirarak_sil.png` | aynı dosya | Sola kaydır → **Sil** · **Düzenle** → (–) ile tek tek sil, solda **Temizle** → tümünü sil (onaylı) · **Bitti** |
| 06 | `06_kisiler.png` | `ui/ekranlar/rehber/RehberEkrani.kt` | Arama alanı → 10 · **Kartım** · Türkçe harf başlıkları (yapışkan) · soyad kalın · sağda mavi harf dizini (dokun/sürükle) · uzun bas = Ara / Mesaj / Favori · + = yeni kişi |
| 07 | `07_kisi_karti.png` | `ui/ekranlar/kisidetay/KisiDetayEkrani.kt` | 112dp avatar · **mesaj / ara / video / posta** (olmayan soluk) · numaralar (dokun = ara, uzun bas = kopyala) · son aramalar (3 + Tümünü Göster) · Mesaj Gönder · Kişiyi Paylaş (vCard) · Favori ekle/çıkar · Ana Ekrana Ekle · **Bu Arayanı Engelle** (onaylı) · **Kişiyi Sil** (onaylı) · Düzenle → sistem düzenleyici, dönünce yenilenir · rehberde yoksa **Yeni Kişi Oluştur** |
| 08/09 | `08_tus_takimi.png`, `09_…_koyu.png` | `ui/ekranlar/tuslar/TusTakimiEkrani.kt` | TR biçimi · rehber/T9 eşleşmesi mavi (dokun = o numarayı al) · yoksa **Numara Ekle** · `0` uzun = `+` · `1` uzun (boşken) = sesli mesaj · yeşil ara (boşsa son aranan) · sil (uzun = hepsi) · titreşim + DTMF sesi |
| 10 | `10_arama.png` | `ui/ekranlar/arama/AramaEkrani.kt` | Klavye açık gelir · **Vazgeç** · Türkçe/aksan duyarsız ("isik" → Işık) · eşleşen kısım kalın · numara yazılırsa “… numarasını ara” · dokun = ara · ⓘ = kart · klavyede "ara" = ilk sonuç |
| 11 | `11_giden_arama.png` | `gorusme/ui/AramaEkranlari.kt` → `GorusmeEkrani` | "arıyor… / bağlanıyor…" · 3×2 buton aynı düzen |
| 12 | `12_gorusme.png` | aynı | Süre + HD + SIM · **hoparlör** (Bluetooth/kulaklık varsa **ses** menüsü) · **beklet** · **sessiz** · **ekle** (uygulama Tuşlar'da açılır) · **bitir** · **tuşlar** · kulakta ekran kapanır |
| 13 | `13_gorusme_tuslar.png` | aynı | DTMF tuşları, girilenler başlıkta · **Gizle** |
| 14 | `14_gelen_arama.png` | `GelenAramaEkrani` | Ekran **kilitli değilse**: **Reddet / Kabul Et** · Hatırlat · Mesaj |
| 15 | `15_gelen_kilitli.png` | aynı | Ekran **kilitliyse**: **kaydırarak cevapla** (parıltılı yazı, %70 eşik) · ses tuşu zili susturur |
| 16 | `16_mesajla_yanitla.png` | aynı | Hızlı yanıtlar eylem sayfası → SMS ile reddeder |
| 17 | `17_izin_uyarisi.png` | `ui/ekranlar/izineksik/IzinEksikUyarisi.kt` | Herhangi bir yerde **Ara**'ya basıldı, izin eksik → iOS uyarısı · **İzin Ver ve Ara** → rol → Telefon izni → arama kendiliğinden başlar · reddedilirse kapanır |
| 18 | `18_ayarlar.png` | `ui/ekranlar/ayarlar/AyarlarEkrani.kt` | Tema (Sistem/Açık/Koyu) · Vurgu rengi · İzinler (x/5) · Varsayılan Uygulama · Engellenen Numaralar · Tuşlarda Titreşim · Tuş Sesleri · Hızlı Yanıtlar · Hata Kayıtları · Hakkında |
| — | — | `ayarlar/AyarAltEkranlari.kt` | **Hata Kayıtları** (Paylaş, Temizle) · **Engellenen Numaralar** (Kaldır, Numara Ekle… uyarısı) · **Hızlı Yanıtlar** (yazdıkça kaydedilir) |
| 20 | `20_guvenli_alan.png` | `ui/bilesenler/GuvenliAlan.kt` | Kamera deliği + gezinme tuşları otomatik — bkz. `06_GUVENLI_ALAN.md` |

## Bildirimler (görsel yok)
- Gelen arama: tam ekran (kilitliyken 15 açılır) + **Reddet / Cevapla** düğmeli bildirim.
- Görüşme sürerken: süre sayaçlı bildirim + **Bitir**; dokununca arama ekranı.
- Cevapsız arama: "N cevapsız arama" → dokununca Son Aramalar; Son Aramalar açılınca kapanır.
- Hatırlat: 10 dk sonra "… kişisini geri ara" → dokununca Tuşlar'da numara dolu.
