#!/usr/bin/env python3
"""Eymen Telefon v1.2 tasarım önerisi: kişi posteri, canlı ad, kişi düzenleme, uzun basma önizleme, yeni ayarlar."""
import sys, pathlib, re
KOK = pathlib.Path(__file__).parent
sys.path.insert(0, str(KOK))
from ikonlar import svg as ic
from playwright.sync_api import sync_playwright

OUT = pathlib.Path(sys.argv[1]); ICON = pathlib.Path(sys.argv[2]).read_text()
eski = (KOK / "mockup_uretici_ios.py").read_text()
CSS = re.search(r"CSS = r'''(.*?)'''", eski, re.S).group(1)

def av(ini, size=40, fs=None, foto=None):
    fs = fs or int(size * 0.4)
    if foto:
        return f'<div class="av" style="width:{size}px;height:{size}px;background:{foto};background-size:cover"></div>'
    return f'<div class="av" style="width:{size}px;height:{size}px;font-size:{fs}px">{ini}</div>'

def sb(beyaz=False):
    c = "#fff" if beyaz else "var(--etiket)"
    return f'''<div class="sb" style="color:{c}"><span>9:41</span><i class="cam"></i><span class="sbr">
<svg width="17" height="11" viewBox="0 0 17 11"><rect x="0" y="7" width="3" height="4" rx="1" fill="currentColor"/><rect x="4.5" y="5" width="3" height="6" rx="1" fill="currentColor"/><rect x="9" y="2.5" width="3" height="8.5" rx="1" fill="currentColor"/><rect x="13.5" y="0" width="3" height="11" rx="1" fill="currentColor"/></svg>
<svg width="25" height="12" viewBox="0 0 25 12"><rect x=".5" y=".5" width="21" height="11" rx="3.5" fill="none" stroke="currentColor" opacity=".4"/><rect x="2" y="2" width="16" height="8" rx="2" fill="currentColor"/></svg></span></div>'''

def phone(sid, baslik, govde, koyu=False, cls="", beyaz=False, not_=""):
    n = f'<div class="not">{not_}</div>' if not_ else ""
    return f'<div class="cell"><div class="phone {"dark" if koyu else ""} {cls}" id="{sid}">{sb(beyaz)}{govde}<div class="home"></div></div><div class="cap">{baslik}</div>{n}</div>'

# "Fotoğraf" yer tutucuları (kişinin seçeceği resim) — tamamen CSS ile çizilmiş manzaralar
FOTO_GUN_BATIMI = ("radial-gradient(circle at 70% 38%,#FFE29A 0 7%,rgba(255,226,154,.0) 18%),"
                   "radial-gradient(ellipse 120% 40% at 30% 92%,#2B1B3D 0 60%,transparent 61%),"
                   "radial-gradient(ellipse 110% 45% at 85% 98%,#3F2550 0 60%,transparent 61%),"
                   "linear-gradient(180deg,#2E3A8C 0%,#7A4FA0 35%,#F07A6A 62%,#FFB36B 78%,#3F2550 100%)")
FOTO_DENIZ = ("radial-gradient(circle at 25% 30%,#FFFFFF 0 4%,rgba(255,255,255,0) 14%),"
              "linear-gradient(180deg,#5FB7E8 0%,#A8DDF5 48%,#1E7FB3 49%,#0E5C8A 75%,#E8D6A8 76%,#D9C08A 100%)")
FOTO_ORMAN = ("radial-gradient(ellipse 60% 30% at 20% 85%,#1E3B26 0 60%,transparent 61%),"
              "radial-gradient(ellipse 70% 40% at 80% 90%,#16301E 0 60%,transparent 61%),"
              "linear-gradient(180deg,#B8D8C8 0%,#7FB59A 40%,#3D7A57 70%,#1E3B26 100%)")

def cbtn(icn, t, aktif=False, kirmizi=False):
    cls = "cb red" if kirmizi else ("cb on" if aktif else "cb")
    renk = "#000" if aktif else "#fff"
    rot = ' style="transform:rotate(135deg)"' if kirmizi else ""
    return f'<div class="{cls}"><div{rot}>{ic(icn,30,renk,2)}</div><span>{t}</span></div>'

def poster(foto, koyuluk=.25):
    return f'<div class="poster" style="background:{foto};background-size:cover"></div><div class="posterdim" style="background:linear-gradient(180deg,rgba(0,0,0,{koyuluk}) 0%,rgba(0,0,0,0) 35%,rgba(0,0,0,.15) 60%,rgba(0,0,0,.55) 100%)"></div>'

S = []

# A — Gelen arama, kişi posteri + canlı ad
S.append(phone("v12_01_gelen_poster", "1 · Gelen arama: kişi posteri + canlı ad", f'''
{poster(FOTO_GUN_BATIMI)}
<div class="pname"><div class="psub">mobil</div><div class="bigname anim">{''.join(f'<span style="animation-delay:{i*0.07:.2f}s">{c}</span>' if c!='|' else '<br>' for i,c in enumerate('Ayşe|Demir'))}</div></div>
<div class="qrow"><div class="q">{ic("alarm",26,"#fff",2)}<span>Hatırlat</span></div><div class="q">{ic("mesaj_dolu",26,"#fff")}<span>Mesaj</span></div></div>
<div class="arow"><div class="cb red big"><div style="transform:rotate(135deg)">{ic("telefon",34,"#fff")}</div><span>Reddet</span></div><div class="cb green big"><div>{ic("telefon",34,"#fff")}</div><span>Kabul Et</span></div></div>
''', cls="cs", beyaz=True, not_="Kişinin seçtiği fotoğraf tam ekran, HD. Fotoğraf yavaşça yakınlaşır (canlı efekt). Ad dev yazıyla harf harf belirir, sonra hafifçe 'nefes alır'."))

# B — Kilitli
S.append(phone("v12_02_gelen_kilitli", "2 · Gelen arama (kilitli)", f'''
{poster(FOTO_DENIZ, .1)}
<div class="pname"><div class="psub">mobil</div><div class="bigname">Burak<br>Arslan</div></div>
<div class="qrow" style="bottom:170px"><div class="q">{ic("alarm",26,"#fff",2)}<span>Hatırlat</span></div><div class="q">{ic("mesaj_dolu",26,"#fff")}<span>Mesaj</span></div></div>
<div class="slider"><div class="knob">{ic("telefon",28,"#fff")}</div><span class="shine">kaydırarak cevapla</span></div>
''', cls="cs", beyaz=True, not_="Ekran kilitliyken kaydırma rayı; parıltı soldan sağa akar."))

# C — Görüşme (poster küçülür, bulanır)
S.append(phone("v12_03_gorusme_poster", "3 · Görüşme: poster bulanıklaşır, ad küçülür", f'''
<div class="poster blur" style="background:{FOTO_GUN_BATIMI};background-size:cover"></div><div class="posterdim" style="background:rgba(0,0,0,.35)"></div>
<div class="ctop" style="top:96px">{av("",72,foto=FOTO_GUN_BATIMI)}<div class="cname" style="margin-top:12px">Ayşe Demir</div><div class="cst">03:42</div></div>
<div class="cgrid">{cbtn("hoparlor","hoparlör",True)}{cbtn("beklet","beklet")}{cbtn("sessiz","sessiz")}{cbtn("arti","ekle")}{cbtn("telefon","bitir",kirmizi=True)}{cbtn("tuslar","tuşlar")}</div>
''', cls="cs", beyaz=True, not_="Cevaplanınca dev ad yukarı kayıp küçülür (akıcı geçiş). Butonlar ve dizilim iOS ile aynı: 3×2, kırmızı bitir ortada."))

# D — Arama bitti (bilinmeyen yazmaz)
S.append(phone("v12_04_arama_bitti", "4 · Arama bitti: ad kalır, 'Bilinmeyen' yazmaz", f'''
<div class="poster blur" style="background:{FOTO_GUN_BATIMI};background-size:cover"></div><div class="posterdim" style="background:rgba(0,0,0,.45)"></div>
<div class="ctop" style="top:96px">{av("",72,foto=FOTO_GUN_BATIMI)}<div class="cname" style="margin-top:12px">Ayşe Demir</div><div class="cst">Arama bitti · 03:58</div></div>
<div class="cgrid" style="opacity:.35">{cbtn("hoparlor","hoparlör")}{cbtn("beklet","beklet")}{cbtn("sessiz","sessiz")}{cbtn("arti","ekle")}{cbtn("telefon","bitir",kirmizi=True)}{cbtn("tuslar","tuşlar")}</div>
''', cls="cs", beyaz=True, not_="Hata düzeltmesi: arama bitince son bilinen ad + süre 1 sn gösterilir, sonra ekran kapanır. Rehberde yoksa numara yazılır."))

# E — Kişiler (profesyonel)
kisiler = [("A", [("Ahmet","Yılmaz",None),("Ayşe","Demir",FOTO_GUN_BATIMI)]),("B",[("Burak","Arslan",FOTO_DENIZ)]),("C",[("Can","Öztürk",None)]),("Ç",[("Çağla","Er",FOTO_ORMAN)])]
liste = ""
for h, ks in kisiler:
    liste += f'<div class="sec">{h}</div><div class="list">'
    for i,(a,s,f) in enumerate(ks):
        liste += f'<div class="row {"last" if i==len(ks)-1 else ""}" style="min-height:52px">{av(a[0]+s[0],34,13,f)}<div class="rc"><div class="rt" style="font-weight:400">{a} <b>{s}</b></div></div></div>'
    liste += '</div>'
idx = ''.join(f'<span>{c}</span>' for c in "A B C Ç D E F G H I İ J K L M N O Ö P R S Ş T U Ü V Y Z #".split())
S.append(phone("v12_05_kisiler", "5 · Kişiler (profesyonel)", f'''
<div class="nav"><span class="nb">Listeler</span><span></span><span class="nb r">{ic("arti",22,"var(--mavi)",2.2)}</span></div>
<div class="pad"><div class="lt">Kişiler</div><div class="search">{ic("buyutec",16,"var(--gri)",2.2)}<span>Ara</span></div></div>
<div class="list"><div class="row last" style="padding:8px 0 12px">{av("",58,foto=FOTO_ORMAN)}<div class="rc"><div class="rt" style="font-size:19px">Muhammet Atasoy</div><div class="rs">Kartım · Kişi Posteri</div></div></div></div>
{liste}
<div class="fn" style="text-align:center;padding:14px">312 Kişi</div>
<div class="aidx" style="top:262px">{idx}</div>
<div class="tab">{''.join(f'<div class="ti" style="color:{"var(--mavi)" if i==2 else "var(--gri)"}">{ic(n,26,"var(--mavi)" if i==2 else "var(--gri)")}<span>{l}</span></div>' for i,(n,l) in enumerate([("yildiz_dolu","Favoriler"),("saat_dolu","Son Aramalar"),("kisi_dolu","Kişiler"),("tuslar","Tuş Takımı")]))}</div>
''', not_="Küçük fotoğraf/monogram, soyad kalın, yapışkan harfler, sağda harf dizini, altta toplam kişi sayısı. Ayarlar dişlisi kalktı → 'Listeler' yerine geçer."))

# F — Uzun basma önizleme
S.append(phone("v12_06_uzun_bas_onizleme", "6 · Kişiye uzun bas: önizleme + menü", f'''
<div class="blurall"><div class="nav"><span class="nb">Listeler</span></div><div class="pad"><div class="lt">Kişiler</div></div>{liste}</div>
<div class="dimlight"></div>
<div class="peek"><div class="peekhead" style="background:{FOTO_GUN_BATIMI};background-size:cover"><div class="peekname">Ayşe Demir</div></div>
<div class="peekrow"><span class="fn" style="color:#111">mobil</span><span class="blue" style="font-size:16px">0533 987 65 43</span></div></div>
<div class="menu">
<div class="mi">Ara<span>{ic("telefon",18,"#111")}</span></div><div class="mi">Mesaj<span>{ic("mesaj_dolu",18,"#111")}</span></div><div class="mi">Görüntülü Ara<span>{ic("video_dolu",18,"#111")}</span></div>
<div class="mi sep">Paylaş<span>{ic("paylas",18,"#111",2)}</span></div><div class="mi">Favorilere Ekle<span>{ic("yildiz",18,"#111",2)}</span></div><div class="mi">Düzenle<span>{ic("duzenle",18,"#111",2)}</span></div>
<div class="mi red sep">Kişiyi Sil<span>{ic("engelle",18,"#FF3B30",2)}</span></div></div>
''', not_="Arka plan bulanıklaşır, kişi kartı büyüyerek 'yüzer', altında menü. Dokununca tam kart açılır; dışarı dokununca kapanır. Titreşimle açılır."))

# G — Kişi kartı v2
S.append(phone("v12_07_kisi_karti", "7 · Kişi kartı: poster başlıklı", f'''
<div class="grp-bg" style="padding-top:0">
<div class="cardhead" style="background:{FOTO_GUN_BATIMI};background-size:cover"><div class="nav" style="color:#fff;padding-top:40px;height:84px"><span class="nb" style="color:#fff">{ic("geri",22,"#fff",2.4)}Kişiler</span><span></span><span class="nb r" style="color:#fff">Düzenle</span></div>
<div class="chname">Ayşe Demir</div>
<div class="acts glass">{''.join(f'<div class="act">{ic(i,22,"#fff")}<span>{t}</span></div>' for i,t in [("mesaj_dolu","mesaj"),("telefon","ara"),("video_dolu","video"),("eposta_dolu","posta")])}</div></div>
<div class="grp" style="margin-top:16px"><div class="row"><div class="rc"><div class="lbl">mobil</div><div class="blue">0533 987 65 43</div></div></div><div class="row last"><div class="rc"><div class="lbl">e-posta</div><div class="blue">ayse@ornek.com</div></div></div></div>
<div class="grp"><div class="row"><span class="blue">Kişi Posteri ve Fotoğraf</span></div><div class="row"><span class="blue">Mesaj Gönder</span></div><div class="row"><span class="blue">Kişiyi Paylaş</span></div><div class="row last"><span class="blue">Favorilere Ekle</span></div></div>
<div class="grp"><div class="row last"><span style="color:var(--kirmizi)">Bu Arayanı Engelle</span></div></div>
</div>''', not_="Kartın üstü kişinin posteri; ad posterin üstünde, eylem butonları buzlu cam."))

# H — Kişi düzenle / yeni kişi (uygulama içinden)
def alan(etiket, deger="", ph=False):
    return f'<div class="row"><div class="rc"><span style="color:{"var(--gri3)" if ph else "var(--etiket)"};font-size:17px">{deger if deger else etiket}</span></div></div>'
S.append(phone("v12_08_kisi_duzenle", "8 · Yeni kişi / düzenle (uygulama içinde)", f'''
<div class="grp-bg">
<div class="nav"><span class="nb">Vazgeç</span><span class="nt">Yeni Kişi</span><span class="nb r"><b>Bitti</b></span></div>
<div style="text-align:center;margin:6px 0 14px"><div style="display:flex;justify-content:center">{av("",104,foto=FOTO_GUN_BATIMI)}</div><div class="blue" style="font-size:15px;margin-top:8px">Fotoğrafı Düzenle</div></div>
<div class="grp">{alan("Ad","Ayşe")}{alan("Soyad","Demir")}{alan("Şirket","",True).replace('class="row"','class="row last"')}</div>
<div class="grp"><div class="row"><span class="minus">−</span><span class="lbl2">mobil ›</span><span style="font-size:17px">0533 987 65 43</span></div><div class="row last"><span class="plus">+</span><span style="font-size:17px">telefon ekle</span></div></div>
<div class="grp"><div class="row last"><span class="plus">+</span><span style="font-size:17px">e-posta ekle</span></div></div>
<div class="grp"><div class="row last"><span class="sq" style="background:linear-gradient(135deg,#FF9500,#FF2D55)">{ic("cihaz",17,"#fff",2.2)}</span><div class="rc"><div class="rt" style="font-weight:400">Kişi Posteri</div><div class="rs">Arama ekranı arka planı</div></div><span class="mini" style="width:34px;height:52px;border-radius:7px;background:{FOTO_GUN_BATIMI};background-size:cover"></span></div></div>
</div>''', not_="Sistem rehber ekranına gitmez. Fotoğraf: sistem fotoğraf seçici (izin gerektirmez), kırpma dairesi. Kayıt Android rehberine yazılır."))

# I — Kişi posteri düzenleyici
S.append(phone("v12_09_poster_duzenle", "9 · Kişi posteri düzenleyici", f'''
<div class="grp-bg" style="background:#111">
<div class="nav" style="color:#fff"><span class="nb" style="color:#0A84FF">Vazgeç</span><span class="nt" style="color:#fff">Kişi Posteri</span><span class="nb r" style="color:#0A84FF"><b>Bitti</b></span></div>
<div class="mini-phone"><div class="poster" style="background:{FOTO_GUN_BATIMI};background-size:cover;border-radius:28px"></div><div class="bigname" style="font-size:44px;top:40px;position:absolute;left:0;right:0;text-align:center">Ayşe<br>Demir</div>
<div style="position:absolute;bottom:22px;left:0;right:0;display:flex;justify-content:space-around"><i class="dotr"></i><i class="dotg"></i></div></div>
<div class="tools"><div class="tool on">{ic("kart",22,"#fff",2)}<span>Fotoğraf</span></div><div class="tool">{ic("palet",22,"#fff",2)}<span>Renk</span></div><div class="tool">{ic("tema",22,"#fff",2)}<span>Yazı</span></div></div>
<div class="opts"><span class="sw2" style="background:{FOTO_GUN_BATIMI}"></span><span class="sw2" style="background:{FOTO_DENIZ}"></span><span class="sw2" style="background:{FOTO_ORMAN}"></span><span class="sw2" style="background:linear-gradient(#3a3a3c,#1c1c1e)"></span><span class="sw2 add">+</span></div>
<div class="toggle"><span>Canlı efekt (yavaş yakınlaşma)</span><span class="sw on"><i></i></span></div>
</div>''', not_="Fotoğraf seç → sürükle/yakınlaştır ile hizala. Renk: düz/gradyan zemin. Yazı: ad yazı tipi kalınlığı. Önizleme gerçek gelen arama ekranıdır."))

# J — Ayarlar v2
def ayar(renk, icn, t, sag="", son=False, ok=True):
    o = f'<span class="chev">{ic("ileri",14,"var(--gri3)",2.6)}</span>' if ok else ""
    return f'<div class="row {"last" if son else ""}"><span class="sq" style="background:{renk}">{ic(icn,17,"#fff",2.2)}</span><div class="rc"><div class="rt" style="font-weight:400">{t}</div></div><span class="rr">{sag}</span>{o}</div>'
sw = lambda on: f'<span class="sw {"on" if on else ""}"><i></i></span>'
S.append(phone("v12_10_ayarlar", "10 · Ayarlar (sade, gerçekçi)", f'''
<div class="grp-bg">
<div class="nav"><span class="nb">{ic("geri",22,"var(--mavi)",2.4)}Geri</span></div>
<div class="pad"><div class="lt">Telefon</div></div>
<div class="sh">ARAMALAR</div>
<div class="grp">{ayar("#34C759","cihaz","Varsayılan Telefon Uygulaması","Eymen")}{ayar("#007AFF","mesaj_dolu","Mesajla Yanıtla","",True)}</div>
<div class="sh">SES VE TİTREŞİM</div>
<div class="grp"><div class="row"><span class="sq" style="background:#FF2D55">{ic("titresim",17,"#fff",2.2)}</span><div class="rc"><div class="rt" style="font-weight:400">Tuş Takımında Titreşim</div></div>{sw(True)}</div>
<div class="row last"><span class="sq" style="background:#FF9500">{ic("hoparlor",17,"#fff",2)}</span><div class="rc"><div class="rt" style="font-weight:400">Tuş Takımı Sesleri</div></div>{sw(True)}</div></div>
<div class="sh">ENGELLEME</div>
<div class="grp">{ayar("#FF3B30","engelle","Engellenen Kişiler","3")}<div class="row last"><span class="sq" style="background:#5856D6">{ic("bildirim",17,"#fff",2.2)}</span><div class="rc"><div class="rt" style="font-weight:400">Bilinmeyen Arayanları Sustur</div></div>{sw(False)}</div></div>
<p class="fn" style="padding:6px 32px">Rehberde olmayan numaralar çalmadan Son Aramalar'a düşer.</p>
<div class="sh">DESTEK</div>
<div class="grp">{ayar("#8E8E93","dosya","Hata Kayıtları","0")}{ayar("#8E8E93","bilgi","Hakkında","1.2.0",True)}</div>
</div>''', not_="İzinler ve Tema satırları kalktı: tema sistemden otomatik (açık/koyu), izinler sadece girişte."))

# K — Engellenen kişiler
S.append(phone("v12_11_engellenenler", "11 · Engellenen Kişiler", f'''
<div class="grp-bg">
<div class="nav"><span class="nb">{ic("geri",22,"var(--mavi)",2.4)}Telefon</span><span></span><span class="nb r">Düzenle</span></div>
<div class="pad"><div class="lt" style="font-size:30px">Engellenen Kişiler</div></div>
<div class="grp" style="margin-top:16px"><div class="row">{av("MÇ",34,13)}<div class="rc"><div class="rt" style="font-weight:400">Mehmet Çelik</div><div class="rs">0532 555 44 33</div></div></div>
<div class="row">{av("#",34,13)}<div class="rc"><div class="rt" style="font-weight:400">0850 111 22 33</div></div></div>
<div class="row last"><span class="blue">Yeni Ekle…</span></div></div>
<p class="fn" style="padding:8px 32px">Engellenen kişiler sizi arayamaz; aramaları Son Aramalar'da "engellendi" olarak görünür.</p>
</div>''', not_="Yeni Ekle… → rehberden kişi seç ya da numara yaz. Sola kaydır → Engeli Kaldır."))

# L — Tek seferlik izin girişi
S.append(phone("v12_12_izin_girisi", "12 · İzinler sadece girişte (hepsi zorunlu)", f'''
<div class="pad" style="padding-top:60px;text-align:center"><div style="width:84px;height:84px;margin:0 auto">{ICON}</div>
<div class="lt" style="margin-top:20px;font-size:30px">Eymen Telefon'u<br>Varsayılan Yap</div>
<p class="fn" style="margin:12px 10px 0;font-size:15px">Aramaları yönetmek, kişilerini göstermek ve gelen aramayı ekranda göstermek için tek seferlik izin gerekiyor.</p></div>
<div class="pad" style="margin-top:26px">
<div class="step ok">{ic("onay",16,"#fff",3)}<span>Varsayılan telefon uygulaması</span></div>
<div class="step ok">{ic("onay",16,"#fff",3)}<span>Telefon ve aramalar</span></div>
<div class="step now"><i></i><span>Kişiler</span></div>
<div class="step"><i></i><span>Arama geçmişi</span></div>
<div class="step"><i></i><span>Bildirimler</span></div></div>
<div class="bottom"><div class="btn">Devam (3 / 5)</div><p class="fn" style="text-align:center;margin-top:12px">İzinler tamamlanmadan uygulama açılmaz.</p></div>
''', not_="Tek buton sırayla tüm izinleri ister. Biri reddedilirse 'Ayarlar'da izin ver' yolu gösterilir. Hepsi bitince bu ekran bir daha görünmez; uygulama içinde izin ekranı yok."))

# M — Telefon/rehber yönlendirme
S.append(phone("v12_13_yonlendirme", "13 · Sistemin arama/rehber tuşu → Eymen", f'''
<div class="dimlight" style="background:rgba(0,0,0,.35)"></div>
<div class="sysdlg"><div class="sdt">Varsayılan telefon uygulaması olarak Eymen Telefon ayarlansın mı?</div>
<div class="sdr sel">{av("",30,foto="linear-gradient(135deg,#19D3A2,#3D7BFF)")}<span>Eymen Telefon</span><i class="radio on"></i></div>
<div class="sdr">{av("",30,foto="linear-gradient(135deg,#9AA0A6,#5F6368)")}<span>Telefon (sistem)</span><i class="radio"></i></div>
<div class="sdb"><span>İptal</span><span><b>Varsayılan olarak ayarla</b></span></div></div>
<div class="sysdlg" style="top:auto;bottom:40px"><div class="sdt" style="font-size:14px">Kişiyi şununla aç</div>
<div class="sdr sel">{av("",30,foto="linear-gradient(135deg,#19D3A2,#3D7BFF)")}<span>Eymen Telefon</span></div>
<div class="sdb"><span>Yalnızca bir kez</span><span><b>Her zaman</b></span></div></div>
''', not_="Varsayılan olunca: başka uygulamalardaki numaralar, telefon tuşu, rehberdeki 'ara' → Eymen'e gelir. Kişi açma/ekleme/düzenleme isteklerini de karşılarız; Android ilk seferde sorar, 'Her zaman' seçilir."))

EK = r'''
.poster{position:absolute;inset:0;animation:kb 18s ease-in-out infinite alternate}
.poster.blur{filter:blur(18px) brightness(.8);transform:scale(1.2);animation:none}
@keyframes kb{from{transform:scale(1)}to{transform:scale(1.12)}}
.posterdim{position:absolute;inset:0}
.pname{position:absolute;top:84px;left:0;right:0;text-align:center;color:#fff;z-index:3}
.psub{font-size:16px;opacity:.85;margin-bottom:6px}
.bigname{font-size:64px;font-weight:800;line-height:.98;letter-spacing:-1.5px;color:#fff;text-shadow:0 4px 24px rgba(0,0,0,.25)}
.not{max-width:340px;font-size:12.5px;color:#555;line-height:1.4;text-align:center}
.wrap{grid-template-columns:repeat(4,360px)!important}
.blurall{filter:blur(6px);opacity:.7}
.dimlight{position:absolute;inset:0;background:rgba(0,0,0,.12);z-index:4}
.peek{position:absolute;top:150px;left:34px;right:34px;background:#fff;border-radius:16px;overflow:hidden;z-index:6;box-shadow:0 20px 50px rgba(0,0,0,.3)}
.peekhead{height:170px;position:relative}.peekname{position:absolute;bottom:12px;left:0;right:0;text-align:center;color:#fff;font-size:28px;font-weight:700;text-shadow:0 2px 12px rgba(0,0,0,.4)}
.peekrow{padding:12px 16px;display:flex;flex-direction:column;gap:2px}
.menu{position:absolute;top:420px;left:34px;width:250px;background:rgba(250,250,250,.97);border-radius:14px;z-index:6;overflow:hidden;box-shadow:0 20px 50px rgba(0,0,0,.25)}
.mi{display:flex;justify-content:space-between;align-items:center;padding:11px 16px;font-size:16px;border-top:.5px solid #D1D1D6;color:#111}.mi:first-child{border-top:none}
.mi.sep{border-top:7px solid #E5E5EA}.mi.red{color:#FF3B30}
.cardhead{height:330px;position:relative;color:#fff}
.chname{position:absolute;top:150px;left:0;right:0;text-align:center;font-size:32px;font-weight:700;text-shadow:0 2px 14px rgba(0,0,0,.35)}
.acts.glass{position:absolute;bottom:14px;left:0;right:0;margin:0 16px}.acts.glass .act{background:rgba(255,255,255,.22);color:#fff;backdrop-filter:blur(14px)}
.minus{width:22px;height:22px;border-radius:50%;background:#FF3B30;color:#fff;display:flex;align-items:center;justify-content:center;font-weight:700}
.plus{width:22px;height:22px;border-radius:50%;background:#34C759;color:#fff;display:flex;align-items:center;justify-content:center;font-weight:700}
.lbl2{color:var(--mavi);font-size:15px;min-width:64px}
.mini-phone{position:relative;width:200px;height:400px;margin:14px auto 0;border-radius:28px;overflow:hidden;box-shadow:0 0 0 4px #333}
.mini-phone .bigname{font-size:38px}
.dotr{width:38px;height:38px;border-radius:50%;background:#FF3B30}.dotg{width:38px;height:38px;border-radius:50%;background:#34C759}
.tools{display:flex;justify-content:center;gap:16px;margin-top:20px}.tool{display:flex;flex-direction:column;align-items:center;gap:4px;color:#fff;font-size:12px;padding:8px 14px;border-radius:12px;background:#2C2C2E}.tool.on{background:#0A84FF}
.opts{display:flex;justify-content:center;gap:10px;margin-top:16px}.sw2{width:44px;height:44px;border-radius:10px;background-size:cover}.sw2.add{background:#2C2C2E;color:#fff;display:flex;align-items:center;justify-content:center;font-size:22px}
.toggle{display:flex;justify-content:space-between;align-items:center;margin:18px 16px 0;padding:10px 14px;border-radius:10px;background:#1C1C1E;color:#fff;font-size:15px}
.step{display:flex;align-items:center;gap:12px;padding:12px 4px;border-bottom:.5px solid var(--ayirici);font-size:16px}
.step i{width:22px;height:22px;border-radius:50%;border:2px solid #C7C7CC}.step.ok svg{background:#34C759;border-radius:50%;padding:3px;width:22px;height:22px}
.step.now i{border-color:#007AFF;box-shadow:inset 0 0 0 4px #fff,inset 0 0 0 11px #007AFF}.step.now span{font-weight:600}
.sysdlg{position:absolute;top:150px;left:22px;right:22px;background:#fff;border-radius:24px;z-index:6;padding:18px 18px 8px;box-shadow:0 20px 50px rgba(0,0,0,.3)}
.sdt{font-size:17px;font-weight:600;margin-bottom:10px;color:#111;line-height:1.3}
.sdr{display:flex;align-items:center;gap:12px;padding:10px 4px;font-size:15px;color:#111}.sdr span{flex:1}
.radio{width:20px;height:20px;border-radius:50%;border:2px solid #9AA0A6}.radio.on{border-color:#1A73E8;box-shadow:inset 0 0 0 4px #fff,inset 0 0 0 10px #1A73E8}
.sdb{display:flex;justify-content:flex-end;gap:24px;padding:12px 4px;color:#1A73E8;font-size:14px}
.ctop .av{margin:0 auto}

.bigname.anim span{display:inline-block;opacity:0;transform:translateY(18px);animation:harf .6s cubic-bezier(.2,.8,.2,1) forwards}
.bigname.anim{animation:nefes 3.2s ease-in-out 1.2s infinite}
@keyframes harf{to{opacity:1;transform:none}}
@keyframes nefes{0%,100%{transform:scale(1)}50%{transform:scale(1.035)}}
.shine{background-size:200% 100%;animation:parilti 2.2s linear infinite}
@keyframes parilti{from{background-position:150% 0}to{background-position:-50% 0}}
'''

html = f'''<!doctype html><html><head><meta charset="utf-8"><style>{CSS}{EK}</style></head><body>
<div class="wrap"><div class="title"><div style="width:56px;height:56px">{ICON}</div><div><h1>Eymen Telefon v1.2 — tasarım önerisi (onay bekliyor)</h1><p>Kişi posteri · canlı ad · uygulama içinde kişi düzenleme · uzun basma önizleme · sade ayarlar · izinler sadece girişte</p></div></div>
{''.join(S)}</div></body></html>'''

gezgin = f'''<!doctype html><html lang="tr"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Eymen Telefon v1.2 — ekran önizlemesi</title><style>{CSS}{EK}
body{{margin:0;padding:0;background:#E9EAEE;min-height:100vh;display:flex;flex-direction:column;align-items:center;font-family:Inter,Roboto,system-ui,sans-serif}}
.ust{{width:100%;max-width:520px;display:flex;align-items:center;justify-content:space-between;padding:14px 16px;box-sizing:border-box}}
.ust b{{font-size:15px}}.ust span{{color:#666;font-size:13px}}
.sahne{{flex:1;display:flex;align-items:center;justify-content:center;width:100%}}
.cell{{display:none}}.cell.goster{{display:flex}}
.olcek{{transform-origin:top center}}
.alt{{display:flex;gap:12px;padding:12px 0 22px}}
.alt button{{border:0;border-radius:22px;padding:12px 22px;font-size:16px;font-weight:600;background:#007AFF;color:#fff}}
.alt button.ikincil{{background:#fff;color:#007AFF}}
.noktalar{{display:flex;gap:6px;justify-content:center;flex-wrap:wrap;max-width:360px}}.noktalar i{{width:8px;height:8px;border-radius:4px;background:#C7C7CC}}.noktalar i.a{{background:#007AFF;width:20px}}
</style></head><body>
<div class="ust"><b>Eymen Telefon v1.2</b><span id="sayac"></span></div>
<div class="sahne"><div class="olcek" id="olcek">{''.join(S)}</div></div>
<div class="noktalar" id="noktalar"></div>
<div class="alt"><button class="ikincil" onclick="git(-1)">‹ Önceki</button><button onclick="git(1)">Sonraki ›</button></div>
<script>
const h=[...document.querySelectorAll('.cell')];let i=0;const n=document.getElementById('noktalar');
h.forEach(()=>n.appendChild(document.createElement('i')));
function olcekle(){{const y=window.innerHeight-200,w=window.innerWidth-20;const k=Math.min(1,y/900,w/380);document.getElementById('olcek').style.transform='scale('+k+')';document.getElementById('olcek').style.height=(900*k)+'px';}}
function goster(){{h.forEach((c,j)=>c.classList.toggle('goster',j===i));[...n.children].forEach((d,j)=>d.classList.toggle('a',j===i));document.getElementById('sayac').textContent=(i+1)+' / '+h.length;
const kopya=h[i].cloneNode(true);h[i].replaceWith(kopya);h[i]=kopya;}}
function git(d){{i=(i+d+h.length)%h.length;goster();}}
let x0=null;document.addEventListener('touchstart',e=>x0=e.touches[0].clientX);document.addEventListener('touchend',e=>{{if(x0===null)return;const dx=e.changedTouches[0].clientX-x0;if(Math.abs(dx)>50)git(dx<0?1:-1);x0=null;}});
document.addEventListener('keydown',e=>{{if(e.key==='ArrowRight')git(1);if(e.key==='ArrowLeft')git(-1);}});
window.addEventListener('resize',olcekle);olcekle();goster();
</script></body></html>'''
(KOK.parent / "docs" / "v12_onizleme.html").write_text(gezgin)

OUT.mkdir(parents=True, exist_ok=True)
with sync_playwright() as p:
    b = p.chromium.launch(executable_path="/usr/bin/google-chrome", args=["--no-sandbox"])
    pg = b.new_page(viewport={"width": 1700, "height": 1000}, device_scale_factor=2)
    pg.set_content(html); pg.wait_for_timeout(400)
    for el in pg.query_selector_all(".phone"):
        el.screenshot(path=str(OUT / f'{el.get_attribute("id")}.png'), animations="disabled")
    pg2 = b.new_page(viewport={"width": 1700, "height": 1000}, device_scale_factor=1)
    pg2.set_content(html); pg2.wait_for_timeout(400)
    pg2.screenshot(path=str(OUT / "v12_00_genel_bakis.png"), full_page=True, animations="disabled")
    b.close()
print("ok", len(S))
