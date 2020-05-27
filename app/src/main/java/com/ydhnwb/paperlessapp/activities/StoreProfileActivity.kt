package com.ydhnwb.paperlessapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.api.load
import com.google.android.material.appbar.AppBarLayout
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.fragments.others.StoreCatalogFragment
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.CustomFragmentPagerAdapter
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.StoreState
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import kotlinx.android.synthetic.main.activity_store_profile.*
import kotlinx.android.synthetic.main.content_store_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class StoreProfileActivity : AppCompatActivity() {
    private val storeViewModel : StoreViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_profile)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        observerToStore()
        fetch()
        initCollapsingToolbar()
    }

    private fun getPassedStoreId() = intent.getIntExtra("store_id", 0)

    private fun fetch(){
        if(getPassedStoreId() != 0){
            storeViewModel.fetchStoreById(PaperlessUtil.getToken(this), getPassedStoreId().toString())
        }
    }

    private fun observerToStore(){
        if(!storeViewModel.listenToHasFetched().value!!){         }
        storeViewModel.listenToStore().observe(this, Observer { handleStore(it) })
        storeViewModel.listenUIState().observer(this, Observer { handleUIState(it) })
    }

    private fun handleStore(it: Store){
        it.let {
            store_logo.load(it.store_logo)
            store_name.text = it.name
            if(it.products.isNotEmpty()){ handleCatalogProduct(it.products) }
        }
    }

    private fun handleUIState(it : StoreState){
        when(it){
            is StoreState.IsLoading -> {
                if(it.isLoading){
                    loading.visibility = View.VISIBLE
                }else{
                    loading.visibility = View.GONE
                }
            }
            is StoreState.ShowToast -> toast(it.message)
        }
    }


    private fun handleCatalogProduct(it: List<Product>){
        it.isNotEmpty().run {
            val categories = it.map { product -> product.category!! }.distinctBy { category -> category.name }
            val fragmentAdapter = CustomFragmentPagerAdapter(supportFragmentManager)
            if(it.isNotEmpty()){ fragmentAdapter.addFragment(StoreCatalogFragment(), resources.getString(R.string.common_all)) }
            for (c in categories){ fragmentAdapter.addFragment(StoreCatalogFragment.instance(c), c.name!!.toUpperCase(Locale.getDefault())) }
            viewpager.adapter = fragmentAdapter
            tabs.setupWithViewPager(viewpager)
        }
    }

    private fun initCollapsingToolbar(){
        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            var scrollRange = -1
            if (scrollRange == -1) { scrollRange = app_bar.totalScrollRange }
            when {
                scrollRange + verticalOffset == 0 -> {
                    toolbar_layout.title = if (storeViewModel.listenToStore().value != null) {
                        ""
                    } else {
                        storeViewModel.listenToStore().value!!.name
                    }
                }
                scrollRange + verticalOffset > 0 -> {
                    toolbar_layout.title = " "
                }
            }
        })
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
