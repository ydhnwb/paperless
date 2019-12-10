package com.ydhnwb.paperlessapp.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
        val sliderPage = SliderPage().apply {
            description = "Rencanakan keuangan anda dengan menganalisa setiap data secara realtime"
            descColor = Color.parseColor("#ffffff")
            imageDrawable = R.drawable.ic_doodle_opensource
            bgColor = Color.parseColor("#1E80CE")
        }

        val sliderPage2 = SliderPage().apply {
            description = "Tidak perlu selalu standby di toko, anda tetap bisa melihat laporan meski tengah berlibur"
            descColor = Color.parseColor("#ffffff")
            imageDrawable = R.drawable.ic_doodle_better
            bgColor = Color.parseColor("#1E80CE")
        }

        val sliderPage3 = SliderPage().apply {
            description = "Tak perlu takut dicurangi karyawan anda dengan manajemen hak akses toko"
            descColor = Color.parseColor("#ffffff")
            imageDrawable = R.drawable.ic_doodle_privacy
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
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            finish()
        }
    }
}
