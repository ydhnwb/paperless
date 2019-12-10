package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoreViewModel : ViewModel(){
    private var state : SingleLiveEvent<StoreState> = SingleLiveEvent()
    private var api = ApiClient.instance()


    fun validate(store : Store){
        state.value = StoreState.Reset
    }


    fun create(token : String, store_name: String, user_email : String, store_desc : String, store_phone : String, store_address : String, store_logo : String){
        state.value = StoreState.Reset
        val file = File(store_logo)
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        val name = RequestBody.create(MultipartBody.FORM, store_name)
        val desc = RequestBody.create(MultipartBody.FORM, store_desc)
        val address = RequestBody.create(MultipartBody.FORM, store_address)
        val phone = RequestBody.create(MultipartBody.FORM, store_phone)
        val rating = RequestBody.create(MultipartBody.FORM, "0")
        val email = RequestBody.create(MultipartBody.FORM, user_email)
        val photo = MultipartBody.Part.createFormData("store_logo", file.name, requestBodyForFile)
        api.store_create(token, name, desc, email, phone, address, rating, photo).enqueue(object : Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println(t.message)
                state.value = StoreState.Error(t.message)
                state.value = StoreState.IsLoading(false)
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val body = response.body()
                    body?.let {
                        if(it.status!!){
                            state.value = StoreState.Success()
                        }else{
                            state.value = StoreState.Error("Tidak dapat membuat toko. Coba lagi nanti")
                        }
                    }
                }else{
                    state.value = StoreState.Error("Tidak dapat membuat toko.")
                }
                state.value = StoreState.IsLoading(false)
            }
        })
    }
}


sealed class StoreState{
    data class Success(var params : String? = null) : StoreState()
    data class Error(var err : String? = null) : StoreState()
    data class IsLoading(var isLoading : Boolean = false) : StoreState()
    data class Validate(var store_name : String? = null, var store_desc : String? = null,
                        var store_phone : String? = null, var store_address : String? = null,
                        var store_logo : String? = null) : StoreState()
    object Reset : StoreState()
}