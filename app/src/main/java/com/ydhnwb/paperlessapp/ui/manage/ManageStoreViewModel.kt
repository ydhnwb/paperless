package com.ydhnwb.paperlessapp.ui.manage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.repositories.ProductRepository
import com.ydhnwb.paperlessapp.repositories.StoreRepository
import com.ydhnwb.paperlessapp.repositories.UserRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class ManageStoreViewModel (private val storeRepository: StoreRepository,private val userRepository: UserRepository,private val productRepository: ProductRepository) : ViewModel(){
    private val state : SingleLiveEvent<ManageStoreState> = SingleLiveEvent()

    private val allProducts = MutableLiveData<List<Product>>()
    private val selectedProducts = MutableLiveData<List<Product>>()
    private val filteredProducts = MutableLiveData<List<Product>>()

    private val hasFetched = MutableLiveData<Boolean>(false)
    private val currentUser = MutableLiveData<User>()

    private val currentStore = MutableLiveData<Store>()

    private fun setLoading(){ state.value = ManageStoreState.IsLoading(true) }
    private fun hideLoading(){ state.value = ManageStoreState.IsLoading(false) }
    private fun toast(message: String){ state.value = ManageStoreState.ShowToast(message) }
    private fun downloadedUrl(url : String){ state.value = ManageStoreState.DownloadedUrl(url) }

    fun downloadReport(token: String, storeId: String){
        setLoading()
        storeRepository.downloadReport(token, storeId, object : SingleResponse<String>{
            override fun onSuccess(data: String?) {
                hideLoading()
                data?.let { url -> downloadedUrl(url) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun setCurrentManagedStore(store: Store) = currentStore.postValue(store)

    fun fetchCurrentUser(token: String){
        userRepository.getCurrentProfile(token, object: SingleResponse<User>{
            override fun onSuccess(data: User?) { data?.let { currentUser.postValue(it) } }
            override fun onFailure(err: Error) { err.message?.let { toast(it) } }
        })
    }

    fun fetchAllProduct(token: String, storeId : String){
        setLoading()
        productRepository.fetchAllProducts(token, storeId, object :ArrayResponse<Product>{
            override fun onSuccess(datas: List<Product>?) {
                hideLoading()
                datas?.let { allProducts.postValue(it).also { hasFetched.value = true } }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun addSelectedProduct(product: Product){
        val tempSelectedProducts = if(selectedProducts.value == null){
            mutableListOf()
        } else {
            selectedProducts.value as MutableList<Product>
        }
        val sameProduct = tempSelectedProducts.find { p -> p.id == product.id }
        sameProduct?.let {p ->
            p.selectedQuantity = p.selectedQuantity?.plus(1)
        } ?: kotlin.run {
            tempSelectedProducts.add(product)
        }
        selectedProducts.postValue(tempSelectedProducts)
    }

    fun decrementQuantity(product: Product){
        val tempSelectedProducts = if(selectedProducts.value == null){
            mutableListOf()
        } else {
            selectedProducts.value as MutableList<Product>
        }
        val p = tempSelectedProducts.find { it.id == product.id }
        p?.let {
            if(it.selectedQuantity?.minus(1) == 0){
                tempSelectedProducts.remove(it)
            }else{
                it.selectedQuantity = it.selectedQuantity!!.minus(1)
            }
        }
        selectedProducts.postValue(tempSelectedProducts)
    }

    fun incrementQuantity(product: Product){
        val tempSelectedProducts = if(selectedProducts.value == null){
            mutableListOf()
        } else {
            selectedProducts.value as MutableList<Product>
        }
        val p = tempSelectedProducts.find { it.id == product.id }
        p?.let {
            it.selectedQuantity = it.selectedQuantity!!.plus(1)
        }
        selectedProducts.postValue(tempSelectedProducts)
    }

    fun deleteSelectedProduct(p: Product){
        val tempSelectedProducts = if(selectedProducts.value == null){
            mutableListOf()
        } else {
            selectedProducts.value as MutableList<Product>
        }
        val x = tempSelectedProducts.find { it.id == p.id }
        x?.let {
            tempSelectedProducts.remove(it)
        }
        selectedProducts.postValue(tempSelectedProducts)
    }

    fun checkProductByCode(code : String) : Product? {
        val tempProducts = if(allProducts.value == null){ mutableListOf() } else { allProducts.value as MutableList<Product> }
        val p = tempProducts.find { it.code.equals(code) }
        p?.let {
            return it
        } ?: return null
    }

    fun filterByName(query : String){
        allProducts.value?.let {
            val temp = it.filter { product ->
                product.name!!.contains(query, true)
            }
            filteredProducts.postValue(temp.toMutableList())
        }
    }

    fun listenToUIState() = state
    fun listenToAllProducts() = allProducts
    fun listenToSelectedProducts() = selectedProducts
    fun listenToFilteredProducts() = filteredProducts
    fun listenToCurrentStore() = currentStore
    fun listenToCurrentUser() = currentUser
    fun listenToHasFetched() = hasFetched
    fun clearAllSelectedProduct(){ selectedProducts.postValue(mutableListOf()) }
}

sealed class ManageStoreState{
    data class IsLoading(var state : Boolean) : ManageStoreState()
    data class ShowToast(var message : String) : ManageStoreState()
    data class DownloadedUrl(var url : String) : ManageStoreState()
}