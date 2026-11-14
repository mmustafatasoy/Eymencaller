import re, sys, pathlib, xml.etree.ElementTree as ET
src = pathlib.Path(sys.argv[1]).read_text()
start = src.index("P = {"); end = src.index("HANDSET =")
ns = {}; exec(src[start:end], ns); P = ns["P"]
HANDSET_D = "M6.62,10.79c1.44,2.83 3.76,5.14 6.59,6.59l2.2,-2.2c0.27,-0.27 0.67,-0.36 1.02,-0.24 1.12,0.37 2.33,0.57 3.57,0.57 0.55,0 1,0.45 1,1V20c0,0.55 -0.45,1 -1,1 -9.39,0 -17,-7.61 -17,-17 0,-0.55 0.45,-1 1,-1h3.5c0.55,0 1,0.45 1,1 0,1.25 0.2,2.45 0.57,3.57 0.11,0.35 0.03,0.74 -0.25,1.02l-2.2,2.2z"
P["star_fill"] = P["star"].replace("<polygon", '<polygon fill="currentColor"')
AD = {"search":"buyutec","sliders":"ayarlar","star":"yildiz","star_fill":"yildiz_dolu","clock":"saat","users":"kisiler","dialpad":"tuslar","backspace":"sil","micoff":"sessiz","speaker":"hoparlor","pause":"beklet","useradd":"kisi_ekle","msg":"mesaj","video":"video","shield":"kalkan","bell":"bildirim","card":"kart","check":"onay","chev":"ileri","back":"geri","in":"gelen","out":"giden","more":"daha_fazla","edit":"duzenle","block":"engelle","moon":"tema","phoneframe":"cihaz","info":"bilgi","file":"dosya","share":"paylas","plus":"arti","mail":"eposta","vibrate":"titresim","palette":"palet","alarm":"alarm","bt":"bluetooth"}
def f(v): return float(v)
def g(x): return ("%.3f" % x).rstrip("0").rstrip(".")
def conv(el):
    t = el.tag; a = el.attrib
    if t == "circle":
        cx, cy, r = f(a["cx"]), f(a["cy"]), f(a["r"])
        return f"M{g(cx-r)},{g(cy)} a{g(r)},{g(r)} 0 1,0 {g(2*r)},0 a{g(r)},{g(r)} 0 1,0 {g(-2*r)},0"
    if t == "line": return f"M{a['x1']},{a['y1']} L{a['x2']},{a['y2']}"
    if t in ("polyline","polygon"):
        pts = a["points"].split(); pairs = [pts[i]+","+pts[i+1] for i in range(0,len(pts),2)]
        return "M" + " L".join(pairs) + (" Z" if t=="polygon" else "")
    if t == "rect":
        x,y,w,h = f(a["x"]),f(a["y"]),f(a["width"]),f(a["height"]); r = f(a.get("rx","0"))
        if r == 0: return f"M{g(x)},{g(y)} H{g(x+w)} V{g(y+h)} H{g(x)} Z"
        return (f"M{g(x+r)},{g(y)} H{g(x+w-r)} A{g(r)},{g(r)} 0 0,1 {g(x+w)},{g(y+r)} V{g(y+h-r)} "
                f"A{g(r)},{g(r)} 0 0,1 {g(x+w-r)},{g(y+h)} H{g(x+r)} A{g(r)},{g(r)} 0 0,1 {g(x)},{g(y+h-r)} "
                f"V{g(y+r)} A{g(r)},{g(r)} 0 0,1 {g(x+r)},{g(y)} Z")
    if t == "path": return a["d"]
    raise ValueError(t)
out = pathlib.Path(sys.argv[2]); out.mkdir(parents=True, exist_ok=True)
HEAD = '<?xml version="1.0" encoding="utf-8"?>\n<!-- Otomatik üretildi (design/mockup_uretici.py ikonları). DEĞİŞTİRME. -->\n<vector xmlns:android="http://schemas.android.com/apk/res/android"\n    android:width="24dp" android:height="24dp"\n    android:viewportWidth="24" android:viewportHeight="24">\n'
for k, body in P.items():
    root = ET.fromstring(f'<svg xmlns="x">{body}</svg>'.replace('xmlns="x"',''))
    paths = []
    for el in root:
        d = conv(el); fill = el.attrib.get("fill") == "currentColor"; stroke = el.attrib.get("stroke") != "none"
        s = f'    <path android:pathData="{d}"\n        android:fillColor="{"#FFFFFFFF" if fill else "#00000000"}"'
        if stroke: s += '\n        android:strokeColor="#FFFFFFFF" android:strokeWidth="2"\n        android:strokeLineCap="round" android:strokeLineJoin="round"'
        paths.append(s + "/>")
    (out / f"ik_{AD[k]}.xml").write_text(HEAD + "\n".join(paths) + "\n</vector>\n")
(out / "ik_telefon.xml").write_text(HEAD + f'    <path android:pathData="{HANDSET_D}" android:fillColor="#FFFFFFFF"/>\n</vector>\n')
print(len(P)+1, "ikon")
