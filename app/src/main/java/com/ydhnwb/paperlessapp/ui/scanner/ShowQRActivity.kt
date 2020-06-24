package com.ydhnwb.paperlessapp.ui.scanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.api.load
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.activity_show_q_r.*

class ShowQRActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_q_r)
        supportActionBar?.hide()
        val code = if(getIsStore()) "STR${getIdExtra()}" else "USR${getIdExtra()}"
        val x = QRCodeWriter().encode(code, BarcodeFormat.QR_CODE, 700, 700)
        qr.load(PaperlessUtil.createBitmap(x))
    }

    private fun getIdExtra() = intent.getStringExtra("ID")!!
    private fun getIsStore() = intent.getBooleanExtra("IS_STORE", false)
}
