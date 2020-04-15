package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiClient
import com.ydhnwb.paperlessapp.webservices.ApiService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception

class ProductViewModel(private val api : ApiService) : ViewModel(){
    private var state : SingleLiveEvent<ProductState> = SingleLiveEvent()
    private var products = MutableLiveData<List<Product>>()
    private var selectedProducts = MutableLiveData<List<Product>>()

    fun fetchAllProducts(token: String, storeId: String){
        state.value = ProductState.IsLoading(true)
        api.product_get(token, storeId).enqueue(object : Callback<WrappedListResponse<Product>>{
            override fun onFailure(call: Call<WrappedListResponse<Product>>, t: Throwable) {
                println(t.message)
                state.value = ProductState.IsLoading(false)
                state.value = ProductState.ShowToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedListResponse<Product>>, response: Response<WrappedListResponse<Product>>) {
                if (response.isSuccessful){
                    val body = response.body()
                    body?.let {
                        if (it.status){
                            products.postValue(it.data)
                        }
                    }
                }else{ state.value = ProductState.ShowToast("Tidak dapat mengambil data produk") }
                state.value = ProductState.IsLoading(false)
            }
        })
    }

    fun validate(name : String, desc : String, price: Int?, quantity : Int?, isAvailableOnline : Boolean ,weight: Double?, categoryId : Int?, isHaveStock : Boolean) : Boolean {
        state.value = ProductState.Reset
        if(name.isEmpty()){
            state.value = ProductState.Validate(name = "Nama produk tidak boleh kosong")
            return false
        }else if(desc.isEmpty()){
            state.value = ProductState.Validate(desc = "Deskripsi tidak boleh kosong")
            return false
        }else if(price == null || price <= 0){
            state.value = ProductState.Validate(price = "Harga produk tidak boleh kosong atau nol")
            return false
        }else if(categoryId == null){
            state.value = ProductState.Validate(categoryId = "Kategori wajib dipilih terlebih dahulu")
            return false
        }else if(isHaveStock){
            if(quantity == null || quantity <= 0){
                state.value = ProductState.Validate(qty = "Isikan quantity terlebih dahulu")
                return false
            }
        }else if(isAvailableOnline && ( weight == null || weight < 1)){
            state.value = ProductState.Validate(weight = "Berat tak boleh kosong")
            return false
        }
        return true
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

    fun createProduct(token: String, storeId : String, product: Product, categoryId : Int){
        state.value = ProductState.IsLoading(true)
        println(storeId)
        println(product)
        println(categoryId)
        val file = File(product.image.toString())
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, requestBodyForFile)
        api.product_store(token, storeId, product.name.toString(), product.description.toString() ,product.code, product.price!!,
            categoryId, product.availableOnline, product.weight, true, product.qty, image)
            .enqueue(object : Callback<WrappedResponse<Product>>{
                override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) {
                    println(t.message)
                    state.value = ProductState.ShowToast(t.message.toString())
                    state.value = ProductState.IsLoading(false)
                }

                override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                    if(response.isSuccessful){
                        val body = response.body()
                        body?.let {
                            if(it.status){
                                state.value = ProductState.ShowToast("Success create product")
                                state.value = ProductState.Success
                            }else{
                                state.value = ProductState.ShowPopup("Gagal saat membuat produk")
                            }
                        }
                    }else{
                        state.value = ProductState.ShowPopup("Terjadi kesalahan. Tidak dapat membuat produk")
                        println(response.body())
                        println(response.code())
                    }
                    state.value = ProductState.IsLoading(false)
                }
            })
    }

    fun updateProduct(token: String, storeId: String, product : Product, categoryId: Int, isUpdateImage : Boolean){
        state.value = ProductState.IsLoading(true)
        println(product)
        if(isUpdateImage){
            val file = File(product.image.toString())
            val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
            val image = MultipartBody.Part.createFormData("image", file.name, requestBodyForFile)
            api.product_update(token, storeId, product.id.toString(), product.name.toString(), product.description.toString(),
                product.code, product.price!!, categoryId, product.availableOnline, product.weight, true, product.qty, image)
                .enqueue(object : Callback<WrappedResponse<Product>>{
                    override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) {
                        println(t.message)
                        state.value = ProductState.IsLoading(false)
                        state.value = ProductState.ShowToast(t.message.toString())
                    }

                    override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                        if(response.isSuccessful){
                            val b = response.body()
                            b?.let {
                                if(it.status) state.value = ProductState.Success else state.value = ProductState.ShowPopup("Tidak dapat mengupdate produk")
                            }
                        }else{
                            state.value = ProductState.ShowPopup("Terjadi kesalahan saat mengupdate produk")
                        }
                        state.value = ProductState.IsLoading(false)
                    }
                })
        }else{
            api.product_update(token,
                storeId,
                product.id.toString(),
                product.name.toString(),
                product.description.toString(),
                product.code,
                product.price!!,
                categoryId,
                product.availableOnline,
                product.weight,
                true,
                product.qty)
                .enqueue(object : Callback<WrappedResponse<Product>>{
                    override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) {
                        println(t.message)
                        state.value = ProductState.IsLoading(false)
                        state.value = ProductState.ShowToast(t.message.toString())
                    }

                    override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                        println(response)
                        if(response.isSuccessful){
                            val b = response.body()
                            b?.let {
                                if(it.status) state.value = ProductState.Success else state.value = ProductState.ShowPopup("Tidak dapat mengupdate produk")
                            }
                            println(response.body())
                            println(response.code())
                        }else{
                            state.value = ProductState.ShowPopup("Terjadi kesalahan saat mengupdate produk")
                            println(response.body())
                            println(response.code())
                        }
                        state.value = ProductState.IsLoading(false)
                    }
                })
        }
    }

    fun delete(token : String, storeId: String, productId : String){
        state.value = ProductState.IsLoading(true)
        api.product_delete(token, storeId, productId).enqueue(object : Callback<WrappedResponse<Product>>{
            override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) {
                println(t.message)
                state.value = ProductState.IsLoading(false)
                state.value = ProductState.ShowToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if (it.status){
                            state.value = ProductState.Success
                        }else{
                            state.value = ProductState.ShowPopup("Tidak dapat menghapus")
                        }
                    }
                }else{
                    state.value = ProductState.ShowPopup("Tidak dapat menghapus")
                }
                state.value = ProductState.IsLoading(false)
            }

        })
    }

    fun checkProductByCode(code : String) : Product? {
        val tempProducts = if(products.value == null){ mutableListOf() } else { products.value as MutableList<Product> }
        val p = tempProducts.find { it.code.equals(code) }
        p?.let {
            return it
        } ?: kotlin.run {
            return null
        }
    }

    fun listenSelectedProducts() = selectedProducts
    fun listenToUIState() = state
    fun listenProducts() = products
}


sealed class ProductState{
    data class Validate(var name: String? = null, var desc : String? = null,var image: String? = null, var price: String? = null,
                        var weight: String? = null, var availableOnline: String? = null, var status : String? = null,
                        var qty: String? = null, var categoryId : String? = null) : ProductState()
    data class IsLoading(var state : Boolean) : ProductState()
    data class ShowToast(var message : String) : ProductState()
    data class ShowPopup(var message : String) : ProductState()
    object Success : ProductState()
    object Reset : ProductState()
}