package com.ydhnwb.paperlessapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mancj.materialsearchbar.MaterialSearchBar
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.fragments.others.CatalogFragment
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.CustomFragmentPagerAdapter
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.CatalogState
import com.ydhnwb.paperlessapp.viewmodels.CatalogViewModel

import kotlinx.android.synthetic.main.activity_catalog.*
import kotlinx.android.synthetic.main.content_catalog.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CatalogActivity : AppCompatActivity() {
    private val catalogViewModel : CatalogViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)
        setSupportActionBar(toolbar)
        setupSearchBar()
        catalogViewModel.listenToState().observer(this, Observer { handleUIState(it) })
        catalogViewModel.listenToProducts().observe(this, Observer { handleCatalogProduct(it) })
        fetchFirst()
    }

    private fun setupSearchBar(){
        search_bar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {}
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence?) {
                if(text != null && text.isNotEmpty()){
                    catalogViewModel.search(PaperlessUtil.getToken(this@CatalogActivity), text.toString())
                }
            }
        })
    }

    private fun handleUIState(it: CatalogState){
        when(it){
            is CatalogState.IsLoading -> {
                if (it.state){
                    catalog_loading.visibility = View.VISIBLE
                }else{
                    catalog_loading.visibility = View.GONE
                }
            }
            is CatalogState.ShowToast -> toast(it.message)
        }
    }

    private fun handleCatalogProduct(it: List<Product>){
        it.isNotEmpty().run {
            val categories = it.map { product -> product.category!! }.distinctBy { category -> category.name }
            val fragmentAdapter = CustomFragmentPagerAdapter(supportFragmentManager)
            if(catalogViewModel.listenToProducts().value != null && it.isNotEmpty()){
                fragmentAdapter.addFragment(CatalogFragment(), resources.getString(R.string.common_all))
            }
            for (c in categories){ fragmentAdapter.addFragment(CatalogFragment.instance(c), c.name!!.toUpperCase(Locale.getDefault())) }
            viewpager.adapter = fragmentAdapter
            tabs.setupWithViewPager(viewpager)
        }
    }

    private fun fetchFirst(){
        if(!catalogViewModel.listenToHasFetched().value!!){
            catalogViewModel.search(PaperlessUtil.getToken(this), getPassedQuery()!!)
        }
    }

    private fun getPassedQuery() = intent.getStringExtra("q")

    private fun toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_LONG).show()

}
