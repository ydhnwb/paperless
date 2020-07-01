package com.ydhnwb.paperlessapp.ui.manage.etalase

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
import com.ydhnwb.paperlessapp.ui.checkout.CheckoutActivity
import com.ydhnwb.paperlessapp.ui.scanner.ScannerActivity
import com.ydhnwb.paperlessapp.ui.manage.ManageStoreState
import com.ydhnwb.paperlessapp.ui.manage.ManageStoreViewModel
import com.ydhnwb.paperlessapp.ui.manage.bookmenu.BookmenuFragment
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.CustomFragmentPagerAdapter
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.showInfoAlert
import kotlinx.android.synthetic.main.bottomsheet_detail.view.*
import kotlinx.android.synthetic.main.fragment_etalase.view.*
import kotlinx.android.synthetic.main.fragment_etalase.view.btn_details
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class EtalaseFragment : Fragment(R.layout.fragment_etalase) {
    companion object {
        private const val REQ_CHECKOUT = 12
    }
    private lateinit var bottomSheet: BottomSheetBehavior<*>
    private val parentViewModel: ManageStoreViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUIComponent()
        parentViewModel.listenToAllProducts().observe(viewLifecycleOwner, Observer { handleProducts(it) })
        if(!parentViewModel.listenToHasFetched().value!!){
            PaperlessUtil.getToken(requireActivity())?.let { parentViewModel.fetchAllProduct(it, parentViewModel.listenToCurrentStore().value?.id.toString()) }
        }
        if(parentViewModel.listenToAllProducts().value == null || parentViewModel.listenToAllProducts().value!!.isEmpty()){
            view.empty_view.visibility = View.VISIBLE
        }else{
            view.empty_view.visibility = View.GONE
        }
        parentViewModel.clearAllSelectedProduct()
        parentViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
        parentViewModel.listenToSelectedProducts().observe(viewLifecycleOwner, Observer { handleSelectedProduct(it) })
    }

    private fun setupUIComponent(){
        view!!.rv_detail_order.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter =
                SelectedProductAdapter(
                    mutableListOf(),
                    activity!!,
                    parentViewModel
                )
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
            val selectedProducts : ArrayList<Product>? = parentViewModel.listenToSelectedProducts().value as ArrayList<Product>?
            if(selectedProducts != null && selectedProducts.isNotEmpty()){
                startActivityForResult(Intent(activity!!, CheckoutActivity::class.java).apply {
                    putParcelableArrayListExtra("PRODUCT", selectedProducts)
                    putExtra("STORE", parentViewModel.listenToCurrentStore().value)
                }, REQ_CHECKOUT)
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
        }else if(requestCode == REQ_CHECKOUT && resultCode == Activity.RESULT_OK){
            parentViewModel.clearAllSelectedProduct()
            PaperlessUtil.getToken(requireActivity())?.let { parentViewModel.fetchAllProduct(it, parentViewModel.listenToCurrentStore().value?.id.toString()) }
        }
    }

    private fun checkProductByCode(code: String?){
        code?.let {
            val product : Product? = parentViewModel.checkProductByCode(it)
            product?.let {
                val p = product.copy()
                p.selectedQuantity = 1
                parentViewModel.addSelectedProduct(p)
                toast("${p.name} ditambahkan ke keranjang")
            } ?: kotlin.run {
                toast("Produk dengan code $it tidak ditemukan")
            }
        }
    }

    private fun handleUIState(it: ManageStoreState){
        when(it){
            is ManageStoreState.ShowToast -> toast(it.message)
            is ManageStoreState.IsLoading -> {
                if(it.state){
                    view!!.loading!!.visibility = View.GONE
                }else{
                    view!!.loading!!.visibility = View.GONE
                }
            }
            is ManageStoreState.Alert -> requireActivity().showInfoAlert(it.message)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSelectedProduct(it: List<Product>){
        with(view!!){
            rv_detail_order.adapter?.let { a->
                if(a is SelectedProductAdapter){ a.updateList(it) }
                val totalQuantity = it.sumBy { product -> product.selectedQuantity!! }
                val totalPrice = if(it.isEmpty()){ 0 }else{ it.sumBy { product ->
                    val temp = product.price!! * product.selectedQuantity!!
                    if(product.discountByPercent != null){
                        temp - (temp * product.discountByPercent!! / 100).toInt()
                    }else{
                        temp
                    }
                } }
                tv_item_indicator.text = "Tagih ($totalQuantity items)"
                tv_total_price.text = PaperlessUtil.setToIDR(totalPrice)
            }
        }
    }

    private fun handleProducts(it : List<Product>){
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

    private fun toast(message : String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

}