package com.ydhnwb.paperlessapp.fragments.manage

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.CheckoutActivity
import com.ydhnwb.paperlessapp.adapters.EtalaseAdapter
import com.ydhnwb.paperlessapp.adapters.SelectedProductAdapter
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.ProductState
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.bottomsheet_detail.view.*
import kotlinx.android.synthetic.main.fragment_etalase.view.*
import kotlinx.android.synthetic.main.fragment_etalase.view.btn_details

class EtalaseFragment : Fragment(R.layout.fragment_etalase) {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var bottomSheet: BottomSheetBehavior<*>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        setupUIComponent()
        productViewModel.listenProducts().observe(viewLifecycleOwner, Observer {
            view.rv_etalase.adapter?.let { adapter ->
                if(adapter is EtalaseAdapter){
                    adapter.updateList(it)
                }
            }
        })
        productViewModel.listenToUIState().observe(viewLifecycleOwner, Observer {
            when(it){
                is ProductState.ShowToast -> toast(it.message)
                is ProductState.IsLoading -> {
                    if(it.state){ view.loading.visibility = View.VISIBLE }else{ view.loading.visibility = View.GONE }
                }
            }
        })

        productViewModel.listenSelectedProducts().observe(viewLifecycleOwner, Observer {
            view.rv_detail_order.adapter?.let {a->
                if(a is SelectedProductAdapter){ a.updateList(it) }
                val totalQuantity = it.sumBy { product -> product.selectedQuantity!! }
                val totalPrice = if(it.isEmpty()){ 0 }else{ it.sumBy { product ->
                        product.price!! * product.selectedQuantity!!
                    }
                }
                view.tv_item_indicator.text = "Tagih ($totalQuantity items)"
                view.tv_total_price.text = PaperlessUtil.setToIDR(totalPrice)
            }
        })
        productViewModel.fetchProducts(PaperlessUtil.getToken(activity!!))
    }

    private fun toast(message : String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    private fun setupUIComponent(){
        view!!.rv_etalase.apply {
            layoutManager = if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                GridLayoutManager(activity, 2)
            }else{
                GridLayoutManager(activity, 4)
            }
            adapter = EtalaseAdapter(mutableListOf(), activity!!, productViewModel)
        }
        view!!.rv_detail_order.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SelectedProductAdapter(mutableListOf(), activity!!, productViewModel)
        }
        bottomSheet = BottomSheetBehavior.from(view!!.bottomsheet_detail_order)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        view!!.btn_details.setOnClickListener {
            if(bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheet.state == BottomSheetBehavior.STATE_HIDDEN){
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }else{
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        view!!.btn_checkout.setOnClickListener {
            val selectedProducts : ArrayList<Product>? = productViewModel.listenSelectedProducts().value as ArrayList<Product>?
            if(selectedProducts != null){
                startActivity(Intent(activity!!, CheckoutActivity::class.java).apply {
                    putParcelableArrayListExtra("PRODUCT", selectedProducts)
                })
            }else{
                toast("Pilih produk terlebih dahulu")
            }

        }
    }
}