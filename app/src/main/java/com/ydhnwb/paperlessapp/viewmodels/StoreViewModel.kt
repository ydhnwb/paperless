package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
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
    private var myStores = MutableLiveData<List<Store>>()
    private var otherStore = MutableLiveData<List<Store>>()

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
                state.value = StoreState.ShowToast(t.message.toString())
                state.value = StoreState.IsLoading(false)
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val body = response.body()
                    body?.let {
                        if(it.status!!){
                            state.value = StoreState.Success()
                        }else{
                            state.value = StoreState.ShowToast("Tidak dapat membuat toko. Coba lagi nanti")
                        }
                    }
                }else{
                    state.value = StoreState.ShowToast("Tidak dapat membuat toko.")
                }
                state.value = StoreState.IsLoading(false)
            }
        })
    }

    fun fetchStore(){
        state.value = StoreState.IsLoading(true, false)
        val list = mutableListOf<Store>()
        for(i in 0..4){
            list.add(Store().apply {
                id = i
                name = "Toko $i"
                store_logo = "https://cdn.vox-cdn.com/thumbor/SVEQv9ZyogzkPLs4PwTCh1NBCHg=/0x0:2048x1365/1200x800/filters:focal(861x520:1187x846)/cdn.vox-cdn.com/uploads/chorus_image/image/59488337/20786021_1964885550462647_3189575152413374824_o.0.jpg"
            })
        }
        myStores.postValue(list)
        state.value = StoreState.IsLoading(false, false)
    }

    fun fetchOtherStore(){
        state.value = StoreState.IsLoading(true, true)
        val list= mutableListOf<Store>()
        for(i in 0..4){
            list.add(Store().apply {
                id = i
                name = "Toko lain $i"
                store_logo = "https://cdn.vox-cdn.com/thumbor/SVEQv9ZyogzkPLs4PwTCh1NBCHg=/0x0:2048x1365/1200x800/filters:focal(861x520:1187x846)/cdn.vox-cdn.com/uploads/chorus_image/image/59488337/20786021_1964885550462647_3189575152413374824_o.0.jpg"
            })
        }
        otherStore.postValue(list)
        state.value = StoreState.IsLoading(false, true)
    }


    fun listenToMyStore() = myStores
    fun listenToOtherStore() = otherStore
    fun listenUIState() = state
}


sealed class StoreState{
    data class Success(var params : String? = null) : StoreState()
    data class ShowToast(var message : String) : StoreState()
    data class IsLoading(var isLoading : Boolean = false, var isOther : Boolean = false) : StoreState()
    data class Validate(var store_name : String? = null, var store_desc : String? = null,
                        var store_phone : String? = null, var store_address : String? = null,
                        var store_logo : String? = null) : StoreState()
    object Reset : StoreState()

}