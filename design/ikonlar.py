"""Eymen Telefon ikon seti (24x24). Hem mockup üretici hem Android vektör çevirici bunu kullanır.
Ç = çizgi (stroke), D = dolu (fill). 'evenodd' içeren yollar Android'de fillType=evenOdd olur."""
import math

def _disli():
    # 8 dişli çark + ortada delik
    pts = []
    for i in range(16):
        a = math.pi * 2 * i / 16 - math.pi / 16
        r = 10.2 if i % 2 == 0 else 7.6
        for da in (-0.17, 0.17):
            pts.append((12 + r * math.cos(a + da), 12 + r * math.sin(a + da)))
    d = "M" + " L".join(f"{x:.2f},{y:.2f}" for x, y in pts) + " Z M12,8.6 a3.4,3.4 0 1,0 0,6.8 a3.4,3.4 0 1,0 0,-6.8 Z"
    return f'<path fill="currentColor" fill-rule="evenodd" stroke="none" d="{d}"/>'

TELEFON_D = "M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"

IKONLAR = {
    # ---- dolu (iOS tarzı) ----
    "telefon": f'<path fill="currentColor" stroke="none" d="{TELEFON_D}"/>',
    "yildiz_dolu": '<polygon fill="currentColor" stroke="none" points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26"/>',
    "saat_dolu": '<path fill="currentColor" fill-rule="evenodd" stroke="none" d="M12 2a10 10 0 1 0 0 20a10 10 0 1 0 0-20z M11 6h2v5.4l3.6 2.1-1 1.7L11 12.6z"/>',
    "kisi_dolu": '<path fill="currentColor" fill-rule="evenodd" stroke="none" d="M12 2a10 10 0 1 0 0 20a10 10 0 1 0 0-20z M12 6a3.6 3.6 0 1 0 0 7.2a3.6 3.6 0 1 0 0-7.2z M5.9 18.4c1.4-2.2 3.6-3.4 6.1-3.4s4.7 1.2 6.1 3.4A8.4 8.4 0 0 1 12 20.6a8.4 8.4 0 0 1-6.1-2.2z"/>',
    "tuslar": ''.join(f'<circle cx="{x}" cy="{y}" r="1.9" fill="currentColor" stroke="none"/>' for y in (3.5, 9.2, 14.9) for x in (5.5, 12, 18.5)) + '<circle cx="12" cy="20.6" r="1.9" fill="currentColor" stroke="none"/>',
    "mesaj_dolu": '<path fill="currentColor" stroke="none" d="M12 3C6.5 3 2 6.6 2 11c0 2.4 1.3 4.6 3.4 6.1L4.5 21l4.3-2.2c1 .3 2.1.4 3.2.4 5.5 0 10-3.6 10-8.1S17.5 3 12 3z"/>',
    "video_dolu": '<path fill="currentColor" stroke="none" d="M2.5 7.5A2.5 2.5 0 0 1 5 5h8.5A2.5 2.5 0 0 1 16 7.5v9a2.5 2.5 0 0 1-2.5 2.5H5a2.5 2.5 0 0 1-2.5-2.5z M17.5 10l4-2.6v9.2l-4-2.6z"/>',
    "eposta_dolu": '<path fill="currentColor" stroke="none" d="M2 6.5A1.5 1.5 0 0 1 3.5 5h17A1.5 1.5 0 0 1 22 6.5v.4l-10 6.3L2 6.9z M2 9.3l10 6.3 10-6.3v8.2a1.5 1.5 0 0 1-1.5 1.5h-17A1.5 1.5 0 0 1 2 17.5z"/>',
    "hoparlor": '<path fill="currentColor" stroke="none" d="M3 9h4l5-4.2v14.4L7 15H3z"/><path d="M15.5 8.5a5 5 0 0 1 0 7"/><path d="M18.5 5.5a9.2 9.2 0 0 1 0 13"/>',
    "sessiz": '<rect x="9" y="2.5" width="6" height="11.5" rx="3" fill="currentColor" stroke="none"/><path d="M5.5 10.5v1a6.5 6.5 0 0 0 13 0v-1"/><line x1="12" y1="18" x2="12" y2="21.5"/><line x1="3.5" y1="3" x2="20.5" y2="21"/>',
    "beklet": '<rect x="6" y="4" width="4.2" height="16" rx="1.2" fill="currentColor" stroke="none"/><rect x="13.8" y="4" width="4.2" height="16" rx="1.2" fill="currentColor" stroke="none"/>',
    "arti": '<line x1="12" y1="4.5" x2="12" y2="19.5"/><line x1="4.5" y1="12" x2="19.5" y2="12"/>',
    "kisiler_dolu": '<circle cx="9" cy="7.5" r="3.8" fill="currentColor" stroke="none"/><path fill="currentColor" stroke="none" d="M2 19.5c0-3.6 3.1-6.3 7-6.3s7 2.7 7 6.3V20H2z"/><circle cx="17" cy="8.5" r="3" fill="currentColor" stroke="none"/><path fill="currentColor" stroke="none" d="M17.6 13.4c2.6.3 4.4 2.3 4.4 5V20h-4.4v-.5c0-2.4-1-4.5-2.7-5.8.8-.2 1.7-.3 2.7-.3z"/>',
    "alarm": '<circle cx="12" cy="13" r="7.5"/><polyline points="12 9.5 12 13 14.5 14.5"/><line x1="5" y1="3.5" x2="2.5" y2="6"/><line x1="19" y1="3.5" x2="21.5" y2="6"/>',
    "disli": _disli(),
    "bluetooth": '<polyline points="7 7 17 17 12 22 12 2 17 7 7 17"/>',
    # ---- çizgi ----
    "buyutec": '<circle cx="10.5" cy="10.5" r="6.5"/><line x1="15.5" y1="15.5" x2="20.5" y2="20.5"/>',
    "bilgi": '<circle cx="12" cy="12" r="9.5"/><line x1="12" y1="16.5" x2="12" y2="11"/><circle cx="12" cy="7.6" r="0.9" fill="currentColor"/>',
    "ileri": '<polyline points="9.5 5.5 16 12 9.5 18.5"/>',
    "geri": '<polyline points="15 4.5 7.5 12 15 19.5"/>',
    "sil": '<path d="M21 5H9l-7 7 7 7h12a1 1 0 0 0 1-1V6a1 1 0 0 0-1-1z"/><line x1="17.5" y1="9" x2="12.5" y2="15"/><line x1="12.5" y1="9" x2="17.5" y2="15"/>',
    "giden": '<line x1="7" y1="17" x2="17" y2="7"/><polyline points="8 7 17 7 17 16"/>',
    "gelen": '<line x1="17" y1="7" x2="7" y2="17"/><polyline points="16 17 7 17 7 8"/>',
    "engelle": '<circle cx="12" cy="12" r="9"/><line x1="5.6" y1="5.6" x2="18.4" y2="18.4"/>',
    "kalkan": '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><polyline points="9 12 11 14 15 10"/>',
    "bildirim": '<path d="M18 8a6 6 0 0 0-12 0c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.7 21a2 2 0 0 1-3.4 0"/>',
    "kart": '<rect x="4" y="2" width="16" height="20" rx="2"/><circle cx="12" cy="10" r="3"/><path d="M8 17a4 4 0 0 1 8 0"/>',
    "cihaz": '<rect x="6" y="2" width="12" height="20" rx="2"/><line x1="11" y1="18" x2="13" y2="18"/>',
    "tema": '<path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8z"/>',
    "palet": '<circle cx="12" cy="12" r="9"/><circle cx="8" cy="10" r="1.3" fill="currentColor"/><circle cx="12" cy="7.5" r="1.3" fill="currentColor"/><circle cx="16" cy="10" r="1.3" fill="currentColor"/>',
    "titresim": '<rect x="7" y="3" width="10" height="18" rx="2"/><path d="M3 8v8M21 8v8"/>',
    "dosya": '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>',
    "paylas": '<path d="M12 3v12"/><polyline points="8 7 12 3 16 7"/><path d="M5 11v8a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-8"/>',
    "onay": '<polyline points="20 6 9 17 4 12"/>',
    "yildiz": '<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26"/>',
    "duzenle": '<path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4z"/>',
    "daha_fazla": '<circle cx="5" cy="12" r="1.5" fill="currentColor"/><circle cx="12" cy="12" r="1.5" fill="currentColor"/><circle cx="19" cy="12" r="1.5" fill="currentColor"/>',
    "kisi_ekle": '<circle cx="9" cy="8" r="4"/><path d="M2 21v-1a6 6 0 0 1 6-6h2a6 6 0 0 1 6 6v1"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="16" y1="11" x2="22" y2="11"/>',
    "kapat_x": '<line x1="6" y1="6" x2="18" y2="18"/><line x1="18" y1="6" x2="6" y2="18"/>',
}

def svg(ad, boyut=24, renk="currentColor", kalinlik=2):
    return (f'<svg width="{boyut}" height="{boyut}" viewBox="0 0 24 24" fill="none" stroke="{renk}" '
            f'stroke-width="{kalinlik}" stroke-linecap="round" stroke-linejoin="round" style="color:{renk};display:block">'
            f'{IKONLAR[ad]}</svg>')
