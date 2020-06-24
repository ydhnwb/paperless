package com.ydhnwb.paperlessapp.ui.store_page

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.shared_adapter.CatalogAdapter
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.activity_store_page.*
import kotlinx.android.synthetic.main.content_store_page.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class StorePageActivity : AppCompatActivity() {
    private val storePageViewModel: StorePageViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_page)
        setSupportActionBar(toolbar)
        setupUI()
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener{finish()}
        storePageViewModel.fetchStorePage(PaperlessUtil.getToken(this), getStoreId().toString())
        storePageViewModel.listenToStore().observe(this, Observer { handleStore(it) })
        storePageViewModel.listenToUIState().observer(this, Observer { handleUIState(it) })
    }

    private fun setupUI(){
        rv_catalog.apply {
            layoutManager = GridLayoutManager(this@StorePageActivity, 2)
            adapter = CatalogAdapter(mutableListOf(), this@StorePageActivity)
        }
    }

    private fun getStoreId() = intent.getIntExtra("store_id", 0)

    private fun handleStore(it: Store){
        supportActionBar?.title = it.name
        if(!it.products.isNullOrEmpty()){
            val categories = it.products.map { product -> product.category!! }.distinctBy {category ->
                category.name
            }.map { c -> c.name }.toMutableList()
            if(!categories.isNullOrEmpty()){
                categories.add(0,resources.getString(R.string.common_all))
                setupSpinner(categories)
            }
        }
    }

    private fun handleUIState(it: StorePageState){
        when(it){
            is StorePageState.IsLoading -> {
                store_spinner.isEnabled = !it.state
                if(it.state){
                    loading.visibility = View.VISIBLE
                }else{
                    loading.visibility = View.GONE
                }
            }
            is StorePageState.ShowToast -> toast(it.message)
        }
    }

    private fun setupSpinner(categories: MutableList<String?>){
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
            val store = storePageViewModel.listenToStore().value
            store?.let { s ->
                val products = s.products
                if(products.isNotEmpty()){
                    val filtered = products.filter { product -> product.category?.name.equals(category) }
                    attachToRecycler(filtered)
                }
            }
        } ?: kotlin.run {
            val store = storePageViewModel.listenToStore().value
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_store_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_info-> {
                showStoreInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showStoreInfo(){
        storePageViewModel.listenToStore().value?.let {
            AlertDialog.Builder(this).apply {
                setMessage(it.name)
                setPositiveButton(resources.getString(R.string.info_understand)){ d, _ -> d.dismiss()}
            }.show()
        }
    }
}