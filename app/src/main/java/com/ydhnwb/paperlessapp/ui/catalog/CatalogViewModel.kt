package com.ydhnwb.paperlessapp.ui.catalog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.repositories.ProductRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class CatalogViewModel (private val productRepository: ProductRepository) : ViewModel() {
    private val products = MutableLiveData<List<Product>>()
    private val state : SingleLiveEvent<CatalogState> = SingleLiveEvent()

    private fun setLoading(){ state.value = CatalogState.IsLoading(true) }
    private fun hideLoading(){ state.value = CatalogState.IsLoading(false) }
    private fun toast(message: String){ state.value = CatalogState.ShowToast(message) }

    fun searchCatalog(token: String, q: String){
        setLoading()
        productRepository.searchProductCatalog(token, q){ resultProducts, e ->
            hideLoading()
            e?.let { it.message?.let { message -> toast(message) } }
            resultProducts?.let { products.postValue(it) }
        }
    }

    fun listenToUIState() = state
    fun listenToCatalogs() = products

}
sealed class CatalogState {
    data class IsLoading(var state : Boolean) : CatalogState()
    data class ShowToast(var message : String) : CatalogState()
}