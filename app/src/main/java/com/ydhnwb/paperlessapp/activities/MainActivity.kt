package com.ydhnwb.paperlessapp.activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.fragments.DashboardFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.widget.Toast
import com.ydhnwb.paperlessapp.contracts.activities.MainActivityContract
import com.ydhnwb.paperlessapp.fragments.NotificationFragment
import com.ydhnwb.paperlessapp.fragments.ProfileFragment
import com.ydhnwb.paperlessapp.presenters.activities.MainActivityPresenter
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil


class MainActivity : AppCompatActivity(), MainActivityContract.View {
    companion object{ var navStatus = -1 }
    private var fragment : Fragment? = null
    private val presenter = MainActivityPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        if(savedInstanceState == null){
            nav_view.selectedItemId = R.id.navigation_dashboard
        }
        Thread(Runnable {
            if (PaperlessUtil.isFirstTime(this@MainActivity)) {
                runOnUiThread { startActivity(Intent(this@MainActivity, IntroActivity::class.java).also {
                    finish()
                })}
            }
        }).start()
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.navigation_dashboard -> {
                if(navStatus != 0){
                    fragment = DashboardFragment(presenter)
                    navStatus = 0
                }
            }
            R.id.navigation_explore -> {
                if(navStatus != 1){
                    fragment = DashboardFragment(presenter)
                    navStatus = 1
                }
            }
            R.id.navigation_notifications -> {
                if(navStatus != 2){
                    fragment = NotificationFragment(presenter)
                    navStatus = 2
                }
            }
            R.id.navigation_profile -> {
                if(navStatus != 3){
                    fragment = ProfileFragment()
                    navStatus = 3
                }
            }
            else -> {
                navStatus = 0
                fragment = DashboardFragment(presenter)
            }
        }
        if(fragment == null){
            navStatus = 0
            fragment = DashboardFragment(presenter)
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.screen_container, fragment!!)
        fragmentTransaction.commit()
        true
    }

    override fun showIntro() {}

    override fun showLogin() {}

    override fun showSheet() {}

    override fun closeSheet() {}

    override fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}