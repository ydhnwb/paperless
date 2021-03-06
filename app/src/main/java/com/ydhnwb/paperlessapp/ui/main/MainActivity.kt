package com.ydhnwb.paperlessapp.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.main.dashboard.DashboardFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.ydhnwb.paperlessapp.ui.login.LoginActivity
import com.ydhnwb.paperlessapp.ui.main.explore.ExploreFragment
import com.ydhnwb.paperlessapp.ui.main.notification.NotificationFragment
import com.ydhnwb.paperlessapp.ui.main.profile.ProfileFragment
import com.ydhnwb.paperlessapp.ui.IntroActivity
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil


class MainActivity : AppCompatActivity() {
    companion object{
        var navStatus = -1
        const val CHANNEL_ID = "paperless_app"
        private const val CHANNEL_NAME= "Paperless"
        private const val CHANNEL_DESC = "Android Push Notification Test"
    }
    private var fragment : Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        if(savedInstanceState == null){ nav_view.selectedItemId = R.id.navigation_dashboard }
        Thread(Runnable {
            if (PaperlessUtil.isFirstTime(this@MainActivity)) {
                runOnUiThread { startActivity(Intent(this@MainActivity, IntroActivity::class.java).also {
                    finish()
                })}
            }else{
                if(PaperlessUtil.getToken(this) == null){
                    runOnUiThread { startActivity(Intent(this, LoginActivity::class.java)).also {
                        finish()
                    } }
                }
            }
        }).start()
        setupNotificationManager()
    }

    private fun setupNotificationManager(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = CHANNEL_DESC
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.navigation_dashboard -> {
                if(navStatus != 0){
                    fragment =
                        DashboardFragment()
                    navStatus = 0
                }
            }
            R.id.navigation_explore -> {
                if(navStatus != 1){
                    fragment =
                        ExploreFragment()
                    navStatus = 1
                }
            }
            R.id.navigation_notifications -> {
                if(navStatus != 2){
                    fragment =
                        NotificationFragment()
                    navStatus = 2
                }
            }
            R.id.navigation_profile -> {
                if(navStatus != 3){
                    fragment =
                        ProfileFragment()
                    navStatus = 3
                }
            }
            else -> {
                navStatus = 0
                fragment =
                    DashboardFragment()
            }
        }
        if(fragment == null){
            navStatus = 0
            fragment =
                DashboardFragment()
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.screen_container, fragment!!)
        fragmentTransaction.commit()
        true
    }

}
