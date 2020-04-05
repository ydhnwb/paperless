package com.ydhnwb.paperlessapp.fragments.manage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.ProductActivity
import com.ydhnwb.paperlessapp.adapters.DetailedProductAdapter
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.ProductState
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import kotlinx.android.synthetic.main.fragment_product.view.*

class ProductFragment : Fragment(R.layout.fragment_product) {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var parentStoreViewModel: StoreViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        parentStoreViewModel = ViewModelProvider(activity!!).get(StoreViewModel::class.java)
        view.rv_manage_product.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = DetailedProductAdapter(mutableListOf(), activity!!, parentStoreViewModel.getCurrentStore()!!)
        }
        view.fab_add.setOnClickListener {
            startActivity(Intent(activity, ProductActivity::class.java).apply {
                putExtra("STORE", parentStoreViewModel.getCurrentStore())
            })
        }
        productViewModel.listenToUIState().observer(viewLifecycleOwner, Observer {
            when(it){
                is ProductState.ShowToast -> toast(it.message)
                is ProductState.IsLoading -> {
                    if(it.state){ view.loading.visibility = View.VISIBLE }else{ view.loading.visibility = View.GONE }
                }
            }
        })
        productViewModel.listenProducts().observe(viewLifecycleOwner, Observer {
            view.rv_manage_product.adapter?.let { i ->
                if(i is DetailedProductAdapter){
                    i.updateList(it)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        productViewModel.fetchAllProducts(PaperlessUtil.getToken(activity!!), parentStoreViewModel.getCurrentStore()!!.id.toString())
    }
    private fun toast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

}