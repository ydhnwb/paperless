package com.ydhnwb.paperlessapp.ui.manage.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.repositories.ProductRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class ProductViewModel (private val productRepository: ProductRepository) : ViewModel(){
    private val state: SingleLiveEvent<ProductState> = SingleLiveEvent()
    private val products = MutableLiveData<List<Product>>()

    private fun setLoading(){ state.value = ProductState.IsLoading(true) }
    private fun hideLoading(){ state.value = ProductState.IsLoading(false) }
    private fun toast(m: String){ state.value = ProductState.ShowToast(m) }

    fun fetchProducts(token: String, storeId: String){
        setLoading()
        productRepository.fetchAllProducts(token, storeId, object : ArrayResponse<Product>{
            override fun onSuccess(datas: List<Product>?) {
                hideLoading()
                datas?.let { products.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun changeAvailibilityOfProduct(token: String, storeId: String, product : Product){
        setLoading()
        product.status = !product.status!!
        productRepository.updateProductOnly(token, storeId, product, product.category!!.id!!, object :
            SingleResponse<Product> {
            override fun onSuccess(data: Product?) {
                hideLoading()
                data?.let {
                    fetchProducts(token, storeId)
                }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun listenToUIState() = state
    fun listenToProducts() = products
}

sealed class ProductState {
    data class IsLoading(var state: Boolean) : ProductState()
    data class ShowToast(var message: String) : ProductState()
}