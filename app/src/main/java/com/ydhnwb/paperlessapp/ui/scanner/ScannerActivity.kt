package com.ydhnwb.paperlessapp.ui.scanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.ydhnwb.paperlessapp.R
import kotlinx.android.synthetic.main.activity_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView


class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private lateinit var scannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        scannerView = ZXingScannerView(this)
        content_frame.addView(scannerView)
        supportActionBar?.hide()
        checkPermisson()
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(rawResult: Result?) {
        rawResult?.let {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
            println(it.text)
            val i = Intent()
            i.putExtra("CODE", it.text)
            i.putExtra("IS_STORE", it.text.contains(resources.getString(R.string.qr_suffix_store), true))
            setResult(Activity.RESULT_OK, i)

            finish()
        }
    }

    private fun checkPermisson(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Aplikasi tidak berjalan tanpa izin ke kamera", Toast.LENGTH_LONG).show()
                finish()
            } else {
                ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.CAMERA), 20)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            20 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("IntroAct", "Permission has been denied by user")
                } else {
                    Log.i("IntroAct", "Permission has been granted by user")
                }
            }
        }
    }
}
