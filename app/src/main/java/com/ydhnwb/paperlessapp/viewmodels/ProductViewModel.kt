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

    fun listenToUIState() = state
    fun listenProducts() = products
}


sealed class ProductState{
    data class IsLoading(var state : Boolean) : ProductState()
    data class ShowToast(var message : String) : ProductState()
    object Reset : ProductState()
}