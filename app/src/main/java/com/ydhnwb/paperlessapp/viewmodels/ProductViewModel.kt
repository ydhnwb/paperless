package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class ProductViewModel : ViewModel(){
    private var state : SingleLiveEvent<ProductState> = SingleLiveEvent()
    private var myProducts = MutableLiveData<List<Product>>()

    fun fetchMyProducts(){
        state.value = ProductState.IsLoading(true)
        myProducts.postValue(mutableListOf(
            Product(1, 1, "Shampoo"),
            Product(2, 1, "Lifebuoy"),
            Product(3, 1, "Pepsodent"),
            Product(4, 1, "Kopi ABC")
        ))
        state.value = ProductState.IsLoading(false)
    }

    fun listenToMyProducts() = myProducts
    fun listenToUIState() = state
}


sealed class ProductState{
    data class IsLoading(var state : Boolean) : ProductState()
    data class ShowToast(var message : String) : ProductState()
    object Reset : ProductState()
}