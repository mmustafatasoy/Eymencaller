package com.atasoy.eymentelefon.widget

// V13-10 · Bileşenden dokununca görünmeden arar (AramaBaslatici üzerinden), hemen kapanır.

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.atasoy.eymentelefon.MainActivity
import com.atasoy.eymentelefon.konteyner
import com.atasoy.eymentelefon.ui.navigasyon.AnaSekme

class WidgetAramaActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val numara = intent.getStringExtra(EK_NUMARA).orEmpty()
        val k = konteyner()
        if (numara.isBlank() || !k.izinYoneticisi.zorunlularTamam()) {
            startActivity(MainActivity.sekmeIntenti(this, AnaSekme.FAVORILER))
        } else {
            k.aramaBaslatici.ara(numara)
        }
        finish()
    }

    companion object {
        private const val EK_NUMARA = "eymen.widget.numara"

        fun intent(context: Context, numara: String): Intent =
            Intent(context, WidgetAramaActivity::class.java)
                .putExtra(EK_NUMARA, numara)
                .setData(android.net.Uri.fromParts("tel", numara, null))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
