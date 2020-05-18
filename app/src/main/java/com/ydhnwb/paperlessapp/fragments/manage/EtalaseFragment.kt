package com.ydhnwb.paperlessapp.fragments.manage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.CheckoutActivity
import com.ydhnwb.paperlessapp.activities.ScannerActivity
import com.ydhnwb.paperlessapp.adapters.SelectedProductAdapter
import com.ydhnwb.paperlessapp.fragments.BookmenuFragment
import com.ydhnwb.paperlessapp.fragments.SearchEtalaseFragment
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.CustomFragmentPagerAdapter
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.ProductState
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import kotlinx.android.synthetic.main.bottomsheet_detail.view.*
import kotlinx.android.synthetic.main.fragment_etalase.view.*
import kotlinx.android.synthetic.main.fragment_etalase.view.btn_details
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class EtalaseFragment : Fragment(R.layout.fragment_etalase) {
    private lateinit var bottomSheet: BottomSheetBehavior<*>
    private val parentStoreViewModel: StoreViewModel by sharedViewModel()
    private val productViewModel: ProductViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUIComponent()
        productViewModel.listenProducts().observe(viewLifecycleOwner, Observer { handleProducts(it) })
        if(productViewModel.listenProducts().value == null || productViewModel.listenProducts().value!!.isEmpty()){ view.empty_view.visibility = View.VISIBLE }else{ view.empty_view.visibility = View.GONE }
        productViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
        productViewModel.listenSelectedProducts().observe(viewLifecycleOwner, Observer { handleSelectedProduct(it) })
        if(savedInstanceState != null){
            if(!productViewModel.listenToHasFetched().value!!){
                productViewModel.clearAllSelectedProduct()
                productViewModel.fetchAllProducts(PaperlessUtil.getToken(activity!!), parentStoreViewModel.getCurrentStore()?.id.toString())
            }
        }else{
            productViewModel.clearAllSelectedProduct()
            productViewModel.fetchAllProducts(PaperlessUtil.getToken(activity!!), parentStoreViewModel.getCurrentStore()?.id.toString())
        }

    }

    private fun setupUIComponent(){
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
            if(selectedProducts != null && selectedProducts.isNotEmpty()){
                startActivity(Intent(activity!!, CheckoutActivity::class.java).apply {
                    putParcelableArrayListExtra("PRODUCT", selectedProducts)
                    putExtra("STORE", parentStoreViewModel.getCurrentStore())
                })
            }else{
                toast(resources.getString(R.string.info_choose_product_first))
            }
        }
        view!!.btn_scan.setOnClickListener { startActivityForResult(Intent(activity, ScannerActivity::class.java), 0) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null ){
            checkProductByCode(data.getStringExtra("CODE"))
        }
    }

    private fun checkProductByCode(code: String?){
        code?.let {
            val product : Product? = productViewModel.checkProductByCode(it)
            product?.let {
                val p = product.copy()
                p.selectedQuantity = 1
                productViewModel.addSelectedProduct(p)
                toast("${p.name} ditambahkan ke keranjang")
            } ?: kotlin.run {
                toast("Produk dengan code $it tidak ditemukan")
            }
        }
    }

    private fun handleUIState(it: ProductState){
        when(it){
            is ProductState.ShowToast -> toast(it.message)
            is ProductState.IsLoading -> {
                if(it.state){
                    view!!.loading!!.visibility = View.VISIBLE
                }else{
                    view!!.loading!!.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSelectedProduct(it: List<Product>){
        with(view!!){
            rv_detail_order.adapter?.let { a->
                if(a is SelectedProductAdapter){ a.updateList(it) }
                val totalQuantity = it.sumBy { product -> product.selectedQuantity!! }
                val totalPrice = if(it.isEmpty()){ 0 }else{ it.sumBy { product -> product.price!! * product.selectedQuantity!! } }
                tv_item_indicator.text = "Tagih ($totalQuantity items)"
                tv_total_price.text = PaperlessUtil.setToIDR(totalPrice)
            }
        }
    }

    private fun handleProducts(it : List<Product>){
        if (productViewModel.listenToHasFetched().value!!){
            if(it.isNullOrEmpty()){
                view!!.empty_view.visibility = View.VISIBLE
            }else{
                val filteredCategories = it.map { product -> product.category!! }.distinctBy { category -> category.name }
                val fragmentAdapter = CustomFragmentPagerAdapter(childFragmentManager)
                for (c in filteredCategories){
                    fragmentAdapter.addFragment(BookmenuFragment.instance(c), c.name!!)
                }
                if (!it.isNullOrEmpty()){ fragmentAdapter.addFragment(SearchEtalaseFragment(), resources.getString(R.string.info_search_result)) }
                view!!.empty_view.visibility = View.GONE
                view!!.viewpager.adapter = fragmentAdapter
                view!!.tabs.setupWithViewPager(view!!.viewpager)
            }
        }
    }

    private fun toast(message : String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}