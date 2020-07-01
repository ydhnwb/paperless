package com.ydhnwb.paperlessapp.ui.quickupdate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.repositories.ProductRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class QuickUpdateViewModel (private val productRepository: ProductRepository) : ViewModel(){
    private val state: SingleLiveEvent<QuickUpdateState> = SingleLiveEvent()
    private val products = MutableLiveData<List<Product>>()

    private fun setLoading(){
        state.value = QuickUpdateState.Loading(true)
    }
    private fun hideLoading(){
        state.value = QuickUpdateState.Loading(false)
    }
    private fun success(){
        state.value = QuickUpdateState.Success
    }
    private fun toast(message: String){
        state.value = QuickUpdateState.ShowToast(message)
    }
    private fun alert(message: String){
        state.value = QuickUpdateState.Alert(message)
    }

    fun fetchProducts(token: String, storeId: String){
        setLoading()
        productRepository.fetchAllProducts(token, storeId, object: ArrayResponse<Product>{
            override fun onSuccess(datas: List<Product>?) {
                hideLoading()
                datas?.let { products.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { alert(it) }
            }
        })
    }

    fun updateProduct(token: String,storeId: String, product: Product){
        setLoading()
        productRepository.updateProductOnly(token, storeId, product, product.category!!.id!!, object: SingleResponse<Product>{
            override fun onSuccess(data: Product?) {
                hideLoading()
                data?.let { success() }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { alert(it) }
            }
        })
    }

    fun listenToState() = state
    fun listenToProducts() = products
}

sealed class QuickUpdateState {
    data class Alert(val message: String) : QuickUpdateState()
    data class Loading(val isLoading: Boolean) : QuickUpdateState()
    object Success : QuickUpdateState()
    data class ShowToast(val message: String) : QuickUpdateState()
}