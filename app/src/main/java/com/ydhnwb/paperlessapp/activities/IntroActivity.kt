package com.ydhnwb.paperlessapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil

class IntroActivity : AppIntro2(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        checkPermisson()
        val sliderPage = SliderPage().apply {
            description = "Rencanakan keuangan anda dengan menganalisa setiap data secara realtime"
            descColor = Color.parseColor("#ffffff")
            imageDrawable = R.drawable.ic_doodle_ecommerce
            bgColor = Color.parseColor("#1E80CE")
        }

        val sliderPage2 = SliderPage().apply {
            description = "Tidak perlu selalu standby di toko, anda tetap bisa melihat laporan meski tengah berlibur"
            descColor = Color.parseColor("#ffffff")
            imageDrawable = R.drawable.ic_doodle_payment_processed_alt
            bgColor = Color.parseColor("#1E80CE")
        }

        val sliderPage3 = SliderPage().apply {
            description = "Tak perlu takut dicurangi karyawan anda dengan manajemen hak akses toko"
            descColor = Color.parseColor("#ffffff")
            imageDrawable = R.drawable.ic_doodle_social_alt
            bgColor = Color.parseColor("#1E80CE")
        }
        addSlide(AppIntroFragment.newInstance(sliderPage))
        addSlide(AppIntroFragment.newInstance(sliderPage2))
        addSlide(AppIntroFragment.newInstance(sliderPage3))
        setZoomAnimation()
        isSkipButtonEnabled = false
        isVibrateOn = true
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        PaperlessUtil.setFirstTime(this@IntroActivity, false).also {
            startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkPermisson(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this@IntroActivity, "Aplikasi tidak berjalan tanpa izin ke kamera", Toast.LENGTH_LONG).show()
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
