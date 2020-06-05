package com.ydhnwb.paperlessapp.fragments.manage.product_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.repositories.ProductRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class ProductViewModel (private val productRepository: ProductRepository) : ViewModel(){
    private val state: SingleLiveEvent<ProductState> = SingleLiveEvent()
    private val products = MutableLiveData<List<Product>>()

    private fun setLoading(){ state.value = ProductState.IsLoading(true) }
    private fun hideLoading(){ state.value = ProductState.IsLoading(false) }
    private fun toast(m: String){ state.value = ProductState.ShowToast(m) }

    fun fetchProducts(token: String, storeId: String){
        setLoading()
        productRepository.fetchAllProducts(token, storeId){ resultProducts, error ->
            hideLoading()
            error?.let { it.message?.let { m -> toast(m) } }
            resultProducts?.let { products.postValue(it) }
        }
    }

    fun listenToUIState() = state
    fun listenToProducts() = products
}

sealed class ProductState {
    data class IsLoading(var state: Boolean) : ProductState()
    data class ShowToast(var message: String) : ProductState()
}