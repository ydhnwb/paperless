package com.ydhnwb.paperlessapp.activities

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.fragments.manage.EmployeeFragment
import com.ydhnwb.paperlessapp.fragments.manage.EtalaseFragment
import com.ydhnwb.paperlessapp.fragments.manage.HomeFragment
import com.ydhnwb.paperlessapp.fragments.manage.ProductFragment
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import com.ydhnwb.paperlessapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_manage.*
import kotlinx.android.synthetic.main.app_bar_manage.*
import kotlinx.android.synthetic.main.nav_header_manage.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private var openFirst = true
        private var navStatus = -1
    }

    private val storeViewModel: StoreViewModel by viewModel()
    private val userViewModel : UserViewModel by viewModel()
    private val productViewModel: ProductViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getCurrentStore().name
        initComp()
        userViewModel.profile(PaperlessUtil.getToken(this))
        userViewModel.listenToCurrentUser().observe(this, Observer { handleNavUser(it) })
        storeViewModel.setCurrentManagedStore(getCurrentStore())
        if(savedInstanceState == null){
            openFirst = true
            val item = nav_view.menu.getItem(0).setChecked(true)
            onNavigationItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.manage, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_code -> {
                startActivity(Intent(this, ShowQRActivity::class.java).apply {
                    putExtra("ID", getCurrentStore().id.toString())
                    putExtra("IS_STORE", true)
                })
                true
            }
            else ->  super.onOptionsItemSelected(item)

        }
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
                    fragment = HomeFragment.instance(getCurrentStore())
                }
            }
            R.id.nav_etalase -> {
                if(navStatus == 1 && !openFirst){
                    drawer_layout.closeDrawer(GravityCompat.START)
                }else{
                    openFirst = false
                    navStatus = 1
                    productViewModel.setHasFetched(false)
                    fragment = EtalaseFragment()
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
                    fragment = EmployeeFragment.instance(getCurrentStore())
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

    private fun handleNavUser(it : User){
        with(nav_view.getHeaderView(0)){
            user_image.load(R.drawable.ydhnwb)
            user_name.text = it.name
            user_email.text = it.email
        }

    }
    private fun getCurrentStore() = intent.getParcelableExtra<Store>("STORE")!!

}
