package com.ydhnwb.paperlessapp.ui.quickupdate

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showInfoAlert
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.activity_quick_update.*
import kotlinx.android.synthetic.main.content_quick_update.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuickUpdateActivity : AppCompatActivity(), QuickUpdateInterface {
    private val quickUpdateViewModel: QuickUpdateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_update)
        setSupportActionBar(toolbar)
        setupRecyclerView()
        setupToolbar()
        fetchProducts()
        observe()
    }

    private fun fetchProducts() = quickUpdateViewModel.fetchProducts(PaperlessUtil.getToken(this), getPassedStore()?.id.toString())

    private fun observe(){
        observeState()
        observeProducts()
    }

    private fun observeState() = quickUpdateViewModel.listenToState().observer(this, Observer { handleState(it) })
    private fun observeProducts() = quickUpdateViewModel.listenToProducts().observe(this, Observer { handleProducts(it) })

    private fun handleState(state: QuickUpdateState){
        when(state){
            is QuickUpdateState.Alert -> showInfoAlert(state.message)
            is QuickUpdateState.ShowToast -> showToast(state.message)
            is QuickUpdateState.Success -> {
                showToast(resources.getString(R.string.stock_updated))
                finish()
            }
            is QuickUpdateState.Loading -> isLoading(state.isLoading)
        }
    }

    private fun isLoading(b: Boolean) = if(b) loading.visible() else loading.gone()

    private fun handleProducts(products: List<Product>){
        val filtered = products.filter { it.qty != null }
        rv_products.adapter?.let { adapter ->
            if(adapter is QuickUpdateProductAdapter){
                adapter.updateList(filtered)
            }
        }
    }

    private fun setupRecyclerView(){
        rv_products.apply {
            adapter = QuickUpdateProductAdapter(mutableListOf(), this@QuickUpdateActivity)
            layoutManager = LinearLayoutManager(this@QuickUpdateActivity)
        }
    }

    private fun setupToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    override fun click(product: Product) {
        AlertDialog.Builder(this).apply {
            setMessage(resources.getString(R.string.update_stock_of_this_product))
            setPositiveButton(resources.getString(R.string.update_stock)){ d, _ ->
                updateStock(product)
                d.dismiss()
            }
        }.show()
    }

    private fun updateStock(product: Product){
        quickUpdateViewModel.updateProduct(PaperlessUtil.getToken(this), getPassedStore()?.id.toString(), product)
    }

    private fun getPassedStore() = intent.getParcelableExtra<Store>("store")
}