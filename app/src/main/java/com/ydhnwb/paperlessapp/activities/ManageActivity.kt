package com.ydhnwb.paperlessapp.activities

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.fragments.manage.EmployeeFragment
import com.ydhnwb.paperlessapp.fragments.manage.EtalaseFragment
import com.ydhnwb.paperlessapp.fragments.manage.HomeFragment
import com.ydhnwb.paperlessapp.fragments.manage.ProductFragment
import com.ydhnwb.paperlessapp.models.Store
import kotlinx.android.synthetic.main.activity_manage.*
import kotlinx.android.synthetic.main.app_bar_manage.*

class ManageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private var openFirst = true
        private var navStatus = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getCurrentStore()?.name
        initComp()
        if(savedInstanceState == null){
            openFirst = true
            val item = nav_view.getMenu().getItem(0).setChecked(true)
            onNavigationItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.manage, menu)
        return true
    }

    override fun onBackPressed() { if (drawer_layout.isDrawerOpen(GravityCompat.START)) { drawer_layout.closeDrawer(GravityCompat.START) } else { super.onBackPressed() } }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment : Fragment? = null
        when (item.itemId) {
            R.id.nav_home -> {
                if(navStatus == 0 && !openFirst){
                    drawer_layout.closeDrawer(GravityCompat.START)
                }else{
                    navStatus = 0
                    openFirst = false
                    fragment =
                        HomeFragment()
                }
            }
            R.id.nav_etalase -> {
                if(navStatus == 1 && !openFirst){
                    drawer_layout.closeDrawer(GravityCompat.START)
                }else{
                    openFirst = false
                    navStatus = 1
                    fragment =
                        EtalaseFragment()
                }
            }
            R.id.nav_product -> {
                if(navStatus == 2 && !openFirst){
                    drawer_layout.closeDrawer(GravityCompat.START)
                }else{
                    openFirst = false
                    navStatus = 2
                    fragment = ProductFragment()
                }
            }

            R.id.nav_employee -> {
                if(navStatus == 3 && !openFirst){
                    drawer_layout.closeDrawer(GravityCompat.START)
                }else{
                    openFirst = false
                    navStatus = 3
                    fragment = EmployeeFragment()
                }
            }

            R.id.nav_setting -> {
                startActivity(Intent(this@ManageActivity, RegisterActivity::class.java))
            }

            else -> {
                openFirst = false
                navStatus = 0
                fragment = HomeFragment()
            }
        }

        if(fragment != null){
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            ft.replace(R.id.container_fragment, fragment)
            ft.commit()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initComp(){
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun getCurrentStore() = intent.getParcelableExtra<Store>("STORE")
}
