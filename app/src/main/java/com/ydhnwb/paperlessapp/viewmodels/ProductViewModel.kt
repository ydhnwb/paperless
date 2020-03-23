package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.webservices.ApiClient
import java.lang.Exception

class ProductViewModel : ViewModel(){
    private var state : SingleLiveEvent<ProductState> = SingleLiveEvent()
    private var products = MutableLiveData<List<Product>>()
    private var api = ApiClient.instance()
    private var selectedProducts = MutableLiveData<List<Product>>()



    fun fetchProducts(token: String){
        try {
            state.value = ProductState.IsLoading(false)
            val dummyProducts = mutableListOf<Product>().apply {
                add(Product(1,"Latte","https://cdn02.indozone.id/re/content/2019/10/07/ers0M9/t_5d9ae209ae934.jpg?w=700&q=85", 15000, null, false, null))
                add(Product(2,"Americano","https://cdn02.indozone.id/re/content/2019/10/07/ers0M9/t_5d9ae209ae934.jpg?w=700&q=85", 15000, null, false, null))
                add(Product(3,"Coffee Toraja","https://cdn02.indozone.id/re/content/2019/10/07/ers0M9/t_5d9ae209ae934.jpg?w=700&q=85", 15000, null, false, null))
                add(Product(4,"Mocca","https://cdn02.indozone.id/re/content/2019/10/07/ers0M9/t_5d9ae209ae934.jpg?w=700&q=85", 15000, null, false, null))
                add(Product(5,"Cappuchino","https://cdn02.indozone.id/re/content/2019/10/07/ers0M9/t_5d9ae209ae934.jpg?w=700&q=85", 15000, null, false, null))
            }
            products.postValue(dummyProducts)
            state.value = ProductState.IsLoading(false)
        }catch (e: Exception){
            println(e.message)
            state.value = ProductState.IsLoading(false)
            state.value = ProductState.ShowToast(e.message.toString())
        }
    }

    fun validate(product: Product) : Boolean {
        state.value = ProductState.Reset
        if(product.name.isNullOrEmpty()){
            state.value = ProductState.Validate(name = "Nama produk tidak boleh kosong")
            return false
        }else if(product.price == null || product.price == 0){
            state.value = ProductState.Validate(price = "Harga produk tidak boleh kosong atau nol")
            return false
        }else if(product.categoryId == null){
            state.value = ProductState.Validate(categoryId = "Kategori wajib dipilih terlebih dahulu")
            return false
        }else if(product.qty != null){
            state.value = ProductState.Validate(qty = "Isikan quantity terlebih dahulu")
            return false
        }else if(product.weight != null || product.weight != 0.0F){
            state.value = ProductState.Validate(weight = "Berat tak boleh kosong")
            return false
        }
        return true
    }

    fun addProduct(product: Product){
        val tempSelectedProducts = if(selectedProducts.value == null){
            mutableListOf()
        } else {
            selectedProducts.value as MutableList<Product>
        }
        tempSelectedProducts.add(product)
        selectedProducts.postValue(tempSelectedProducts)
    }

    fun listenSelectedProducts() = selectedProducts
    fun listenToUIState() = state
    fun listenProducts() = products
}


sealed class ProductState{
    data class Validate(var name: String? = null, var image: String? = null, var price: String? = null,
                        var weight: String? = null, var availableOnline: String? = null, var status : String? = null,
                        var qty: String? = null, var categoryId : String? = null) : ProductState()
    data class IsLoading(var state : Boolean) : ProductState()
    data class ShowToast(var message : String) : ProductState()
    object Reset : ProductState()
}