package com.ydhnwb.paperlessapp.ui.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.repositories.CategoryRepository
import com.ydhnwb.paperlessapp.repositories.ProductRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class ProductCreateEditViewModel (private val productRepository: ProductRepository, private val categoryRepository: CategoryRepository) : ViewModel(){
    private val state: SingleLiveEvent<ProductCreateEditState> = SingleLiveEvent()
    private val currentProduct = MutableLiveData<Product>()
    private val categories = MutableLiveData<List<Category>>()

    private fun setLoading(){ state.value = ProductCreateEditState.IsLoading(true) }
    private fun hideLoading(){ state.value = ProductCreateEditState.IsLoading(false) }
    private fun toast(message: String){ state.value = ProductCreateEditState.ShowToast(message) }
    private fun successDelete(){ state.value = ProductCreateEditState.SuccessDelete }
    private fun resetState(){ state.value = ProductCreateEditState.Reset }
    private fun success(isCreate: Boolean){ state.value = ProductCreateEditState.Success(isCreate) }

    fun createProduct(token: String, storeId : String, product: Product, categoryId : Int){
        setLoading()
        productRepository.createProduct(token, storeId, product, categoryId, object: SingleResponse<Product>{
            override fun onSuccess(data: Product?) {
                hideLoading()
                data?.let { success(true) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun fetchCategories(){
        setLoading()
        categoryRepository.getCategories(object: ArrayResponse<Category>{
            override fun onSuccess(datas: List<Category>?) {
                hideLoading()
                datas?.let { categories.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    /*fun updateProduct(token: String, storeId: String, product : Product, categoryId: Int, withImage: Boolean){
        setLoading()
        if(withImage){
            productRepository.updateProductWithImage(token, storeId, product, categoryId, object : SingleResponse<Product>{
                override fun onSuccess(data: Product?) {
                    hideLoading()
                    data?.let { success(false) }
                }
                override fun onFailure(err: Error) {
                    hideLoading()
                    err.message?.let { toast(it) }
                }
            })
        }else{
            productRepository.updateProductOnly(token, storeId, product, categoryId, object : SingleResponse<Product>{
                override fun onSuccess(data: Product?) {
                    hideLoading()
                    data?.let { success(false) }
                }
                override fun onFailure(err: Error) {
                    hideLoading()
                    err.message?.let { toast(it) }
                }
            })
        }

    }*/

    private fun updateProductImage(token: String, storeId: String, product: Product){
        setLoading()
        productRepository.updateImageProductOnly(token, storeId, product, object : SingleResponse<Product>{
            override fun onSuccess(data: Product?) {
                hideLoading()
                data?.let {
                    success(false)
                }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }

        })
    }

    fun updateProduct(token: String, storeId: String, product : Product, categoryId: Int, withImage: Boolean){
        setLoading()
        productRepository.updateProductOnly(token, storeId, product, categoryId, object : SingleResponse<Product>{
            override fun onSuccess(data: Product?) {
                hideLoading()
                data?.let {
                    if(withImage){
                        updateProductImage(token, storeId, product)
                    }else{
                        success(false)
                    }
                }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun deleteProduct(token: String, storeId: String, productId: String){
        setLoading()
        productRepository.deleteProduct(token, storeId, productId, object: SingleResponse<Product>{
            override fun onSuccess(data: Product?) {
                hideLoading()
                data?.let { successDelete() }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }


    fun validate(name : String, desc : String, price: Int?, quantity : Int?, categoryId : Int?, isHaveStock : Boolean) : Boolean {
        resetState()
        if(name.isEmpty()){
            state.value = ProductCreateEditState.Validate(name = "Nama produk tidak boleh kosong")
            return false
        }else if(desc.isEmpty()){
            state.value = ProductCreateEditState.Validate(desc = "Deskripsi tidak boleh kosong")
            return false
        }else if(price == null || price <= 0){
            state.value = ProductCreateEditState.Validate(price = "Harga produk tidak boleh kosong atau nol")
            return false
        }else if(categoryId == null){
            state.value = ProductCreateEditState.Validate(categoryId = "Kategori wajib dipilih terlebih dahulu")
            return false
        }else if(isHaveStock){
            if(quantity == null || quantity <= 0){
                state.value = ProductCreateEditState.Validate(qty = "Isikan quantity terlebih dahulu")
                return false
            }
        }
        return true
    }

    fun listenToUIState() = state
    fun listenToCurrentProduct() = currentProduct
    fun listenToCategories() = categories
}

sealed class ProductCreateEditState {
    data class IsLoading(var state : Boolean) : ProductCreateEditState()
    data class ShowToast(var message : String) : ProductCreateEditState()
    data class Validate(var name: String? = null,
                        var desc : String? = null,
                        var image: String? = null,
                        var price: String? = null,
                        var weight: String? = null,
                        var availableOnline: String? = null,
                        var status : String? = null,
                        var qty: String? = null,
                        var categoryId : String? = null
    ) : ProductCreateEditState()
    object SuccessDelete : ProductCreateEditState()
    object Reset : ProductCreateEditState()
    data class Success(var isCreate: Boolean) : ProductCreateEditState()
}