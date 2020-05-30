package com.ydhnwb.paperlessapp.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.CatalogAdapter
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.StoreState
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import kotlinx.android.synthetic.main.activity_store_page.*
import kotlinx.android.synthetic.main.content_store_page.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class StorePageActivity : AppCompatActivity() {
    private val storeViewModel: StoreViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_page)
        setSupportActionBar(toolbar)
        setupUI()
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener{finish()}
        storeViewModel.fetchStoreById(PaperlessUtil.getToken(this), getStoreId().toString())
        storeViewModel.listenToStore().observe(this, Observer { handleStore(it) })
        storeViewModel.listenUIState().observer(this, Observer { handleUIState(it) })
    }

    private fun setupUI(){
        rv_catalog.apply {
            layoutManager = LinearLayoutManager(this@StorePageActivity)
            adapter = CatalogAdapter(mutableListOf(), this@StorePageActivity)
        }
    }

    private fun getStoreId() = intent.getIntExtra("store_id", 0)

    private fun handleStore(it: Store){
        store_logo.load(it.store_logo)
        store_name.text = it.name
        if(!it.products.isNullOrEmpty()){
            val categories = it.products.map { product -> product.category!!.name!! }.toMutableList()
            if(!categories.isNullOrEmpty()){
                categories.add(0,resources.getString(R.string.common_all))
                setupSpinner(categories)
            }
        }
    }

    private fun handleUIState(it: StoreState){
        when(it){
            is StoreState.IsLoading -> {
                store_spinner.isEnabled = !it.isLoading
                if(it.isLoading){
                    loading.visibility = View.VISIBLE
                }else{
                    loading.visibility = View.GONE
                }
            }
            is StoreState.ShowToast -> toast(it.message)
        }
    }

    private fun setupSpinner(categories: List<String>){
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        store_spinner.adapter = spinnerAdapter
        store_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position != 0){
                    val selected = spinnerAdapter.getItem(position)
                    filterRecycler(selected)
                }else{
                    filterRecycler(null)
                }

            }
        }
    }

    private fun filterRecycler(category: String?){
        category?.let {
            val store = storeViewModel.listenToStore().value
            store?.let { s ->
                val products = s.products
                if(products.isNotEmpty()){
                    val filtered = products.filter { product -> product.category?.name.equals(category) }
                    attachToRecycler(filtered)
                }
            }
        } ?: kotlin.run {
            val store = storeViewModel.listenToStore().value
            store?.let { s ->
                val products = s.products
                if(products.isNotEmpty()){ attachToRecycler(products) }
            }
        }
    }

    private fun attachToRecycler(listOfProduct: List<Product>){
        rv_catalog.adapter.let { adapter -> if(adapter is CatalogAdapter) adapter.updateList(listOfProduct) }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}