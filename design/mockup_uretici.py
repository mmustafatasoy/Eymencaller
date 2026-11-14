#!/usr/bin/env python3
"""Eymen Telefon — ekran tasarımlarını (mockup) HTML olarak üretir ve PNG'ye çevirir."""
import sys, pathlib
from playwright.sync_api import sync_playwright

OUT = pathlib.Path(sys.argv[1])
ICON = pathlib.Path(sys.argv[2]).read_text()

# ---------- ikonlar (24x24, çizgi) ----------
P = {
 "search": '<circle cx="11" cy="11" r="7"/><line x1="16.5" y1="16.5" x2="21" y2="21"/>',
 "sliders": '<line x1="4" y1="6" x2="20" y2="6"/><line x1="4" y1="12" x2="20" y2="12"/><line x1="4" y1="18" x2="20" y2="18"/><circle cx="9" cy="6" r="2.2" fill="currentColor"/><circle cx="15" cy="12" r="2.2" fill="currentColor"/><circle cx="8" cy="18" r="2.2" fill="currentColor"/>',
 "star": '<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26"/>',
 "clock": '<circle cx="12" cy="12" r="9"/><polyline points="12 7 12 12 15.5 14"/>',
 "users": '<circle cx="9" cy="8" r="4"/><path d="M2 21v-1a6 6 0 0 1 6-6h2a6 6 0 0 1 6 6v1"/><path d="M16 3.5a4 4 0 0 1 0 7.5"/><path d="M22 21v-1a5 5 0 0 0-3-4.6"/>',
 "dialpad": ''.join(f'<circle cx="{x}" cy="{y}" r="1.5" fill="currentColor" stroke="none"/>' for y in (4,9,14) for x in (6,12,18)) + '<circle cx="12" cy="20" r="1.5" fill="currentColor" stroke="none"/>',
 "backspace": '<path d="M21 5H9l-7 7 7 7h12a1 1 0 0 0 1-1V6a1 1 0 0 0-1-1z"/><line x1="17" y1="9" x2="12" y2="14"/><line x1="12" y1="9" x2="17" y2="14"/>',
 "micoff": '<rect x="9" y="2" width="6" height="12" rx="3"/><path d="M5 10v2a7 7 0 0 0 14 0v-2"/><line x1="12" y1="19" x2="12" y2="22"/><line x1="3" y1="3" x2="21" y2="21"/>',
 "speaker": '<polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><path d="M15.5 8.5a5 5 0 0 1 0 7"/><path d="M19 5a10 10 0 0 1 0 14"/>',
 "pause": '<rect x="6" y="4" width="4" height="16" rx="1"/><rect x="14" y="4" width="4" height="16" rx="1"/>',
 "useradd": '<circle cx="9" cy="8" r="4"/><path d="M2 21v-1a6 6 0 0 1 6-6h2a6 6 0 0 1 6 6v1"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="16" y1="11" x2="22" y2="11"/>',
 "msg": '<path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>',
 "video": '<rect x="2" y="6" width="14" height="12" rx="2"/><polygon points="22 8 16 12 22 16 22 8"/>',
 "shield": '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><polyline points="9 12 11 14 15 10"/>',
 "bell": '<path d="M18 8a6 6 0 0 0-12 0c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.7 21a2 2 0 0 1-3.4 0"/>',
 "card": '<rect x="4" y="2" width="16" height="20" rx="2"/><circle cx="12" cy="10" r="3"/><path d="M8 17a4 4 0 0 1 8 0"/>',
 "check": '<polyline points="20 6 9 17 4 12"/>',
 "chev": '<polyline points="9 18 15 12 9 6"/>',
 "back": '<line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>',
 "in": '<line x1="17" y1="7" x2="7" y2="17"/><polyline points="16 17 7 17 7 8"/>',
 "out": '<line x1="7" y1="17" x2="17" y2="7"/><polyline points="8 7 17 7 17 16"/>',
 "more": '<circle cx="12" cy="5" r="1.3" fill="currentColor"/><circle cx="12" cy="12" r="1.3" fill="currentColor"/><circle cx="12" cy="19" r="1.3" fill="currentColor"/>',
 "edit": '<path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4z"/>',
 "block": '<circle cx="12" cy="12" r="9"/><line x1="5.6" y1="5.6" x2="18.4" y2="18.4"/>',
 "moon": '<path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8z"/>',
 "phoneframe": '<rect x="6" y="2" width="12" height="20" rx="2"/><line x1="11" y1="18" x2="13" y2="18"/>',
 "info": '<circle cx="12" cy="12" r="9"/><line x1="12" y1="16" x2="12" y2="11"/><line x1="12" y1="8" x2="12.01" y2="8"/>',
 "file": '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>',
 "share": '<circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><line x1="8.6" y1="13.5" x2="15.4" y2="17.5"/><line x1="15.4" y1="6.5" x2="8.6" y2="10.5"/>',
 "plus": '<line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>',
 "mail": '<rect x="2" y="4" width="20" height="16" rx="2"/><polyline points="22 6 12 13 2 6"/>',
 "vibrate": '<rect x="7" y="3" width="10" height="18" rx="2"/><path d="M3 8v8M21 8v8"/>',
 "palette": '<circle cx="12" cy="12" r="9"/><circle cx="8" cy="10" r="1.3" fill="currentColor"/><circle cx="12" cy="7.5" r="1.3" fill="currentColor"/><circle cx="16" cy="10" r="1.3" fill="currentColor"/>',
 "alarm": '<circle cx="12" cy="13" r="8"/><polyline points="12 9 12 13 14 15"/><line x1="5" y1="3" x2="2" y2="6"/><line x1="19" y1="3" x2="22" y2="6"/>',
 "bt": '<polyline points="7 7 17 17 12 22 12 2 17 7 7 17"/>',
}
HANDSET = '<path fill="currentColor" stroke="none" d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/>'

def ic(name, size=22, color="currentColor", sw=2):
    body = HANDSET if name == "phone" else P[name]
    return f'<svg width="{size}" height="{size}" viewBox="0 0 24 24" fill="none" stroke="{color}" stroke-width="{sw}" stroke-linecap="round" stroke-linejoin="round" style="color:{color}">{body}</svg>'

GRADS = ["#19D3A2,#3D7BFF", "#FF8A5B,#FF4F8B", "#8B5CF6,#3D7BFF", "#F7B733,#FC4A1A", "#22C1C3,#2D9CDB", "#F857A6,#7B61FF", "#43E97B,#38F9D7", "#5EE7DF,#B490CA"]
def av(initials, i=0, size=42, fs=None):
    fs = fs or int(size*0.38)
    return f'<div class="av" style="width:{size}px;height:{size}px;font-size:{fs}px;background:linear-gradient(135deg,{GRADS[i%len(GRADS)]})">{initials}</div>'

def status(light=False):
    c = "#0B1220" if light else "#EAF0FF"
    return f'''<div class="sb" style="color:{c}"><span>20:48</span><span class="sbr"><svg width="16" height="12" viewBox="0 0 16 12"><rect x="0" y="8" width="3" height="4" fill="{c}"/><rect x="4.3" y="5.5" width="3" height="6.5" fill="{c}"/><rect x="8.6" y="3" width="3" height="9" fill="{c}"/><rect x="12.9" y="0" width="3" height="12" fill="{c}" opacity=".4"/></svg><svg width="24" height="12" viewBox="0 0 24 12"><rect x=".5" y=".5" width="20" height="11" rx="3" fill="none" stroke="{c}"/><rect x="2" y="2" width="14" height="8" rx="1.6" fill="{c}"/><rect x="21.5" y="4" width="2" height="4" rx="1" fill="{c}"/></svg></span></div>'''

def searchbar(text="Kişi veya numara ara", focused=False):
    cls = "srch f" if focused else "srch"
    tx = f'<span class="typed">{text}</span><span class="caret"></span>' if focused else f'<span>{text}</span>'
    right = f'<span class="clr">{ic("plus",18,"#8A97B4")}</span>' if focused else f'<span class="me">{av("MA",0,30,12)}</span>'
    return f'<div class="{cls}">{ic("search",20,"#8A97B4")}{tx}<span class="grow"></span>{right}</div>'

def nav(active):
    items = [("star","Favoriler"),("clock","Son"),("users","Rehber"),("dialpad","Tuşlar")]
    out = '<div class="nav">'
    for i,(icn,lbl) in enumerate(items):
        a = (i == active)
        out += f'<div class="ni {"a" if a else ""}"><div class="pill">{ic(icn,22, "#0B1220" if a else "#8A97B4")}</div><span>{lbl}</span></div>'
    return out + '</div>'

def phone(sid, title, body, bg="var(--bg)", light=False):
    return f'''<div class="cell"><div class="phone" id="{sid}" style="background:{bg}">{status(light)}{body}<div class="home"></div></div><div class="cap">{title}</div></div>'''

# ---------- EKRANLAR ----------
S = []

# 1 Karşılama
S.append(phone("01_karsilama", "01 · Karşılama", f'''
<div class="glow g1"></div><div class="glow g2"></div>
<div class="center col" style="height:640px;justify-content:center;gap:18px">
  <div style="width:132px;height:132px;filter:drop-shadow(0 18px 40px rgba(25,211,162,.35))">{ICON}</div>
  <div class="h1" style="font-size:30px;margin-top:10px">Eymen Telefon</div>
  <div class="t2" style="text-align:center;max-width:250px;font-size:15px;line-height:1.5">Rehberin, aramaların ve görüşmelerin — tek, hızlı ve şık bir yerde.</div>
  <div class="dots"><i class="on"></i><i></i><i></i></div>
</div>
<div class="bottom"><div class="btn pri">Başlayalım</div><div class="t3" style="text-align:center;margin-top:12px">Devam ederek gizlilik politikasını kabul edersiniz</div></div>
'''))

# 2 İzinler
def perm(icn, title, desc, state, i):
    if state == "ok":
        r = f'<span class="chip ok">{ic("check",14,"#19D3A2",3)} Verildi</span>'
    elif state == "req":
        r = '<span class="chip go">Ayarla</span>'
    else:
        r = '<span class="chip go">İzin ver</span>'
    tag = '<span class="tag">Zorunlu</span>' if i < 4 else '<span class="tag opt">İsteğe bağlı</span>'
    return f'''<div class="pc {"done" if state=="ok" else ""}"><div class="pi">{ic(icn,22,"#19D3A2" if state=="ok" else "#EAF0FF")}</div><div class="grow"><div class="pt">{title} {tag}</div><div class="pd">{desc}</div></div>{r}</div>'''
S.append(phone("02_izinler", "02 · İzinler (açılıştan önce)", f'''
<div class="pad">
  <div class="kick">ADIM 2 / 2</div>
  <div class="h1">Başlamadan önce</div>
  <div class="t2" style="margin:6px 0 14px">Arama yapabilmek ve rehberini gösterebilmek için birkaç izne ihtiyacımız var. Verilerin telefonundan çıkmaz.</div>
  <div class="prog"><div style="width:60%"></div></div>
  <div class="t3" style="margin:6px 0 12px">3 / 5 izin hazır</div>
  {perm("phoneframe","Varsayılan telefon uygulaması","Gelen ve giden aramaları bu uygulama yönetir","req",0)}
  {perm("phone","Telefon","Arama yapma, cevaplama, hat durumu","ok",1)}
  {perm("card","Rehber","Kişileri göster, ekle ve düzenle","ok",2)}
  {perm("clock","Arama geçmişi","Son aramaları listele ve temizle","ok",3)}
  {perm("bell","Bildirimler","Gelen arama ve cevapsız arama uyarısı","no",4)}
</div>
<div class="bottom"><div class="btn pri dis">Devam et</div><div class="link">Neden bu izinler gerekli?</div></div>
'''))

# 3 Tuş takımı
keys = [("1","‿"),("2","ABCÇ"),("3","DEF"),("4","GĞHIİ"),("5","JKL"),("6","MNOÖ"),("7","PQRSŞ"),("8","TUÜV"),("9","WXYZ"),("*",""),("0","+"),("#","")]
kp = ''.join(f'<div class="key"><b>{k}</b><small>{l}</small></div>' for k,l in keys)
S.append(phone("03_tus_takimi", "03 · Tuş takımı", f'''
<div class="pad" style="padding-top:6px">{searchbar()}</div>
<div class="num">0532 123 45 67</div>
<div class="sug">{av("AY",0,34)}<div class="grow"><div class="nm">Ahmet <u>Y</u>ılmaz</div><div class="t3">Mobil · 0532 123 45 67</div></div><span class="chip go sm">Ekle</span></div>
<div class="kp">{kp}</div>
<div class="row center" style="gap:46px;margin-top:6px">
  <div class="ghost">{ic("useradd",22,"#8A97B4")}</div>
  <div class="callbtn">{ic("phone",30,"#fff")}</div>
  <div class="ghost">{ic("backspace",24,"#8A97B4")}</div>
</div>
{nav(3)}
'''))

# 4 Son aramalar
def rec(n, init, gi, kind, info, t, cnt=""):
    col = {"in":"#19D3A2","out":"#3D7BFF","miss":"#FF5A5F"}[kind]
    name_col = "#FF5A5F" if kind=="miss" else "#EAF0FF"
    icn = "in" if kind!="out" else "out"
    return f'''<div class="lr">{av(init,gi)}<div class="grow"><div class="nm" style="color:{name_col}">{n} <span class="t3">{cnt}</span></div><div class="t3 row" style="gap:4px">{ic(icn,14,col,2.5)}{info}</div></div><div class="t3" style="margin-right:10px">{t}</div><div class="cbtn">{ic("phone",18,"#19D3A2")}</div></div>'''
S.append(phone("04_son_aramalar", "04 · Son aramalar", f'''
<div class="pad" style="padding-top:6px">{searchbar()}
<div class="chips"><span class="fc a">Tümü</span><span class="fc">Cevapsız <b>2</b></span><span class="fc">Gelen</span><span class="fc">Giden</span></div>
<div class="sec">Bugün</div>
{rec("Ayşe Demir","AD",1,"in","Mobil","20:31")}
{rec("Mehmet Çelik","MÇ",2,"miss","Mobil","19:05","(2)")}
{rec("0212 555 01 23","#",4,"out","İstanbul","17:42")}
{rec("Elif Kaya","EK",5,"out","İş","14:10")}
<div class="sec">Dün</div>
{rec("Burak Arslan","BA",3,"in","Mobil","22:17")}
{rec("Zeynep Şahin","ZŞ",6,"miss","Mobil","18:40")}
{rec("Can Öztürk","CÖ",7,"out","Mobil","09:02","(3)")}
</div>
<div class="fab">{ic("dialpad",24,"#0B1220")}</div>
{nav(1)}
'''))

# 5 Rehber
favs = [("AD",1,"Ayşe"),("EK",5,"Elif"),("ME",2,"Annem"),("BA",3,"Burak"),("CÖ",7,"Can")]
favh = ''.join(f'<div class="fav">{av(i,g,54)}<span>{n}</span></div>' for i,g,n in favs)
def crow(n, init, gi, sub):
    return f'<div class="lr">{av(init,gi,40)}<div class="grow"><div class="nm">{n}</div><div class="t3">{sub}</div></div></div>'
alpha = "A B C Ç D E F G H I İ J K L M N O Ö P R S Ş T U Ü V Y Z".split()
idx = ''.join(f'<span class="{"a" if c=="A" else ""}">{c}</span>' for c in alpha)
S.append(phone("05_rehber", "05 · Rehber", f'''
<div class="pad" style="padding-top:6px">{searchbar("312 kişi arasında ara")}
<div class="sec" style="margin-top:12px">Favoriler</div>
<div class="favs">{favh}</div>
<div class="me-card">{av("MA",0,40)}<div class="grow"><div class="nm">Muhammet Atasoy</div><div class="t3">Benim kartım</div></div>{ic("chev",18,"#8A97B4")}</div>
<div class="letter">A</div>
{crow("Ahmet Yılmaz","AY",0,"0532 123 45 67")}
{crow("Ayşe Demir","AD",1,"0533 987 65 43")}
<div class="letter">B</div>
{crow("Burak Arslan","BA",3,"0544 222 11 00")}
<div class="letter">C</div>
{crow("Can Öztürk","CÖ",7,"0505 444 33 22")}
</div>
<div class="aidx">{idx}</div>
<div class="fab ext">{ic("useradd",20,"#0B1220")}<span>Yeni kişi</span></div>
{nav(2)}
'''))

# 6 Kişi detayı
S.append(phone("06_kisi_detay", "06 · Kişi detayı", f'''
<div class="hero-bg"></div>
<div class="row pad" style="justify-content:space-between;padding-top:6px;position:relative">{ic("back",22,"#EAF0FF")}<div class="row" style="gap:18px">{ic("star",22,"#F7B733")}{ic("edit",22,"#EAF0FF")}{ic("more",22,"#EAF0FF")}</div></div>
<div class="center col" style="position:relative;margin-top:8px">{av("AD",1,104)}<div class="h1" style="margin-top:12px">Ayşe Demir</div><div class="t2">İş arkadaşı · Atasoy Yazılım</div></div>
<div class="acts">
  <div class="act p"><div>{ic("phone",22,"#0B1220")}</div><span>Ara</span></div>
  <div class="act"><div>{ic("msg",22,"#EAF0FF")}</div><span>Mesaj</span></div>
  <div class="act"><div>{ic("video",22,"#EAF0FF")}</div><span>Görüntülü</span></div>
  <div class="act"><div>{ic("mail",22,"#EAF0FF")}</div><span>E-posta</span></div>
</div>
<div class="pad">
 <div class="card">
  <div class="cr">{ic("phone",20,"#19D3A2")}<div class="grow"><div class="nm">0533 987 65 43</div><div class="t3">Mobil · Varsayılan</div></div>{ic("msg",20,"#8A97B4")}</div>
  <div class="cr">{ic("phone",20,"#19D3A2")}<div class="grow"><div class="nm">0212 444 10 20</div><div class="t3">İş</div></div>{ic("msg",20,"#8A97B4")}</div>
 </div>
 <div class="card">
  <div class="ch">Son aramalar <span class="t3">Tümü</span></div>
  <div class="cr sm">{ic("in",16,"#19D3A2",2.5)}<div class="grow t2">Gelen · 4 dk 12 sn</div><span class="t3">Bugün 20:31</span></div>
  <div class="cr sm">{ic("out",16,"#3D7BFF",2.5)}<div class="grow t2">Giden · 1 dk 03 sn</div><span class="t3">Pzt 11:20</span></div>
 </div>
 <div class="card"><div class="cr">{ic("share",20,"#EAF0FF")}<div class="grow nm">Kişiyi paylaş</div></div><div class="cr">{ic("block",20,"#FF5A5F")}<div class="grow nm" style="color:#FF5A5F">Numarayı engelle</div></div></div>
</div>
'''))

# 7 Arama ekranı (search)
kb_rows = ["q w e r t y u ı o p ğ ü", "a s d f g h j k l ş i", "z x c v b n m ö ç"]
kb = ''.join('<div class="kbr">' + ''.join(f'<span>{k}</span>' for k in r.split()) + '</div>' for r in kb_rows)
kb += '<div class="kbr"><span class="w">123</span><span class="sp">boşluk</span><span class="w acc">ara</span></div>'
def hl(name, q="ay"):
    i = name.lower().find(q)
    return name if i < 0 else f'{name[:i]}<mark>{name[i:i+len(q)]}</mark>{name[i+len(q):]}'
S.append(phone("07_arama_ekrani", "07 · Arama (kişi / numara)", f'''
<div class="pad" style="padding-top:6px">
<div class="row" style="gap:10px">{ic("back",22,"#EAF0FF")}<div class="grow">{searchbar("Ay", True)}</div></div>
<div class="sec" style="margin-top:14px">Kişiler · 3</div>
<div class="lr">{av("AD",1,40)}<div class="grow"><div class="nm">{hl("Ayşe Demir")}</div><div class="t3">Mobil · 0533 987 65 43</div></div><div class="cbtn">{ic("phone",18,"#19D3A2")}</div></div>
<div class="lr">{av("DA",4,40)}<div class="grow"><div class="nm">Deniz {hl("Aydın")}</div><div class="t3">Mobil · 0542 300 20 10</div></div><div class="cbtn">{ic("phone",18,"#19D3A2")}</div></div>
<div class="lr">{av("AY",0,40)}<div class="grow"><div class="nm">Ahmet Yılmaz</div><div class="t3">Not: <mark>Ay</mark> sonu ödeme</div></div><div class="cbtn">{ic("phone",18,"#19D3A2")}</div></div>
<div class="sec">Son aramalarda</div>
<div class="lr">{ic("clock",20,"#8A97B4")}<div class="grow"><div class="nm">{hl("Ayşe Demir")}</div><div class="t3">Bugün 20:31 · Gelen</div></div></div>
</div>
<div class="kb">{kb}</div>
'''))

# 8 Giden arama
S.append(phone("08_giden_arama", "08 · Giden arama", f'''
<div class="callbg"></div>
<div class="center col" style="position:relative;margin-top:70px">
  <div class="t2 row" style="gap:6px">{ic("phoneframe",14,"#8A97B4")} SIM 1 · Aranıyor…</div>
  <div class="rings">{av("ME",2,120)}</div>
  <div class="h1" style="font-size:30px">Annem</div>
  <div class="t2" style="margin-top:4px">Mobil · 0535 111 22 33</div>
</div>
<div class="ctl3">
  <div class="cb"><div>{ic("micoff",24,"#EAF0FF")}</div><span>Sessiz</span></div>
  <div class="cb"><div>{ic("dialpad",24,"#EAF0FF")}</div><span>Tuşlar</span></div>
  <div class="cb"><div>{ic("speaker",24,"#EAF0FF")}</div><span>Hoparlör</span></div>
</div>
<div class="endwrap"><div class="end">{ic("phone",32,"#fff")}</div></div>
'''))

# 9 Gelen arama
S.append(phone("09_gelen_arama", "09 · Gelen arama", f'''
<div class="callbg in"></div>
<div class="center col" style="position:relative;margin-top:60px">
  <div class="chip ok">Gelen arama · SIM 1</div>
  <div class="rings g">{av("AD",1,128)}</div>
  <div class="h1" style="font-size:32px">Ayşe Demir</div>
  <div class="t2" style="margin-top:4px">Mobil · 0533 987 65 43</div>
</div>
<div class="quick"><div class="q">{ic("msg",20,"#EAF0FF")}<span>Mesajla reddet</span></div><div class="q">{ic("alarm",20,"#EAF0FF")}<span>Hatırlat</span></div></div>
<div class="swipe">
  <div class="sd red">{ic("phone",26,"#fff")}</div>
  <div class="st">‹ ‹  Kaydır  › ›</div>
  <div class="sd green">{ic("phone",26,"#fff")}</div>
</div>
<div class="t3" style="text-align:center;position:absolute;bottom:34px;width:100%">Cevaplamak için sağa, reddetmek için sola kaydır</div>
'''))

# 10 Görüşme
grid = [("micoff","Sessiz",False),("dialpad","Tuş takımı",False),("speaker","Hoparlör",True),("pause","Beklet",False),("useradd","Arama ekle",False),("users","Kişiler",False)]
g = ''.join(f'<div class="cb {"on" if on else ""}"><div>{ic(i,24,"#0B1220" if on else "#EAF0FF")}</div><span>{l}</span></div>' for i,l,on in grid)
S.append(phone("10_gorusme", "10 · Görüşme sırasında", f'''
<div class="callbg on"></div>
<div class="center col" style="position:relative;margin-top:56px">
  <div class="row" style="gap:8px"><span class="chip ok">HD</span><span class="t2">SIM 1</span></div>
  <div style="margin:22px 0 14px">{av("AD",1,96)}</div>
  <div class="h1" style="font-size:28px">Ayşe Demir</div>
  <div class="timer">03:42</div>
  <div class="wave">{''.join(f'<i style="height:{h}px"></i>' for h in [8,14,22,12,26,18,10,20,28,14,9,16,24,12,8])}</div>
</div>
<div class="grid3">{g}</div>
<div class="endwrap"><div class="end">{ic("phone",32,"#fff")}</div></div>
'''))

# 11 Ayarlar
def srow(icn, t, sub="", right="chev", col="#EAF0FF"):
    if right == "chev": r = ic("chev",18,"#8A97B4")
    elif right == "on": r = '<span class="tg on"><i></i></span>'
    elif right == "off": r = '<span class="tg"><i></i></span>'
    else: r = right
    subh = f'<div class="t3">{sub}</div>' if sub else ''
    return f'<div class="cr">{ic(icn,20,col)}<div class="grow"><div class="nm">{t}</div>{subh}</div>{r}</div>'
sw = ''.join(f'<i style="background:{c}" class="{"a" if k==0 else ""}"></i>' for k,c in enumerate(["#19D3A2","#3D7BFF","#8B5CF6","#FF8A5B","#F857A6"]))
S.append(phone("11_ayarlar", "11 · Ayarlar + İzin ayarları", f'''
<div class="pad" style="padding-top:6px">
<div class="row" style="gap:12px;margin-bottom:10px">{ic("back",22,"#EAF0FF")}<div class="h2">Ayarlar</div></div>
<div class="sec">Görünüm</div>
<div class="card">{srow("moon","Tema","Sistem · Koyu · Açık",'<span class="seg"><b>Koyu</b></span>')}<div class="cr">{ic("palette",20,"#EAF0FF")}<div class="grow nm">Vurgu rengi</div><div class="sw">{sw}</div></div></div>
<div class="sec">İzinler ve güvenlik</div>
<div class="card">{srow("shield","İzin ayarları","5 / 5 izin verildi",'<span class="chip ok sm">Tamam</span>',"#19D3A2")}{srow("phoneframe","Varsayılan telefon uygulaması","Eymen Telefon")}{srow("block","Engellenen numaralar","3 numara")}</div>
<div class="sec">Aramalar</div>
<div class="card">{srow("vibrate","Tuşlarda titreşim","", "on")}{srow("speaker","Tuş sesleri","", "off")}{srow("msg","Hızlı yanıt mesajları","4 mesaj")}</div>
<div class="sec">Destek</div>
<div class="card">{srow("file","Hata kayıtları","Son 7 gün · 0 hata")}{srow("info","Hakkında","Sürüm 1.0.0 (1)")}</div>
</div>
'''))

# 12 İzin eksik (aramaya basınca)
S.append(phone("12_izin_eksik", "12 · Aramaya basınca izin eksikse", f'''
<div style="filter:blur(2px);opacity:.5">
<div class="num" style="margin-top:60px">0532 123 45 67</div>
<div class="kp">{kp}</div></div>
<div class="scrim"></div>
<div class="sheet">
  <div class="grab"></div>
  <div class="shi">{ic("shield",34,"#19D3A2")}</div>
  <div class="h2" style="text-align:center;margin-top:14px">Arama için izin gerekli</div>
  <div class="t2" style="text-align:center;margin:8px 6px 18px;line-height:1.5">Bu numarayı arayabilmemiz için <b>Telefon</b> izni ve <b>varsayılan telefon uygulaması</b> olarak seçilmemiz gerekiyor.</div>
  <div class="mini">{ic("phone",18,"#FF5A5F")}<span class="grow">Telefon izni</span><span class="chip bad sm">Eksik</span></div>
  <div class="mini">{ic("phoneframe",18,"#19D3A2")}<span class="grow">Varsayılan uygulama</span><span class="chip ok sm">Hazır</span></div>
  <div class="btn pri" style="margin-top:16px">İzin ver ve ara</div>
  <div class="btn sec2">Şimdi değil</div>
</div>
'''))

CSS = r'''
:root{--bg:#0A0F1C;--s1:#121A2B;--s2:#1A2438;--s3:#24304A;--t1:#EAF0FF;--t2:#A9B4CC;--t3:#7C89A6;--acc:#19D3A2;--acc2:#3D7BFF;--ok:#22C55E;--bad:#FF5A5F}
*{box-sizing:border-box;margin:0;padding:0}
body{background:#05080F;font-family:"Inter","Roboto","Segoe UI",system-ui,sans-serif;color:var(--t1);padding:40px}
.wrap{display:grid;grid-template-columns:repeat(4,360px);gap:44px 36px}
.title{grid-column:1/-1;display:flex;align-items:center;gap:16px;margin-bottom:8px}
.title h1{font-size:30px}.title p{color:var(--t3)}
.cell{display:flex;flex-direction:column;gap:12px;align-items:center}
.cap{color:var(--t2);font-size:14px;font-weight:600}
.phone{width:360px;height:780px;border-radius:40px;position:relative;overflow:hidden;box-shadow:0 0 0 8px #151c2b,0 30px 60px rgba(0,0,0,.6)}
.sb{height:34px;display:flex;justify-content:space-between;align-items:center;padding:0 24px;font-size:13px;font-weight:600;position:relative;z-index:5}
.sbr{display:flex;gap:6px;align-items:center}
.home{position:absolute;bottom:8px;left:50%;transform:translateX(-50%);width:120px;height:4px;border-radius:4px;background:rgba(234,240,255,.5);z-index:20}
.pad{padding:0 18px}
.row{display:flex;align-items:center}.col{display:flex;flex-direction:column}.center{display:flex;align-items:center;justify-content:center}
.grow{flex:1;min-width:0}
.h1{font-size:24px;font-weight:800;letter-spacing:-.4px}.h2{font-size:20px;font-weight:700}
.t2{color:var(--t2);font-size:13.5px}.t3{color:var(--t3);font-size:12px}
.kick{font-size:11px;letter-spacing:1.6px;color:var(--acc);font-weight:700;margin:14px 0 6px}
.av{border-radius:50%;display:flex;align-items:center;justify-content:center;font-weight:700;color:#fff;flex:none;box-shadow:inset 0 0 0 1px rgba(255,255,255,.15)}
.glow{position:absolute;border-radius:50%;filter:blur(60px)}.g1{width:260px;height:260px;background:#19D3A2;opacity:.25;top:80px;left:-60px}.g2{width:260px;height:260px;background:#3D7BFF;opacity:.28;bottom:120px;right:-80px}
.dots{display:flex;gap:6px;margin-top:20px}.dots i{width:8px;height:8px;border-radius:4px;background:var(--s3)}.dots i.on{width:22px;background:var(--acc)}
.bottom{position:absolute;bottom:30px;left:18px;right:18px}
.btn{height:54px;border-radius:18px;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:16px}
.btn.pri{background:linear-gradient(135deg,#19D3A2,#3D7BFF);color:#06101f;box-shadow:0 12px 30px rgba(25,211,162,.3)}
.btn.dis{opacity:.45;box-shadow:none}
.btn.sec2{color:var(--t2);margin-top:6px}
.link{text-align:center;color:var(--acc);font-size:13px;font-weight:600;margin-top:14px}
.prog{height:6px;border-radius:6px;background:var(--s2);overflow:hidden}.prog div{height:100%;background:linear-gradient(90deg,#19D3A2,#3D7BFF);border-radius:6px}
.pc{display:flex;align-items:center;gap:12px;background:var(--s1);border:1px solid rgba(255,255,255,.05);border-radius:18px;padding:12px;margin-bottom:9px}
.pc.done{border-color:rgba(25,211,162,.25)}
.pi{width:42px;height:42px;border-radius:13px;background:var(--s2);display:flex;align-items:center;justify-content:center;flex:none}
.pt{font-weight:700;font-size:14px}.pd{color:var(--t3);font-size:11.5px;margin-top:2px}
.tag{font-size:9.5px;font-weight:700;color:#FFB547;background:rgba(255,181,71,.12);padding:2px 6px;border-radius:6px;margin-left:4px;vertical-align:middle}.tag.opt{color:var(--t3);background:var(--s2)}
.chip{font-size:12px;font-weight:700;padding:7px 11px;border-radius:12px;display:inline-flex;align-items:center;gap:4px;white-space:nowrap;flex:none}
.chip.ok{color:var(--acc);background:rgba(25,211,162,.12)}.chip.go{color:#06101f;background:var(--acc)}.chip.bad{color:var(--bad);background:rgba(255,90,95,.14)}.chip.sm{padding:5px 9px;font-size:11px}
.srch{height:50px;border-radius:25px;background:var(--s1);display:flex;align-items:center;gap:10px;padding:0 10px 0 16px;color:var(--t3);font-size:14.5px;border:1px solid rgba(255,255,255,.04)}
.srch.f{border:1.5px solid var(--acc);background:var(--s2)}.typed{color:var(--t1)}.caret{width:2px;height:20px;background:var(--acc);margin-left:-8px}
.num{text-align:center;font-size:34px;font-weight:300;letter-spacing:1px;margin:26px 0 10px}
.sug{margin:0 18px 10px;background:var(--s1);border-radius:16px;padding:10px 12px;display:flex;align-items:center;gap:10px}
.sug u{text-decoration:none;color:var(--acc)}
.nm{font-weight:600;font-size:14.5px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.kp{display:grid;grid-template-columns:repeat(3,76px);gap:10px 26px;justify-content:center}
.key{width:76px;height:62px;border-radius:31px;background:var(--s1);display:flex;flex-direction:column;align-items:center;justify-content:center}
.key b{font-size:26px;font-weight:500;line-height:1}.key small{font-size:9px;color:var(--t3);letter-spacing:1px;margin-top:3px;height:10px}
.callbtn{width:70px;height:70px;border-radius:35px;background:linear-gradient(135deg,#22C55E,#19D3A2);display:flex;align-items:center;justify-content:center;box-shadow:0 12px 28px rgba(34,197,94,.4)}
.ghost{width:46px;height:46px;display:flex;align-items:center;justify-content:center}
.nav{position:absolute;bottom:0;left:0;right:0;height:84px;background:rgba(18,26,43,.96);backdrop-filter:blur(10px);display:flex;justify-content:space-around;padding-top:10px;border-top:1px solid rgba(255,255,255,.05)}
.ni{display:flex;flex-direction:column;align-items:center;gap:4px;font-size:11.5px;color:var(--t3);font-weight:600}
.ni .pill{width:60px;height:32px;border-radius:16px;display:flex;align-items:center;justify-content:center}
.ni.a{color:var(--t1)}.ni.a .pill{background:var(--acc)}
.chips{display:flex;gap:8px;margin:12px 0 4px}.fc{font-size:12.5px;padding:8px 13px;border-radius:14px;background:var(--s1);color:var(--t2);font-weight:600}.fc.a{background:var(--t1);color:#0B1220}.fc b{color:var(--bad)}
.sec{font-size:12px;font-weight:700;color:var(--t3);letter-spacing:.6px;text-transform:uppercase;margin:14px 0 6px}
.lr{display:flex;align-items:center;gap:12px;padding:8px 0}
.cbtn{width:38px;height:38px;border-radius:19px;background:rgba(25,211,162,.12);display:flex;align-items:center;justify-content:center;flex:none}
.fab{position:absolute;right:18px;bottom:102px;width:58px;height:58px;border-radius:20px;background:linear-gradient(135deg,#19D3A2,#3D7BFF);display:flex;align-items:center;justify-content:center;box-shadow:0 12px 26px rgba(25,211,162,.35)}
.fab.ext{width:auto;padding:0 18px;gap:8px;color:#0B1220;font-weight:700;font-size:14px}
.favs{display:flex;gap:12px}.fav{display:flex;flex-direction:column;align-items:center;gap:6px;font-size:11.5px;color:var(--t2)}
.me-card{display:flex;align-items:center;gap:12px;background:var(--s1);border-radius:16px;padding:10px 12px;margin:14px 0 4px}
.letter{color:var(--acc);font-weight:800;font-size:13px;margin:10px 0 0}
.aidx{position:absolute;right:4px;top:230px;display:flex;flex-direction:column;font-size:8.5px;color:var(--t3);font-weight:700;gap:1.5px;align-items:center;width:14px}
.aidx span.a{color:#0B1220;background:var(--acc);border-radius:6px;width:14px;text-align:center}
.hero-bg{position:absolute;top:0;left:0;right:0;height:260px;background:radial-gradient(circle at 50% 30%,rgba(255,79,139,.35),transparent 65%)}
.acts{display:flex;justify-content:center;gap:14px;margin:20px 0 16px;position:relative}
.act{display:flex;flex-direction:column;align-items:center;gap:6px;font-size:11.5px;color:var(--t2);font-weight:600}
.act div{width:58px;height:58px;border-radius:20px;background:var(--s1);display:flex;align-items:center;justify-content:center}
.act.p div{background:linear-gradient(135deg,#19D3A2,#3D7BFF)}
.card{background:var(--s1);border-radius:20px;padding:4px 14px;margin-bottom:10px;border:1px solid rgba(255,255,255,.04)}
.cr{display:flex;align-items:center;gap:14px;padding:11px 0;border-bottom:1px solid rgba(255,255,255,.05)}.cr:last-child{border-bottom:0}.cr.sm{padding:8px 0}
.ch{display:flex;justify-content:space-between;font-weight:700;font-size:13.5px;padding:12px 0 4px}
mark{background:rgba(25,211,162,.22);color:var(--acc);border-radius:4px;padding:0 1px}
.kb{position:absolute;bottom:0;left:0;right:0;background:#161E30;padding:10px 4px 26px}
.kbr{display:flex;justify-content:center;gap:4px;margin-bottom:8px}.kbr span{width:26px;height:40px;background:#26314A;border-radius:7px;display:flex;align-items:center;justify-content:center;font-size:15px}
.kbr .w{width:54px;font-size:12px}.kbr .sp{width:170px;font-size:12px;color:var(--t3)}.kbr .acc{background:var(--acc);color:#0B1220;font-weight:700}
.callbg{position:absolute;inset:0;background:radial-gradient(circle at 50% 28%,rgba(139,92,246,.45),transparent 55%),radial-gradient(circle at 80% 90%,rgba(61,123,255,.3),transparent 50%),#080C18}
.callbg.in{background:radial-gradient(circle at 50% 30%,rgba(255,79,139,.42),transparent 55%),radial-gradient(circle at 20% 90%,rgba(25,211,162,.25),transparent 50%),#080C18}
.callbg.on{background:radial-gradient(circle at 50% 22%,rgba(255,79,139,.3),transparent 50%),radial-gradient(circle at 50% 100%,rgba(25,211,162,.2),transparent 50%),#080C18}
.rings{margin:34px 0 22px;position:relative;display:flex;align-items:center;justify-content:center;width:220px;height:220px;border-radius:50%;box-shadow:0 0 0 0 transparent;background:radial-gradient(circle,rgba(139,92,246,.0) 54%,rgba(139,92,246,.18) 55%,rgba(139,92,246,.0) 70%,rgba(139,92,246,.10) 71%,rgba(139,92,246,0) 100%)}
.rings.g{background:radial-gradient(circle,rgba(255,79,139,0) 56%,rgba(255,79,139,.2) 57%,rgba(255,79,139,0) 72%,rgba(255,79,139,.1) 73%,transparent 100%)}
.ctl3{position:absolute;bottom:170px;left:0;right:0;display:flex;justify-content:center;gap:30px}
.cb{display:flex;flex-direction:column;align-items:center;gap:7px;font-size:12px;color:var(--t2);font-weight:600}
.cb div{width:64px;height:64px;border-radius:32px;background:rgba(255,255,255,.08);display:flex;align-items:center;justify-content:center;backdrop-filter:blur(8px)}
.cb.on div{background:var(--t1)}
.endwrap{position:absolute;bottom:52px;left:0;right:0;display:flex;justify-content:center}
.end{width:76px;height:76px;border-radius:38px;background:linear-gradient(135deg,#FF5A5F,#E11D48);display:flex;align-items:center;justify-content:center;box-shadow:0 14px 32px rgba(225,29,72,.45)}
.end svg{transform:rotate(135deg)}
.quick{position:absolute;bottom:170px;left:0;right:0;display:flex;justify-content:center;gap:16px}
.q{display:flex;align-items:center;gap:8px;background:rgba(255,255,255,.08);padding:11px 15px;border-radius:16px;font-size:12.5px;font-weight:600;color:var(--t1)}
.swipe{position:absolute;bottom:74px;left:22px;right:22px;height:76px;border-radius:38px;background:rgba(255,255,255,.08);display:flex;align-items:center;justify-content:space-between;padding:0 7px;border:1px solid rgba(255,255,255,.08)}
.sd{width:62px;height:62px;border-radius:31px;display:flex;align-items:center;justify-content:center}.sd.red{background:#E11D48}.sd.red svg{transform:rotate(135deg)}.sd.green{background:linear-gradient(135deg,#22C55E,#19D3A2);box-shadow:0 0 0 8px rgba(34,197,94,.18)}
.st{color:var(--t2);font-size:13px;font-weight:600;letter-spacing:1px}
.timer{font-size:16px;color:var(--acc);font-weight:700;margin-top:6px;font-variant-numeric:tabular-nums}
.wave{display:flex;gap:3px;align-items:center;height:30px;margin-top:12px}.wave i{width:3px;border-radius:2px;background:linear-gradient(#19D3A2,#3D7BFF);opacity:.85}
.grid3{position:absolute;bottom:160px;left:0;right:0;display:grid;grid-template-columns:repeat(3,84px);justify-content:center;gap:18px 26px}
.tg{width:44px;height:26px;border-radius:13px;background:var(--s3);position:relative;flex:none}.tg i{position:absolute;top:3px;left:3px;width:20px;height:20px;border-radius:10px;background:#A9B4CC}
.tg.on{background:var(--acc)}.tg.on i{left:21px;background:#fff}
.seg{background:var(--s2);border-radius:10px;padding:5px 10px;font-size:12px}
.sw{display:flex;gap:6px}.sw i{width:20px;height:20px;border-radius:10px}.sw i.a{box-shadow:0 0 0 2px var(--s1),0 0 0 4px #fff}
.scrim{position:absolute;inset:0;background:rgba(3,6,14,.6)}
.sheet{position:absolute;left:0;right:0;bottom:0;background:var(--s1);border-radius:30px 30px 0 0;padding:12px 22px 34px;border-top:1px solid rgba(255,255,255,.08)}
.grab{width:42px;height:5px;border-radius:3px;background:var(--s3);margin:0 auto 18px}
.shi{width:72px;height:72px;border-radius:24px;background:rgba(25,211,162,.12);margin:0 auto;display:flex;align-items:center;justify-content:center}
.mini{display:flex;align-items:center;gap:10px;background:var(--s2);border-radius:14px;padding:11px 12px;margin-top:8px;font-size:13.5px;font-weight:600}
.me svg{display:block}
'''

html = f'''<!doctype html><html><head><meta charset="utf-8"><style>{CSS}</style></head><body>
<div class="wrap"><div class="title"><div style="width:56px;height:56px">{ICON}</div><div><h1>Eymen Telefon — Ekran Tasarımları</h1><p>Rehber · Son aramalar · Tuş takımı · Arama ekranları · İzinler · Ayarlar — v1.0 tasarım referansı (Codex bu görsellerin dışına çıkamaz)</p></div></div>
{''.join(S)}</div></body></html>'''

OUT.mkdir(parents=True, exist_ok=True)
(OUT / "_kaynak_mockups.html").write_text(html)
with sync_playwright() as p:
    b = p.chromium.launch(executable_path="/usr/bin/google-chrome", args=["--no-sandbox"])
    pg = b.new_page(viewport={"width": 1700, "height": 1000}, device_scale_factor=2)
    pg.set_content(html); pg.wait_for_timeout(400)
    for el in pg.query_selector_all(".phone"):
        el.screenshot(path=str(OUT / f'{el.get_attribute("id")}.png'))
    pg2 = b.new_page(viewport={"width": 1700, "height": 1000}, device_scale_factor=1)
    pg2.set_content(html); pg2.wait_for_timeout(400)
    pg2.screenshot(path=str(OUT / "00_genel_bakis.png"), full_page=True)
    b.close()
print("ok", len(S))
