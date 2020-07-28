package com.ydhnwb.paperlessapp.ui.catalog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.GeneralProductSearch
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.repositories.ProductRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class CatalogViewModel (private val productRepository: ProductRepository) : ViewModel() {
    private val products = MutableLiveData<List<Product>>(mutableListOf())
    private val state : SingleLiveEvent<CatalogState> = SingleLiveEvent()

    private fun setLoading(){ state.value = CatalogState.IsLoading(true) }
    private fun hideLoading(){ state.value = CatalogState.IsLoading(false) }
    private fun toast(message: String){ state.value = CatalogState.ShowToast(message) }

    fun searchCatalog(token: String, q: String){
        setLoading()
        productRepository.searchProductCatalog(token, q, object: SingleResponse<GeneralProductSearch> {
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
            override fun onSuccess(data: GeneralProductSearch?) {
                hideLoading()
                data?.let {
                    it.allProducts?.let {  x ->
                        products.postValue(x)
                    } ?: kotlin.run {
                        products.postValue(mutableListOf())
                    }
                }
            }
        })
    }

    fun listenToUIState() = state
    fun listenToCatalogs() = products
}
sealed class CatalogState {
    data class IsLoading(var state : Boolean) : CatalogState()
    data class ShowToast(var message : String) : CatalogState()
}