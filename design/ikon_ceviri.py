#!/usr/bin/env python3
"""design/ikonlar.py → app/src/main/res/drawable/ik_*.xml (Android vektör). Kullanım: python3 design/ikon_ceviri.py"""
import sys, pathlib, xml.etree.ElementTree as ET
KOK = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(KOK))
from ikonlar import IKONLAR

def g(x): return ("%.3f" % float(x)).rstrip("0").rstrip(".")

def yol(el):
    a = el.attrib; t = el.tag
    if t == "circle":
        cx, cy, r = float(a["cx"]), float(a["cy"]), float(a["r"])
        return f"M{g(cx-r)},{g(cy)} a{g(r)},{g(r)} 0 1,0 {g(2*r)},0 a{g(r)},{g(r)} 0 1,0 {g(-2*r)},0 Z"
    if t == "line": return f"M{a['x1']},{a['y1']} L{a['x2']},{a['y2']}"
    if t in ("polyline", "polygon"):
        p = a["points"].split(); ciftler = [p[i] + "," + p[i+1] for i in range(0, len(p), 2)]
        return "M" + " L".join(ciftler) + (" Z" if t == "polygon" else "")
    if t == "rect":
        x, y, w, h = (float(a[k]) for k in ("x", "y", "width", "height")); r = float(a.get("rx", 0))
        if r == 0: return f"M{g(x)},{g(y)} H{g(x+w)} V{g(y+h)} H{g(x)} Z"
        return (f"M{g(x+r)},{g(y)} H{g(x+w-r)} A{g(r)},{g(r)} 0 0,1 {g(x+w)},{g(y+r)} V{g(y+h-r)} "
                f"A{g(r)},{g(r)} 0 0,1 {g(x+w-r)},{g(y+h)} H{g(x+r)} A{g(r)},{g(r)} 0 0,1 {g(x)},{g(y+h-r)} "
                f"V{g(y+r)} A{g(r)},{g(r)} 0 0,1 {g(x+r)},{g(y)} Z")
    if t == "path": return a["d"]
    raise ValueError(t)

BAS = ('<?xml version="1.0" encoding="utf-8"?>\n<!-- Otomatik: design/ikonlar.py → design/ikon_ceviri.py. Elle DEĞİŞTİRME. -->\n'
       '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n    android:width="24dp" android:height="24dp"\n'
       '    android:viewportWidth="24" android:viewportHeight="24">\n')
hedef = KOK.parent / "app/src/main/res/drawable"
for eski in hedef.glob("ik_*.xml"): eski.unlink()
for ad, govde in IKONLAR.items():
    kok = ET.fromstring(f"<svg>{govde}</svg>")
    parcalar = []
    for el in kok:
        dolu = el.attrib.get("fill") == "currentColor"
        cizgi = el.attrib.get("stroke") != "none"
        s = f'    <path android:pathData="{yol(el)}"\n        android:fillColor="{"#FFFFFFFF" if dolu else "#00000000"}"'
        if el.attrib.get("fill-rule") == "evenodd": s += '\n        android:fillType="evenOdd"'
        if cizgi: s += '\n        android:strokeColor="#FFFFFFFF" android:strokeWidth="2"\n        android:strokeLineCap="round" android:strokeLineJoin="round"'
        parcalar.append(s + "/>")
    (hedef / f"ik_{ad}.xml").write_text(BAS + "\n".join(parcalar) + "\n</vector>\n")
print(len(IKONLAR), "ikon yazıldı")
