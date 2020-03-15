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
            Product(1, 1, "Latte", "https://www.caffesociety.co.uk/assets/recipe-images/latte-small.jpg"),
            Product(2, 1, "Americano", "https://miro.medium.com/max/400/1*Kz7w2a8MgzuHu1hiJZ8SzQ.jpeg"),
            Product(3, 1, "Mocca", "https://www.bbcgoodfood.com/sites/default/files/styles/recipe/public/recipe/recipe-image/2018/03/mocha-001.jpg"),
            Product(4, 1, "Delhi Ice Coffee", "https://www.chewoutloud.com/wp-content/uploads/2018/02/thai-iced-coffee-1.jpg")
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