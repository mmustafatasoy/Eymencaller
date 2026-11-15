# 01 · Tasarım Sistemi — v1.1 iOS tarzı

Görsel referans: `docs/mockups/00_genel_bakis.png` ve tek tek ekranlar (`01_…` – `20_…`).
Tüm değerler `app/src/main/java/com/atasoy/eymentelefon/ui/theme/` içindedir. Kod içinde çıplak renk / dp YASAK.

> Not: Düzen ve his iOS Telefon uygulamasına yakındır; ikonlar ve yazı tipi bize aittir
> (Apple'ın SF Symbols ikonları ve SF Pro yazı tipi lisanslı olduğu için kullanılmaz; sistem yazı tipi kullanılır).

## 1. Renkler — `EymenRenk`
| Token | Açık | Koyu | Kullanım |
|---|---|---|---|
| `arkaPlan` | `#FFFFFF` | `#000000` | Düz liste ekranları |
| `grupluArkaPlan` | `#F2F2F7` | `#000000` | Ayarlar, izinler, kişi kartı |
| `yuzey` | `#FFFFFF` | `#1C1C1E` | Gruplu kutu, uyarı, eylem sayfası |
| `tusZemini` / `tusBasili` | `#E5E5EA` / `#BDBDC2` | `#333333` / `#737373` | Tuş takımı |
| `etiket` | `#000000` | `#FFFFFF` | Ana metin |
| `ikincilEtiket` | `#8A8A8E` | `#8D8D93` | Alt metin, saat |
| `ucunculEtiket` | `#C7C7CC` | `#48484A` | Ok (›), pasif |
| `ayirici` | `#C6C6C8` | `#38383A` | 0.5dp çizgiler |
| `aramaAlani` | `#767680` %12 | `#767680` %24 | Arama alanı, segment zemini |
| `sekmeCubugu` | `#F9F9F9` %94 | `#161616` %94 | Alt sekme çubuğu |
| `vurgu` | `#007AFF` | `#0A84FF` | Butonlar, linkler, ⓘ, seçili sekme |
| `yesil` / `kirmizi` / `turuncu` | `#34C759` / `#FF3B30` / `#FF9500` | `#30D158` / `#FF453A` / `#FF9F0A` | Ara, sil/cevapsız, uyarı |

- **Vurgu seçenekleri** (Ayarlar): mavi, yeşil, mor, turuncu, pembe (`VurguSecenekleri`, açık/koyu çifti).
- **Ayar ikon kareleri** (`KareRenk`): mavi, yeşil, kırmızı, turuncu, pembe, mor, gri.
- **Avatar** (`AvatarRenk`): gri gradyan `#A5ABB9 → #858994`, beyaz baş harfler (iOS monogram).
- **Arama ekranları** (`AramaEkraniRenk`): her temada koyu; kişi adına göre 6 koyu paletten biri; butonlar %18 beyaz, aktifken beyaz zemin + siyah ikon.

## 2. Yazı — `EymenYazi` (iOS ölçeği)
| Stil | Boyut | Nerede |
|---|---|---|
| `buyukBaslik` | 34 Bold | "Son Aramalar", "Kişiler" |
| `baslik1` | 28 SemiBold | Kişi kartında ad |
| `vurguluGovde` / `govde` | 17 SemiBold / Regular | Satır başlıkları, butonlar |
| `altBaslik` | 15 | Alt satırlar, saat |
| `dipnot` | 13 | Grup başlıkları/dipnotları |
| `sekme` | 10 Medium | Sekme etiketleri |
| `numaraEkrani` | 38 | Tuş takımı numarası |
| `tusRakam` / `tusHarf` | 34 / 9.5 Bold, 2sp aralık | Tuşlar |
| `aramaIsim` / `aramaDurum` | 34 / 17 | Arama ekranı |

## 3. Ölçüler — `EymenBoyut`, `EymenBosluk`, `EymenSekil`
- Kenar boşluğu 16 · satır 44 (avatarlı 60) · avatar 40 (kartım 58, kart 112)
- Gezinme çubuğu 44 · sekme çubuğu 49 (+ gezinme tuşu yüksekliği)
- Tuş 78 (yatay 28, dikey 16 aralık) · arama butonu 76 · Kabul/Reddet 78 · kaydırma rayı 80
- Gruplu kutu köşesi 10 · büyük buton 50 yükseklik, köşe 14 · uyarı 270 genişlik, köşe 14
- Anahtar 51×31 · ikon karesi 29 (köşe 7) · ayırıcı 0.5dp

## 4. Bileşenler (`ui/bilesenler/`)
| Dosya | İçerik | Görsel |
|---|---|---|
| `GuvenliAlan.kt` | `IosSayfa`, `ustGuvenli()`, `altGuvenli()`, `yatayGuvenli()` | 20 |
| `IosNavCubugu.kt` | Gezinme çubuğu, büyük başlık, küçük başlığın belirmesi, metin/geri/ikon butonları | 03–07, 18 |
| `IosListe.kt` | `IosGrup`, `IosSatir`, `IosAyarSatiri`, `IkonKaresi`, `IosBilgiButonu` ⓘ, `IosKapsulButon`, `IosButon`, `IosEylemSatiri`, `Ayirici` | 02, 07, 18 |
| `IosKontroller.kt` | `IosAramaAlani` (+aktif, Vazgeç), `IosSegment`, `IosAnahtar`, `IosAvatar` | 04, 06, 10, 18 |
| `IosSekmeCubugu.kt` | 4 sekme; zemin gezinme tuşlarının arkasına uzanır | 03–09 |
| `IosTusTakimi.kt` | 3×4 yuvarlak tuşlar, basınca koyulaşır | 08, 09, 13 |
| `IosUyari.kt` | `IosUyari` (ortada uyarı), `IosEylemSayfasi` (alttan seçenekler + Vazgeç) | 16, 17 |
| `IosCagri.kt` | `IosCagriButonu`, `IosRenkliCagriButonu`, `IosKaydirarakCevapla` | 11–15 |
| `Tiklama.kt` | `uzunBasilabilir`, `sessizTiklama` | — |

## 5. Hareket
- Büyük başlık kaydırılınca gezinme çubuğunda küçük başlık belirir (+ ince çizgi).
- Tuşa basınca renk 120ms'de koyulaşır; titreşim/ses Ayarlar'a bağlı.
- "kaydırarak cevapla" yazısında soldan sağa akan parıltı (2.2 sn); %70 kaydırınca cevaplar.
- Son Aramalar: sola kaydır → kırmızı "Sil"; Düzenle → satır başında kırmızı (–).

## 6. Simge
`design/icon/icon.svg` · Play Store: `design/icon/icon_playstore_512.png` · uyarlanabilir: `res/drawable/ic_launcher_*.xml`.
