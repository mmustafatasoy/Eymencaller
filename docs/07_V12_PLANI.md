# 07 · v1.2 Planı — kullanıcı onayladı (15 Kasım 2026)

**Tek geçerli tasarım:** `docs/mockups/v12/` + canlı önizleme `docs/v12_onizleme.html`.
`docs/mockups/arsiv_v11/` **eski**; sadece v1.2'de değişmeyen ekranlar (tuş takımı, arama, favoriler, son aramalar) için bakılır.

Bu dosyadaki görevler Claude tarafından tanımlanmış **onaylı özelliklerdir**; Codex bunları yazabilir (AGENTS.md "yeni özellik yok" kuralının tek istisnası). Burada yazmayan hiçbir şey eklenmez.
Durum: `[x]` = Claude yaptı · `[ ]` = sırada. Sırayla, tek tek; her biri sonrası GitHub Actions yeşil olmalı.

---

### [x] V12-01 · "Bilinmeyen" hatası (görsel 4)
- `GorusmeViewModel`: son görülen `GorusmeDurumu` ve `Kisi` saklanır (`sonGorusme`). Liste boşalınca ekran `sonGorusme` ile çizilir.
- Ekranda: ad + "Arama bitti · süre". Rehberde yoksa biçimli numara. `gorusme_bilinmeyen` sadece numara gizliyse.

### [x] V12-02 · Tema sadece sistemden (görsel 10)
- `MainActivity` + `EymenTelefonTemasi`: her zaman `TemaSecimi.SISTEM`. Ayarlar'daki **Tema** satırı kaldırılır. (`AyarDeposu.tema` alanı dokunulmadan kalır, okunmaz.)

### [x] V12-03 · Ayarlar sadeleşir (görsel 10, 11)
- Başlık "Telefon". Gruplar: **ARAMALAR** (Varsayılan Telefon Uygulaması, Mesajla Yanıtla) · **SES VE TİTREŞİM** (Tuş Takımında Titreşim, Tuş Takımı Sesleri) · **ENGELLEME** (Engellenen Kişiler, Bilinmeyen Arayanları Sustur + dipnot) · **DESTEK** (Hata Kayıtları, Hakkında).
- **İzinler** ve **Vurgu Rengi** satırları kalkar. Ayarlar girişi: Son Aramalar'daki dişli.
- Engellenen Kişiler ekranı (görsel 11): satırda avatar + ad (rehberde varsa) + numara; "Yeni Ekle…" → numara uyarısı.

### [x] V12-04 · Bilinmeyen Arayanları Sustur
- `AyarDeposu`: `bilinmeyenSustur: Boolean` (anahtar `bilinmeyen_sustur`, varsayılan false).
- `EymenCallScreeningService`: açıksa ve `RehberDeposu.numaradanKisi(numara) == null` → `CallResponse.Builder().setSilenceCall(true).build()` (çalmaz, kayda düşer).

### [x] V12-05 · Kişi Posteri altyapısı (görsel 1, 2, 3, 9)
- Yapıldı: `veri/poster/PosterDeposu.kt` (`fotoKaydet`, `ayarKaydet`, `fotoSil`, `sil`, `oku`, `degisim`). Arama ekranında: `gorusme/ui/KisiPosteri.kt` (`PosterGorunumu`, `PosterFotografi`, `PosterKucukResim`, `CanliAd`).
- Plan: `veri/poster/PosterDeposu.kt` — `filesDir/posterler/<lookupKey>.jpg` (en uzun kenar 1600px, JPEG %90) + `posterler/<lookupKey>.json` (`{"canli":true,"renk":null}`). Fonksiyonlar: `kaydet(lookupKey, Uri)`, `sil(lookupKey)`, `oku(lookupKey): Poster?` (`data class Poster(val dosya: File?, val renkIndex: Int?, val canli: Boolean)`).
- `UygulamaKonteyneri`'ne `posterDeposu` eklenir.
- Fotoğraf seçimi: `ActivityResultContracts.PickVisualMedia(ImageOnly)` (izin gerektirmez).

### [x] V12-06 · Gelen arama posteri + canlı ad (görsel 1, 2)
- `GorusmeViewModel` kişinin posterini yükler. `AramaZemini`: poster varsa fotoğraf tam ekran (`ContentScale.Crop`), canlıysa `infiniteTransition` ile 18 sn'de 1.0 → 1.12 ölçek (ileri-geri). Üst %35 ve alt %40'ta karartma gradyanı. Poster yoksa mevcut renkli zemin.
- Gelen aramada ad: 64sp ExtraBold, iki satır (ad / soyad), her harf 70ms arayla aşağıdan yukarı + saydamdan belirir (600ms), sonra 3.2 sn'lik %3.5 "nefes" ölçeği. Üstünde küçük "mobil".
- Cevaplanınca (görsel 3): fotoğraf 18dp bulanık + %35 karartma (Android 12+ `Modifier.blur`, altında sadece karartma); ad 34sp'ye küçülüp yukarı kayar (`AnimatedContent`/`animateDpAsState`), üstte 72dp yuvarlak poster küçük resmi.

### [ ] V12-07 · Kişi kartı posterli başlık (görsel 7)
- Kartın üst 330dp'si poster (yoksa avatar rengi gradyan); ad posterin üstünde beyaz; 4 eylem kutusu buzlu cam (%22 beyaz). Gruplarda yeni satır: **Kişi Posteri ve Fotoğraf** → V12-08.

### [ ] V12-08 · Kişi posteri düzenleyici (görsel 9)
- Yeni ekran `ui/ekranlar/poster/PosterEkrani.kt` (rota `poster/{lookupKey}`): koyu zemin, ortada mini gelen-arama önizlemesi, altta **Fotoğraf / Renk / Yazı** sekmeleri; Fotoğraf → seçici; Renk → 5 hazır gradyan (`AramaEkraniRenk.palet`); **Canlı efekt** anahtarı; Vazgeç / Bitti.

### [ ] V12-09 · Uzun basma önizleme (görsel 6)
- Kişiler ve Favoriler'de uzun bas → titreşim (`HapticFeedbackType.LongPress`) + tam ekran katman: arka plan bulanık (12+ `blur`, altında %40 beyaz/siyah örtü), üstte 170dp posterli kart (ad + numara), altında 250dp menü: Ara · Mesaj · Görüntülü Ara | Paylaş · Favorilere Ekle/Çıkar · Düzenle | **Kişiyi Sil** (kırmızı, onaylı). Kart büyüyerek açılır (`scale 0.9→1`, 220ms). Karta dokun → kişi kartı; boşluğa dokun → kapanır.

### [ ] V12-10 · Uygulama içinde kişi ekle / düzenle (görsel 8)
- Yeni ekran `ui/ekranlar/kisiduzenle/KisiDuzenleEkrani.kt` + VM (rota `kisi-duzenle/{kisiId}`, -1 = yeni; `numara` argümanı ön doldurma).
- Alanlar: fotoğraf (seç/kaldır), Ad, Soyad, Şirket, telefonlar (tür seçici: mobil/ev/iş/diğer, − ile sil, + telefon ekle), e-postalar, **Kişi Posteri** satırı.
- Kayıt: `RehberDeposu.kaydet(KisiTaslak)` → `ContentProviderOperation` toplu işlem (RawContacts + StructuredName + Phone + Email + Organization + Photo `DATA15` küçültülmüş 720px JPEG). Düzenlemede eski Data satırları güncellenir/silinir.
- Tüm `DisIslemler.kisiyeEkle / yeniKisi / kisiDuzenle` çağrıları bu ekrana yönlendirilir.

### [x] V12-11 · Kişiler satırlarında küçük avatar + toplam (görsel 5)
- Satır: 34dp avatar (fotoğraf/monogram) + "Ad **Soyad**", satır yüksekliği 52dp; listenin sonunda "N Kişi" (dipnot, ortalı). Üst sol "Listeler" yerine boş (ayarlar Son Aramalar'da).

### [ ] V12-12 · Sistem yönlendirmeleri (görsel 13) — *kısmen: VIEW kişi + CALL_BUTTON yapıldı (Claude); INSERT/EDIT, V12-10 ekranı bitince eklenecek*
- Manifest `MainActivity`'ye ek filtreler: `android.intent.action.VIEW` + `vnd.android.cursor.item/contact` ve `…/person`; `INSERT` + `vnd.android.cursor.dir/contact`; `EDIT` + `vnd.android.cursor.item/contact`; `android.intent.action.CALL_BUTTON`.
- `MainActivity.onNewIntent/onCreate`: kişi URI'si → `kisi/{id}`; INSERT → `kisi-duzenle/-1` (+`phone` ekstrası); EDIT → `kisi-duzenle/{id}`; CALL_BUTTON → Tuşlar.
- Not (kullanıcıya): sistemin kendi Telefon/Rehber simgesi doğrudan açılırsa Android yönlendirmez; ana ekranda Eymen simgesi kullanılmalı.

### [ ] V12-13 · İzinler sadece girişte (görsel 12)
- İzinler ekranı: tek "Devam (x/5)" butonu sırayla eksik olanı ister (rol → telefon → kişiler → geçmiş → bildirim). Reddedilen için "Ayarlar'da izin ver". Hepsi tamam olmadan ana ekrana geçilemez (zaten `MainActivity` kontrol ediyor). `ayarModu` kaldırılır.
