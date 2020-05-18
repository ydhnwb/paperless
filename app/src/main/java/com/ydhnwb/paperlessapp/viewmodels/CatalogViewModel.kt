package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatalogViewModel (private val api : ApiService) : ViewModel(){
    private val state : SingleLiveEvent<CatalogState> = SingleLiveEvent()
    private val products = MutableLiveData<List<Product>>().apply {
        postValue(mutableListOf())
    }
    private val hasFetched = MutableLiveData<Boolean>().apply {
        value = false
    }

    private fun setLoading() { state.value = CatalogState.IsLoading(true) }
    private fun hideLoading() { state.value = CatalogState.IsLoading(false) }
    private fun toast(message: String) {  state.value = CatalogState.ShowToast(message) }

    fun search(token: String, query : String){
        setLoading()
        hasFetched.value = true
        api.catalog_search(token, query).enqueue(object : Callback<WrappedListResponse<Product>>{
            override fun onFailure(call: Call<WrappedListResponse<Product>>, t: Throwable) {
                println(t.message)
                toast(t.message.toString())
                hideLoading()
            }

            override fun onResponse(call: Call<WrappedListResponse<Product>>, response: Response<WrappedListResponse<Product>>) {
                if(response.isSuccessful){
                    val body = response.body()
                    body?.let {
                        products.postValue(it.data)
                    }
                }else{
                    toast("Tidak dapat mencari katalog. (${response.code()})")
                }
                hideLoading()
            }
        })
    }

    fun listenToState() = state
    fun listenToProducts() = products
    fun listenToHasFetched() = hasFetched
}

sealed class CatalogState {
    data class IsLoading(var state : Boolean) : CatalogState()
    data class ShowToast(var message : String) : CatalogState()
}