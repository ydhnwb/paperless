package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.webservices.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class CategoryViewModel : ViewModel(){
    private var api = ApiClient.instance()
    private var state : SingleLiveEvent<CategoryState> = SingleLiveEvent()
    private var categories = MutableLiveData<List<Category>>()


    fun fetchCategory(){
        try{
            state.value = CategoryState.IsLoading(true)
            api.category_get().enqueue(object: Callback<WrappedListResponse<Category>>{
                override fun onFailure(call: Call<WrappedListResponse<Category>>, t: Throwable) {
                    println(t.message)
                    state.value = CategoryState.ShowToast(t.message.toString())
                    state.value = CategoryState.IsLoading(false)
                }

                override fun onResponse(call: Call<WrappedListResponse<Category>>, response: Response<WrappedListResponse<Category>>) {
                    if(response.isSuccessful){
                        val b = response.body() as WrappedListResponse<Category>
                        if (b.status){
                            val cats = b.data
                            categories.postValue(cats)
                        }else{
                            state.value = CategoryState.ShowToast("Gagal memuat kategori.")
                        }
                    }else{
                        state.value = CategoryState.ShowToast("Gagal memuat kategori. Coba lagi nanti.")
                    }
                    state.value = CategoryState.IsLoading(false)
                }
            })
        }catch (e: Exception){
            println(e.message)
            state.value = CategoryState.IsLoading(false)
            state.value = CategoryState.ShowToast("Tidak dapat memuat kategori")
        }
    }

    fun listenToUIState() = state
    fun listenCategories() = categories
}

sealed class CategoryState {
    data class IsLoading(var state: Boolean = false) : CategoryState()
    data class ShowToast(var message: String) : CategoryState()
}