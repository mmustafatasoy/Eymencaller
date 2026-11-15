#!/usr/bin/env python3
"""Eymen Telefon — iOS tarzı ekran tasarımları. Kullanım: python3 mockup_uretici_ios.py <çıkış klasörü> <simge.svg>"""
import sys, pathlib
sys.path.insert(0, str(pathlib.Path(__file__).parent))
from ikonlar import svg as ic
from playwright.sync_api import sync_playwright

OUT = pathlib.Path(sys.argv[1]); ICON = pathlib.Path(sys.argv[2]).read_text()

def av(ini, size=40, fs=None):
    fs = fs or int(size * 0.4)
    return f'<div class="av" style="width:{size}px;height:{size}px;font-size:{fs}px">{ini}</div>'

def sb(koyu=False, beyaz=False):
    c = "#fff" if (koyu or beyaz) else "#000"
    return f'''<div class="sb" style="color:{c}"><span>9:41</span><i class="cam"></i><span class="sbr">
<svg width="17" height="11" viewBox="0 0 17 11"><rect x="0" y="7" width="3" height="4" rx="1" fill="{c}"/><rect x="4.5" y="5" width="3" height="6" rx="1" fill="{c}"/><rect x="9" y="2.5" width="3" height="8.5" rx="1" fill="{c}"/><rect x="13.5" y="0" width="3" height="11" rx="1" fill="{c}"/></svg>
<svg width="25" height="12" viewBox="0 0 25 12"><rect x=".5" y=".5" width="21" height="11" rx="3.5" fill="none" stroke="{c}" opacity=".4"/><rect x="2" y="2" width="16" height="8" rx="2" fill="{c}"/><rect x="22.5" y="4" width="1.8" height="4" rx=".9" fill="{c}" opacity=".4"/></svg></span></div>'''

def tab(aktif):
    items = [("yildiz_dolu","Favoriler"),("saat_dolu","Son Aramalar"),("kisi_dolu","Kişiler"),("tuslar","Tuş Takımı")]
    o = '<div class="tab">'
    for i,(icn,l) in enumerate(items):
        renk = "var(--mavi)" if i == aktif else "var(--gri)"
        o += f'<div class="ti" style="color:{renk}">{ic(icn,26,renk)}<span>{l}</span></div>'
    return o + '</div>'

def nav(sol="", sag="", orta=""):
    return f'<div class="nav"><span class="nb">{sol}</span><span class="nt">{orta}</span><span class="nb r">{sag}</span></div>'

def phone(sid, baslik, govde, koyu=False, cls="", beyazsb=False):
    return f'<div class="cell"><div class="phone {"dark" if koyu else ""} {cls}" id="{sid}">{sb(koyu, beyazsb)}{govde}<div class="home"></div></div><div class="cap">{baslik}</div></div>'

def satir(ad, alt, sag="", kirmizi=False, avatar=None, bilgi=True, son=False):
    a = av(avatar, 40) if avatar else ""
    b = f'<span class="info">{ic("bilgi",22,"var(--mavi)",1.6)}</span>' if bilgi else ""
    return f'<div class="row {"last" if son else ""}">{a}<div class="rc"><div class="rt" style="{"color:var(--kirmizi)" if kirmizi else ""}">{ad}</div><div class="rs">{alt}</div></div><span class="rr">{sag}</span>{b}</div>'

S = []

# 01 Karşılama
S.append(phone("01_karsilama", "01 · Karşılama", f'''
<div class="pad" style="padding-top:56px;text-align:center">
 <div style="width:92px;height:92px;margin:0 auto">{ICON}</div>
 <div class="lt" style="margin-top:22px;line-height:1.1">Eymen Telefon'a<br>Hoş Geldiniz</div>
</div>
<div class="pad" style="margin-top:34px">
 <div class="feat">{ic("telefon",30,"var(--mavi)")}<div><b>Akıllı tuş takımı</b><p>Yazdıkça rehberden kişiyi bulur, numarayı biçimler.</p></div></div>
 <div class="feat">{ic("kisi_dolu",30,"var(--mavi)")}<div><b>Rehber ve favoriler</b><p>Türkçe sıralı kişiler, hızlı arama ve kişi kartları.</p></div></div>
 <div class="feat">{ic("kalkan",30,"var(--mavi)",2)}<div><b>Gizlilik ve engelleme</b><p>Verilerin telefonunda kalır; istenmeyen aramalar çalmaz.</p></div></div>
</div>
<div class="bottom"><p class="fn" style="text-align:center;margin-bottom:14px">Verileriniz cihazdan çıkmaz. <span style="color:var(--mavi)">Gizlilik</span></p><div class="btn">Devam</div></div>
'''))

# 02 İzinler
def izin(renk, icn, t, d, ok):
    sag = f'<span class="ok">{ic("onay",18,"var(--yesil)",3)}</span>' if ok else '<span class="cap2">İzin Ver</span>'
    return f'<div class="row"><span class="sq" style="background:{renk}">{ic(icn,18,"#fff",2.2)}</span><div class="rc"><div class="rt">{t}</div><div class="rs">{d}</div></div>{sag}</div>'
S.append(phone("02_izinler", "02 · İzinler (açılıştan önce)", f'''
<div class="grp-bg">
{nav()}
<div class="pad"><div class="lt">İzinler</div><p class="fn" style="margin:6px 0 18px">Arama yapabilmek ve kişilerini gösterebilmek için aşağıdaki izinler gerekiyor.</p></div>
<div class="sh">ZORUNLU · 3 / 4</div>
<div class="grp">
 {izin("#34C759","cihaz","Varsayılan Telefon Uygulaması","Aramaları bu uygulama yönetir",False)}
 {izin("#34C759","telefon","Telefon","Arama yapma ve cevaplama",True)}
 {izin("#8E8E93","kart","Kişiler","Rehberi göster ve düzenle",True)}
 {izin("#007AFF","saat_dolu","Arama Geçmişi","Son aramaları göster",True).replace('class="row"','class="row last"')}
</div>
<div class="sh">İSTEĞE BAĞLI</div>
<div class="grp">{izin("#FF3B30","bildirim","Bildirimler","Gelen ve cevapsız aramalar",False).replace('class="row"','class="row last"')}</div>
<p class="fn pad" style="margin-top:8px">İzinleri daha sonra Ayarlar › İzinler'den değiştirebilirsin.</p>
<div class="bottom"><div class="btn dis">Devam</div></div>
</div>'''))

# 03 Favoriler
S.append(phone("03_favoriler", "03 · Favoriler", f'''
{nav(ic("disli",22,"var(--mavi)"), ic("arti",22,"var(--mavi)",2.2))}
<div class="pad"><div class="lt">Favoriler</div></div>
<div class="list" style="margin-top:6px">
 {satir("Annem", f'<span class="mini">{ic("telefon",12,"var(--gri)")}</span>mobil', avatar="A")}
 {satir("Ayşe Demir", f'<span class="mini">{ic("telefon",12,"var(--gri)")}</span>mobil', avatar="AD")}
 {satir("Burak Arslan", f'<span class="mini">{ic("video_dolu",12,"var(--gri)")}</span>görüntülü', avatar="BA")}
 {satir("Elif Kaya", f'<span class="mini">{ic("telefon",12,"var(--gri)")}</span>iş', avatar="EK", son=True)}
</div>
{tab(0)}'''))

# 04 Son aramalar
def son(ad, alt, saat, kirmizi=False, ini="", sayi=""):
    t = f'{ad} <span style="color:var(--gri);font-weight:400">{sayi}</span>' if sayi else ad
    return satir(t, f'<span class="mini">{ic("telefon",12,"var(--gri)")}</span>{alt}', f'<span class="time">{saat}</span>', kirmizi, ini)
son_govde = f'''
<div class="nav"><span class="nb">{ic("disli",22,"var(--mavi)")}</span><span class="seg"><b>Tümü</b><span>Cevapsız</span></span><span class="nb r">Düzenle</span></div>
<div class="pad"><div class="lt">Son Aramalar</div></div>
<div class="list" style="margin-top:6px">
 {son("Ayşe Demir","mobil","20:31",ini="AD")}
 {son("Mehmet Çelik","mobil","19:05",True,"MÇ","(2)")}
 {son("0212 555 01 23","İstanbul","17:42",ini="#")}
 {son("Elif Kaya","iş","Dün",ini="EK")}
 {son("Burak Arslan","mobil","Dün",ini="BA")}
 {son("Zeynep Şahin","mobil","Cumartesi",True,"ZŞ")}
 {son("Can Öztürk","mobil","12.11.26",ini="CÖ",sayi="(3)")}
</div>
{tab(1)}'''
S.append(phone("04_son_aramalar", "04 · Son Aramalar", son_govde))
S.append(phone("05_son_aramalar_koyu", "05 · Son Aramalar (koyu)", son_govde, koyu=True))

# 06 Kişiler
alpha = "A B C Ç D E F G H I İ J K L M N O Ö P R S Ş T U Ü V Y Z #".split()
idx = ''.join(f'<span>{c}</span>' for c in alpha)
def kisi(ad, soyad, son=False):
    return f'<div class="row {"last" if son else ""}" style="min-height:44px"><div class="rc"><div class="rt" style="font-weight:400">{ad} <b>{soyad}</b></div></div></div>'
S.append(phone("06_kisiler", "06 · Kişiler", f'''
{nav(ic("disli",22,"var(--mavi)"), ic("arti",22,"var(--mavi)",2.2))}
<div class="pad"><div class="lt">Kişiler</div>
<div class="search">{ic("buyutec",16,"var(--gri)",2.2)}<span>Ara</span></div></div>
<div class="list">
 <div class="row last" style="padding:10px 0">{av("MA",58,22)}<div class="rc"><div class="rt" style="font-size:19px">Muhammet Atasoy</div><div class="rs">Kartım</div></div></div>
</div>
<div class="sec">A</div>
<div class="list">{kisi("Ahmet","Yılmaz")}{kisi("Ayşe","Demir",True)}</div>
<div class="sec">B</div>
<div class="list">{kisi("Burak","Arslan",True)}</div>
<div class="sec">C</div>
<div class="list">{kisi("Can","Öztürk",True)}</div>
<div class="sec">Ç</div>
<div class="list">{kisi("Çağla","Er",True)}</div>
<div class="aidx">{idx}</div>
{tab(2)}'''))

# 07 Kişi kartı
def eylem(icn, t):
    return f'<div class="act">{ic(icn,22,"var(--mavi)")}<span>{t}</span></div>'
S.append(phone("07_kisi_karti", "07 · Kişi Kartı", f'''
<div class="grp-bg">
<div class="nav"><span class="nb">{ic("geri",22,"var(--mavi)",2.4)}Kişiler</span><span></span><span class="nb r">Düzenle</span></div>
<div style="text-align:center;margin-top:6px"><div style="display:flex;justify-content:center">{av("AD",112,44)}</div><div style="font-size:28px;font-weight:600;margin-top:10px">Ayşe Demir</div><div class="fn">Atasoy Yazılım</div></div>
<div class="acts">{eylem("mesaj_dolu","mesaj")}{eylem("telefon","ara")}{eylem("video_dolu","video")}{eylem("eposta_dolu","posta")}</div>
<div class="grp"><div class="row"><div class="rc"><div class="lbl">mobil</div><div class="blue">0533 987 65 43</div></div></div><div class="row last"><div class="rc"><div class="lbl">iş</div><div class="blue">0212 444 10 20</div></div></div></div>
<div class="grp"><div class="row last"><div class="rc"><div class="lbl">Son Aramalar</div><div class="rt" style="font-size:15px;font-weight:400">Bugün 20:31 · Gelen · 4 dk 12 sn</div></div></div></div>
<div class="grp"><div class="row"><span class="blue">Mesaj Gönder</span></div><div class="row"><span class="blue">Kişiyi Paylaş</span></div><div class="row last"><span class="blue">Favorilere Ekle</span></div></div>
<div class="grp"><div class="row last"><span style="color:var(--kirmizi)">Bu Arayanı Engelle</span></div></div>
</div>'''))

# 08 / 09 Tuş takımı
keys = [("1",""),("2","ABCÇ"),("3","DEF"),("4","GĞHIİ"),("5","JKL"),("6","MNOÖ"),("7","PQRSŞ"),("8","TUÜV"),("9","WXYZ"),("*",""),("0","+"),("#","")]
kp = ''.join(f'<div class="key"><b>{k}</b><small>{l}</small></div>' for k,l in keys)
tus = f'''
<div style="height:150px;display:flex;flex-direction:column;align-items:center;justify-content:flex-end">
 <div class="num">0532 123 45 67</div><div class="blue" style="font-size:15px;margin-top:4px">Ahmet Yılmaz · mobil</div></div>
<div class="kp" style="margin-top:22px">{kp}</div>
<div class="kp" style="margin-top:14px"><div></div><div class="call">{ic("telefon",34,"#fff")}</div><div class="del">{ic("sil",30,"var(--gri2)",1.8)}</div></div>
{tab(3)}'''
S.append(phone("08_tus_takimi", "08 · Tuş Takımı", tus))
S.append(phone("09_tus_takimi_koyu", "09 · Tuş Takımı (koyu)", tus, koyu=True))

# 10 Arama
S.append(phone("10_arama", "10 · Arama", f'''
<div class="pad" style="padding-top:8px;display:flex;gap:10px;align-items:center"><div class="search on" style="flex:1;margin:0">{ic("buyutec",16,"var(--gri)",2.2)}<span style="color:var(--etiket)">ay</span><i class="caret"></i></div><span class="blue">Vazgeç</span></div>
<div class="sec2">KİŞİLER</div>
<div class="list">{satir("<b>Ay</b>şe Demir","mobil 0533 987 65 43",avatar="AD",bilgi=False)}{satir("Deniz <b>Ay</b>dın","mobil 0542 300 20 10",avatar="DA",bilgi=False,son=True)}</div>
<div class="sec2">SON ARAMALAR</div>
<div class="list">{satir("<b>Ay</b>şe Demir","Gelen · Bugün 20:31",avatar="AD",bilgi=False,son=True)}</div>
<div class="kb">{''.join('<div class="kbr">'+''.join(f'<span>{c}</span>' for c in r.split())+'</div>' for r in ["q w e r t y u ı o p ğ ü","a s d f g h j k l ş i","z x c v b n m ö ç"])}<div class="kbr"><span class="w">123</span><span class="sp">boşluk</span><span class="w acc">ara</span></div></div>'''))

# Arama ekranları
def cbtn(icn, t, aktif=False, kirmizi=False):
    cls = "cb red" if kirmizi else ("cb on" if aktif else "cb")
    renk = "#fff" if not aktif else "#000"
    rot = ' style="transform:rotate(135deg)"' if kirmizi else ""
    return f'<div class="{cls}"><div{rot}>{ic(icn,30,renk,2)}</div><span>{t}</span></div>'
grid = lambda a=False: f'''<div class="cgrid">{cbtn("hoparlor","hoparlör",a)}{cbtn("beklet","beklet")}{cbtn("sessiz","sessiz")}{cbtn("arti","ekle")}{cbtn("telefon","bitir",kirmizi=True)}{cbtn("tuslar","tuşlar")}</div>'''
S.append(phone("11_giden_arama", "11 · Giden Arama", f'''
<div class="callbg g1"></div>
<div class="ctop"><div class="cname">Annem</div><div class="cst">arıyor…</div></div>
{grid()}''', cls="cs", beyazsb=True))
S.append(phone("12_gorusme", "12 · Görüşme", f'''
<div class="callbg g2"></div>
<div class="ctop"><div class="cname">Ayşe Demir</div><div class="cst">03:42 · HD</div></div>
{grid(True)}''', cls="cs", beyazsb=True))
kp2 = ''.join(f'<div class="key k2"><b>{k}</b><small>{l}</small></div>' for k,l in keys)
S.append(phone("13_gorusme_tuslar", "13 · Görüşmede Tuş Takımı", f'''
<div class="callbg g2"></div>
<div class="ctop" style="top:70px"><div class="cname" style="font-size:30px">*121#</div><div class="cst">Ayşe Demir · 03:58</div></div>
<div class="kp" style="position:absolute;top:210px;left:0;right:0">{kp2}</div>
<div class="kp" style="position:absolute;bottom:48px;left:0;right:0"><div></div><div class="cb red" style="margin:0 auto"><div style="transform:rotate(135deg)">{ic("telefon",30,"#fff")}</div></div><div class="hide">Gizle</div></div>''', cls="cs", beyazsb=True))
S.append(phone("14_gelen_arama", "14 · Gelen Arama (kilit açık)", f'''
<div class="callbg g3"></div>
<div class="ctop"><div class="cname">Ayşe Demir</div><div class="cst">mobil</div></div>
<div class="qrow"><div class="q">{ic("alarm",26,"#fff",2)}<span>Hatırlat</span></div><div class="q">{ic("mesaj_dolu",26,"#fff")}<span>Mesaj</span></div></div>
<div class="arow"><div class="cb red big"><div style="transform:rotate(135deg)">{ic("telefon",34,"#fff")}</div><span>Reddet</span></div><div class="cb green big"><div>{ic("telefon",34,"#fff")}</div><span>Kabul Et</span></div></div>''', cls="cs", beyazsb=True))
S.append(phone("15_gelen_kilitli", "15 · Gelen Arama (kilitli)", f'''
<div class="callbg g3"></div>
<div class="ctop"><div class="cname">Ayşe Demir</div><div class="cst">mobil</div></div>
<div class="qrow" style="bottom:170px"><div class="q">{ic("alarm",26,"#fff",2)}<span>Hatırlat</span></div><div class="q">{ic("mesaj_dolu",26,"#fff")}<span>Mesaj</span></div></div>
<div class="slider"><div class="knob">{ic("telefon",28,"#fff")}</div><span class="shine">kaydırarak cevapla</span></div>''', cls="cs", beyazsb=True))
S.append(phone("16_mesajla_yanitla", "16 · Mesajla Yanıtla (eylem sayfası)", f'''
<div class="callbg g3"></div>
<div class="ctop"><div class="cname">Ayşe Demir</div><div class="cst">mobil</div></div>
<div class="dim"></div>
<div class="sheet"><div class="sg"><div class="sgt">Mesajla yanıtla</div><div class="so">Şu an konuşamıyorum, sonra ararım.</div><div class="so">Toplantıdayım, mesaj atabilir misin?</div><div class="so">Yoldayım, birazdan ararım.</div><div class="so">Özel…</div></div><div class="sg"><div class="so b">Vazgeç</div></div></div>''', cls="cs", beyazsb=True))

# 17 İzin uyarısı (alert)
S.append(phone("17_izin_uyarisi", "17 · Aramaya basınca izin eksikse", f'''
<div style="filter:blur(1.5px)"><div style="height:150px;display:flex;align-items:flex-end;justify-content:center"><div class="num">0532 123 45 67</div></div><div class="kp" style="margin-top:22px">{kp}</div></div>
<div class="dim l"></div>
<div class="alert"><div class="at">Arama İzni Gerekli</div><div class="am">Bu numarayı arayabilmek için Telefon izni ve varsayılan telefon uygulaması olarak seçilmek gerekiyor.</div><div class="ab"><span>Şimdi Değil</span><span><b>İzin Ver ve Ara</b></span></div></div>
{tab(3)}'''))

# 18 Ayarlar
def ayar(renk, icn, t, sag="", son=False):
    return f'<div class="row {"last" if son else ""}"><span class="sq" style="background:{renk}">{ic(icn,17,"#fff",2.2)}</span><div class="rc"><div class="rt" style="font-weight:400">{t}</div></div><span class="rr">{sag}</span><span class="chev">{ic("ileri",14,"var(--gri3)",2.6)}</span></div>'
sw = lambda on: f'<span class="sw {"on" if on else ""}"><i></i></span>'
S.append(phone("18_ayarlar", "18 · Ayarlar", f'''
<div class="grp-bg">
<div class="nav"><span class="nb">{ic("geri",22,"var(--mavi)",2.4)}Geri</span><span></span><span></span></div>
<div class="pad"><div class="lt">Ayarlar</div></div>
<div class="sh">GÖRÜNÜM</div>
<div class="grp">
 <div class="row"><span class="sq" style="background:#5856D6">{ic("tema",17,"#fff",2.2)}</span><div class="rc"><div class="rt" style="font-weight:400">Tema</div></div><span class="seg sm"><span>Sistem</span><b>Açık</b><span>Koyu</span></span></div>
 <div class="row last"><span class="sq" style="background:#FF9500">{ic("palet",17,"#fff",2.2)}</span><div class="rc"><div class="rt" style="font-weight:400">Vurgu Rengi</div></div><span class="dots"><i style="background:#007AFF" class="a"></i><i style="background:#34C759"></i><i style="background:#AF52DE"></i><i style="background:#FF9500"></i><i style="background:#FF2D55"></i></span></div>
</div>
<div class="sh">İZİNLER VE GÜVENLİK</div>
<div class="grp">{ayar("#34C759","kalkan","İzinler","5 / 5")}{ayar("#34C759","cihaz","Varsayılan Uygulama","Eymen")}{ayar("#FF3B30","engelle","Engellenen Numaralar","3",True)}</div>
<div class="sh">ARAMALAR</div>
<div class="grp">
 <div class="row"><span class="sq" style="background:#FF2D55">{ic("titresim",17,"#fff",2.2)}</span><div class="rc"><div class="rt" style="font-weight:400">Tuşlarda Titreşim</div></div>{sw(True)}</div>
 <div class="row"><span class="sq" style="background:#FF9500">{ic("hoparlor",17,"#fff",2)}</span><div class="rc"><div class="rt" style="font-weight:400">Tuş Sesleri</div></div>{sw(False)}</div>
 {ayar("#007AFF","mesaj_dolu","Hızlı Yanıtlar","4",True)}
</div>
<div class="sh">DESTEK</div>
<div class="grp">{ayar("#8E8E93","dosya","Hata Kayıtları","0")}{ayar("#8E8E93","bilgi","Hakkında","1.1.0",True)}</div>
</div>'''))

# 19 Kaydırarak sil
S.append(phone("19_kaydirarak_sil", "19 · Son aramalarda kaydırarak sil", f'''
<div class="nav"><span class="nb">{ic("disli",22,"var(--mavi)")}</span><span class="seg"><b>Tümü</b><span>Cevapsız</span></span><span class="nb r">Düzenle</span></div>
<div class="pad"><div class="lt">Son Aramalar</div></div>
<div class="list" style="margin-top:6px">
 {son("Ayşe Demir","mobil","20:31",ini="AD")}
 <div style="position:relative;overflow:hidden"><div style="transform:translateX(-84px)">{son("Mehmet Çelik","mobil","19:05",True,"MÇ","(2)")}</div><div class="delbtn">Sil</div></div>
 {son("0212 555 01 23","İstanbul","17:42",ini="#")}
 {son("Elif Kaya","iş","Dün",ini="EK")}
</div>
{tab(1)}'''))

# 20 Güvenli alan rehberi
S.append(phone("20_guvenli_alan", "20 · Güvenli alan: kamera deliği + gezinme", f'''
<div class="safe-top">durum çubuğu + kamera deliği<br><b>WindowInsets.safeDrawing (üst)</b></div>
<div class="safe-mid"><div class="lt" style="font-size:26px">İçerik burada</div><p class="fn">Liste ve butonlar asla kameranın altına ya da gezinme tuşlarının arkasına girmez. Telefon dönünce yan kamera deliği de hesaba katılır.</p></div>
<div class="safe-bot">gezinme tuşları / hareket çubuğu<br><b>WindowInsets.navigationBars (alt)</b><div class="navkeys"><span>◁</span><span>○</span><span>▢</span></div></div>''', cls="safe"))

CSS = r'''
.phone{--bg:#fff;--grp:#F2F2F7;--yuzey:#fff;--etiket:#000;--gri:#8E8E93;--gri2:#3C3C43;--gri3:#C7C7CC;--ayirici:#C6C6C8;--mavi:#007AFF;--yesil:#34C759;--kirmizi:#FF3B30;--tus:#E5E5EA;--arama:rgba(118,118,128,.12);--tab:rgba(249,249,249,.94)}
.phone.dark{--bg:#000;--grp:#000;--yuzey:#1C1C1E;--etiket:#fff;--gri:#8D8D93;--gri2:#EBEBF5;--gri3:#48484A;--ayirici:#38383A;--mavi:#0A84FF;--yesil:#30D158;--kirmizi:#FF453A;--tus:#333333;--arama:rgba(118,118,128,.24);--tab:rgba(22,22,22,.94)}
*{box-sizing:border-box;margin:0;padding:0}
body{background:#E9EAEE;font-family:"Inter","Roboto",system-ui,sans-serif;padding:40px;color:#111}
.wrap{display:grid;grid-template-columns:repeat(5,360px);gap:44px 32px}
.title{grid-column:1/-1;display:flex;gap:16px;align-items:center}.title h1{font-size:30px}.title p{color:#666}
.cell{display:flex;flex-direction:column;align-items:center;gap:12px}.cap{font-weight:600;color:#444;font-size:14px}
.phone{width:360px;height:780px;border-radius:44px;background:var(--bg);color:var(--etiket);position:relative;overflow:hidden;box-shadow:0 0 0 9px #111,0 30px 60px rgba(0,0,0,.35)}
.grp-bg{background:var(--grp);position:absolute;inset:0;padding-top:40px}
.sb{height:40px;display:flex;justify-content:space-between;align-items:center;padding:0 28px;font-size:15px;font-weight:600;position:relative;z-index:6}
.sbr{display:flex;gap:6px;align-items:center}
.cam{position:absolute;left:50%;top:11px;width:18px;height:18px;margin-left:-9px;border-radius:50%;background:#000;box-shadow:inset 0 0 0 4px #1a1a1a}
.home{position:absolute;bottom:8px;left:50%;width:134px;height:5px;margin-left:-67px;border-radius:3px;background:var(--etiket);z-index:30;opacity:.9}
.cs .home{background:#fff}
.pad{padding:0 16px}
.nav{height:44px;display:flex;align-items:center;justify-content:space-between;padding:0 14px;font-size:17px;color:var(--mavi)}
.nb{display:flex;align-items:center;gap:2px;min-width:70px}.nb.r{justify-content:flex-end}
.nt{font-weight:600;color:var(--etiket)}
.lt{font-size:34px;font-weight:700;letter-spacing:-.4px}
.fn{font-size:13px;color:var(--gri);line-height:1.4}
.seg{display:flex;background:var(--arama);border-radius:9px;padding:2px;font-size:13px;color:var(--etiket)}
.seg>*{padding:5px 16px;border-radius:7px;font-weight:500}.seg b{background:var(--yuzey);box-shadow:0 2px 6px rgba(0,0,0,.12);font-weight:600}
.dark .seg b{background:#636366}
.seg.sm>*{padding:4px 9px;font-size:12px}
.list{background:var(--bg);padding-left:16px}
.row{display:flex;align-items:center;gap:12px;min-height:60px;border-bottom:.5px solid var(--ayirici);padding:8px 14px 8px 0;position:relative}
.row.last{border-bottom:none}
.rc{flex:1;min-width:0}.rt{font-size:17px;font-weight:600;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.rs{font-size:14px;color:var(--gri);display:flex;align-items:center;gap:4px;margin-top:1px}
.mini{display:inline-flex}
.rr{color:var(--gri);font-size:15px}.time{font-size:15px}
.info{display:inline-flex}
.av{border-radius:50%;background:linear-gradient(180deg,#A5ABB9,#858994);color:#fff;display:flex;align-items:center;justify-content:center;font-weight:600;flex:none;letter-spacing:.5px}
.tab{position:absolute;bottom:0;left:0;right:0;height:84px;background:var(--tab);border-top:.5px solid var(--ayirici);display:flex;justify-content:space-around;padding-top:7px;backdrop-filter:blur(20px);z-index:20}
.ti{display:flex;flex-direction:column;align-items:center;gap:3px;font-size:10px;font-weight:500;width:80px}
.search{display:flex;align-items:center;gap:6px;height:36px;border-radius:10px;background:var(--arama);padding:0 8px;color:var(--gri);font-size:17px;margin:8px 0 10px}
.search.on{background:var(--arama)}.caret{width:2px;height:20px;background:var(--mavi);margin-left:-4px}
.sec{background:var(--bg);padding:6px 16px 2px;font-size:15px;font-weight:600;color:var(--gri)}
.sec2{padding:18px 16px 6px;font-size:13px;color:var(--gri);font-weight:500}
.aidx{position:absolute;right:3px;top:250px;display:flex;flex-direction:column;align-items:center;font-size:10.5px;font-weight:600;color:var(--mavi);gap:1px}
.sh{padding:22px 32px 6px;font-size:13px;color:var(--gri);letter-spacing:.2px}
.grp{background:var(--yuzey);border-radius:10px;margin:0 16px 0;padding-left:16px}
.grp .row{min-height:44px;padding:10px 14px 10px 0}
.grp-bg .grp + .grp{margin-top:18px}
.sq{width:29px;height:29px;border-radius:7px;display:flex;align-items:center;justify-content:center;flex:none}
.grp .row .sq + .rc{}
.ok{display:inline-flex}.cap2{background:var(--mavi);color:#fff;border-radius:14px;padding:5px 12px;font-size:14px;font-weight:600}
.chev{display:inline-flex}
.bottom{position:absolute;bottom:36px;left:16px;right:16px}
.btn{height:50px;border-radius:14px;background:var(--mavi);color:#fff;display:flex;align-items:center;justify-content:center;font-size:17px;font-weight:600}
.btn.dis{opacity:.35}
.feat{display:flex;gap:16px;align-items:flex-start;margin-bottom:24px}.feat b{font-size:16px}.feat p{font-size:14px;color:var(--gri);margin-top:2px;line-height:1.35}
.acts{display:flex;gap:8px;margin:18px 16px 18px}.act{flex:1;background:var(--yuzey);border-radius:12px;height:62px;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:4px;color:var(--mavi);font-size:12px}
.lbl{font-size:14px;color:var(--etiket);margin-bottom:2px}.blue{color:var(--mavi);font-size:17px}
.num{font-size:38px;font-weight:400;letter-spacing:.5px}
.kp{display:grid;grid-template-columns:repeat(3,78px);gap:16px 28px;justify-content:center}
.key{width:78px;height:78px;border-radius:50%;background:var(--tus);display:flex;flex-direction:column;align-items:center;justify-content:center}
.key.k2{background:rgba(255,255,255,.18);color:#fff}.key.k2 small{color:rgba(255,255,255,.8)}
.key b{font-size:34px;font-weight:400;line-height:1}.key small{font-size:9.5px;font-weight:700;letter-spacing:2px;margin-top:1px;height:11px}
.call{width:78px;height:78px;border-radius:50%;background:var(--yesil);display:flex;align-items:center;justify-content:center}
.del{display:flex;align-items:center;justify-content:center}
.kb{position:absolute;bottom:0;left:0;right:0;background:#D1D3D9;padding:8px 3px 30px}.kbr{display:flex;justify-content:center;gap:5px;margin-bottom:10px}.kbr span{width:28px;height:42px;background:#fff;border-radius:5px;display:flex;align-items:center;justify-content:center;font-size:18px;box-shadow:0 1px 0 #898A8D}
.kbr .w{width:58px;font-size:14px;background:#ABB0BA}.kbr .sp{width:170px;font-size:14px}.kbr .acc{background:var(--mavi);color:#fff}
.callbg{position:absolute;inset:0}
.g1{background:radial-gradient(circle at 50% 20%,#5B5F73,#23252F 55%,#101116)}
.g2{background:radial-gradient(circle at 50% 18%,#6E5A64,#2B2328 55%,#121012)}
.g3{background:radial-gradient(circle at 50% 22%,#4F6A73,#1F2A2F 55%,#0D1214)}
.ctop{position:absolute;top:110px;left:0;right:0;text-align:center;color:#fff}
.cname{font-size:34px;font-weight:400;letter-spacing:.2px}.cst{font-size:17px;color:rgba(255,255,255,.62);margin-top:6px}
.cgrid{position:absolute;bottom:90px;left:0;right:0;display:grid;grid-template-columns:repeat(3,76px);gap:26px 30px;justify-content:center}
.cb{display:flex;flex-direction:column;align-items:center;gap:8px;color:#fff;font-size:13px}
.cb>div{width:76px;height:76px;border-radius:50%;background:rgba(255,255,255,.18);display:flex;align-items:center;justify-content:center;backdrop-filter:blur(20px)}
.cb.on>div{background:#fff}.cb.red>div{background:#FF3B30}.cb.green>div{background:#34C759}
.cb.big>div{width:78px;height:78px}
.hide{color:#fff;font-size:17px;display:flex;align-items:center;justify-content:center}
.qrow{position:absolute;bottom:200px;left:0;right:0;display:flex;justify-content:space-around;padding:0 40px}
.q{display:flex;flex-direction:column;align-items:center;gap:6px;color:#fff;font-size:13px}
.arow{position:absolute;bottom:70px;left:0;right:0;display:flex;justify-content:space-between;padding:0 46px}
.slider{position:absolute;bottom:70px;left:24px;right:24px;height:80px;border-radius:40px;background:rgba(255,255,255,.16);display:flex;align-items:center;padding:6px;backdrop-filter:blur(20px)}
.knob{width:68px;height:68px;border-radius:50%;background:#34C759;display:flex;align-items:center;justify-content:center}
.shine{flex:1;text-align:center;font-size:20px;background:linear-gradient(90deg,rgba(255,255,255,.45) 20%,#fff 45%,rgba(255,255,255,.45) 70%);-webkit-background-clip:text;color:transparent}
.dim{position:absolute;inset:0;background:rgba(0,0,0,.4);z-index:8}.dim.l{background:rgba(0,0,0,.25)}
.sheet{position:absolute;left:8px;right:8px;bottom:30px;z-index:9}
.sg{background:rgba(250,250,250,.94);border-radius:14px;overflow:hidden;margin-top:8px}
.sgt{font-size:13px;color:#8A8A8E;text-align:center;padding:14px;border-bottom:.5px solid #C6C6C8}
.so{font-size:19px;color:#007AFF;text-align:center;padding:16px 10px;border-bottom:.5px solid #C6C6C8}.so:last-child{border-bottom:none}.so.b{font-weight:600}
.alert{position:absolute;left:50%;top:300px;width:270px;margin-left:-135px;background:rgba(242,242,242,.96);border-radius:14px;z-index:9;text-align:center;overflow:hidden}
.at{font-size:17px;font-weight:600;padding:19px 16px 2px}.am{font-size:13px;padding:2px 16px 18px;line-height:1.35}
.ab{display:flex;border-top:.5px solid #C6C6C8}.ab span{flex:1;padding:12px 4px;color:#007AFF;font-size:16px}.ab span+span{border-left:.5px solid #C6C6C8}
.sw{width:51px;height:31px;border-radius:16px;background:#E9E9EA;position:relative;flex:none}.sw i{position:absolute;top:2px;left:2px;width:27px;height:27px;border-radius:50%;background:#fff;box-shadow:0 3px 8px rgba(0,0,0,.15)}
.sw.on{background:#34C759}.sw.on i{left:22px}
.dots{display:flex;gap:6px}.dots i{width:20px;height:20px;border-radius:50%}.dots i.a{box-shadow:0 0 0 2px #fff,0 0 0 4px #007AFF}
.delbtn{position:absolute;right:0;top:0;bottom:0;width:84px;background:#FF3B30;color:#fff;display:flex;align-items:center;justify-content:center;font-size:17px}
.safe-top{position:absolute;top:0;left:0;right:0;height:52px;background:rgba(255,59,48,.18);border-bottom:2px dashed #FF3B30;font-size:11px;text-align:center;padding-top:30px;color:#B42318;z-index:5;line-height:1.3}
.safe-top{height:84px}
.safe-mid{position:absolute;top:96px;left:16px;right:16px;bottom:118px;border:2px dashed #34C759;border-radius:14px;padding:18px;background:rgba(52,199,89,.06)}
.safe-bot{position:absolute;bottom:0;left:0;right:0;height:104px;background:rgba(0,122,255,.14);border-top:2px dashed #007AFF;font-size:11px;text-align:center;padding-top:10px;color:#0040A0;line-height:1.3}
.navkeys{display:flex;justify-content:space-around;margin-top:18px;font-size:22px;color:#333;padding:0 60px}
.safe .home{display:none}
'''

html = f'''<!doctype html><html><head><meta charset="utf-8"><style>{CSS}</style></head><body>
<div class="wrap"><div class="title"><div style="width:56px;height:56px">{ICON}</div><div><h1>Eymen Telefon v1.1 — iOS tarzı tasarım</h1><p>20 ekran · açık + koyu tema · kamera deliği ve gezinme tuşlarına göre otomatik güvenli alan</p></div></div>
{''.join(S)}</div></body></html>'''

OUT.mkdir(parents=True, exist_ok=True)
for eski in OUT.glob("*.png"):
    eski.unlink()
with sync_playwright() as p:
    b = p.chromium.launch(executable_path="/usr/bin/google-chrome", args=["--no-sandbox"])
    pg = b.new_page(viewport={"width": 2100, "height": 1000}, device_scale_factor=2)
    pg.set_content(html); pg.wait_for_timeout(500)
    for el in pg.query_selector_all(".phone"):
        el.screenshot(path=str(OUT / f'{el.get_attribute("id")}.png'))
    pg2 = b.new_page(viewport={"width": 2100, "height": 1000}, device_scale_factor=1)
    pg2.set_content(html); pg2.wait_for_timeout(500)
    pg2.screenshot(path=str(OUT / "00_genel_bakis.png"), full_page=True)
    b.close()
print("ok", len(S))
