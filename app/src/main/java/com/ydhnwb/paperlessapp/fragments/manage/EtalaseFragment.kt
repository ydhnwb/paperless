package com.ydhnwb.paperlessapp.fragments.manage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.CheckoutActivity
import com.ydhnwb.paperlessapp.activities.ScannerActivity
import com.ydhnwb.paperlessapp.adapters.EtalaseAdapter
import com.ydhnwb.paperlessapp.adapters.SelectedProductAdapter
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.ProductState
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import kotlinx.android.synthetic.main.bottomsheet_detail.view.*
import kotlinx.android.synthetic.main.fragment_etalase.view.*
import kotlinx.android.synthetic.main.fragment_etalase.view.btn_details
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EtalaseFragment : Fragment(R.layout.fragment_etalase) {
    private val productViewModel: ProductViewModel by viewModel()
    private val parentStoreViewModel: StoreViewModel by sharedViewModel()
    private lateinit var bottomSheet: BottomSheetBehavior<*>
    private var filteredProducts = mutableListOf<Product>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUIComponent()
        productViewModel.listenProducts().observe(viewLifecycleOwner, Observer {
            if(it.isNullOrEmpty()){
                view.spinner_category.visibility = View.GONE
                view.empty_view.visibility = View.VISIBLE
            }else{
                view.spinner_category.visibility = View.VISIBLE
                view.empty_view.visibility = View.GONE
                val filteredCategories = it.map { p -> p.category }.distinctBy { c -> c?.name }.map { f -> f?.name!! }.toMutableList()
                filteredCategories.add(0, resources.getString(R.string.info_all))
                val spinnerAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, filteredCategories)
                view.spinner_category.adapter = spinnerAdapter
                view.spinner_category.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                        if (position == 0){
                            filteredProducts.clear()
                            filteredProducts.addAll(it)
                        }else{
                            val temp = it.filter { p -> p.category!!.name!!.equals(view.spinner_category.getItemAtPosition(position)) }
                            filteredProducts = temp.toMutableList()
                        }
                    }

                }

            }
            view.rv_etalase.adapter?.let { adapter ->
                if(adapter is EtalaseAdapter){
//                    adapter.updateList(it)
                    adapter.updateList(filteredProducts)
                }
            }
        })
        if(productViewModel.listenProducts().value == null || productViewModel.listenProducts().value!!.isEmpty()){
            view.empty_view.visibility = View.VISIBLE
            view.spinner_category.visibility = View.GONE
        }else{
            view.empty_view.visibility = View.GONE
            view.spinner_category.visibility = View.VISIBLE
        }
        productViewModel.listenToUIState().observe(viewLifecycleOwner, Observer { handleUIState(it) })
        productViewModel.listenSelectedProducts().observe(viewLifecycleOwner, Observer { handleSelectedProduct(it) })
        productViewModel.fetchAllProducts(PaperlessUtil.getToken(activity!!), parentStoreViewModel.getCurrentStore()?.id.toString())
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
            if(selectedProducts != null && selectedProducts.isNotEmpty()){
                startActivity(Intent(activity!!, CheckoutActivity::class.java).apply {
                    putParcelableArrayListExtra("PRODUCT", selectedProducts)
                })
            }else{
                toast("Pilih produk terlebih dahulu")
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
                view!!.spinner_category.isEnabled = !it.state
                if(it.state){
                    view?.loading?.visibility = View.VISIBLE
                }else{
                    view?.loading?.visibility = View.GONE
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
}