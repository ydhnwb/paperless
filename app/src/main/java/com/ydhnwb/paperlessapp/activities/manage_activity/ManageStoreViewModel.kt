package com.ydhnwb.paperlessapp.activities.manage_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.repositories.ProductRepository
import com.ydhnwb.paperlessapp.repositories.StoreRepository
import com.ydhnwb.paperlessapp.repositories.UserRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

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
        storeRepository.downloadReport(token, storeId){ url, e ->
            hideLoading()
            e?.let { it.message?.let { m -> toast(m) } }
            url?.let {
                downloadedUrl(it)
            }
        }
    }

    fun setCurrentManagedStore(store: Store) = currentStore.postValue(store)

    fun fetchCurrentUser(token: String){
        userRepository.getCurrentProfile(token){ resultUser, error ->
            error?.let { it.message?.let { m -> toast(m) } }
            resultUser?.let {
                currentUser.postValue(it)
            }
        }
    }

    fun fetchAllProduct(token: String, storeId : String){
        setLoading()
        productRepository.fetchAllProducts(token, storeId){ resultProducts, error ->
            hideLoading()
            error?.let { it.message?.let { m -> toast(m) } }
            resultProducts?.let {
                allProducts.postValue(it)
                hasFetched.value = true
            }
        }
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