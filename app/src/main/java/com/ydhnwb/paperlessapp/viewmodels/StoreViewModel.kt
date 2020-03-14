package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception

class StoreViewModel : ViewModel(){
    private var state : SingleLiveEvent<StoreState> = SingleLiveEvent()
    private var api = ApiClient.instance()
    private var myStores = MutableLiveData<List<Store>>()
    private var otherStore = MutableLiveData<List<Store>>()

    fun validate(store : Store) : Boolean {
        state.value = StoreState.Reset
        if(store.store_logo.isNullOrEmpty()) {
            state.value = StoreState.ShowToast("Belum ada gambar toko yang dipilih. Anda wajib memberikan satu gambar untuk toko")
            state.value = StoreState.Validate(store_logo = "Belum ada gambar toko yang dipilih. Anda wajib memberikan satu gambar untuk toko")
            return false
        }else if(store.name.isNullOrEmpty()){
            state.value = StoreState.ShowToast("Nama toko tidak boleh kosong")
            state.value = StoreState.Validate(store_name = "Nama toko tidak boleh kosong")
            return false
        }else if(store.description.isNullOrEmpty()){
            state.value = StoreState.ShowToast("Deskripsi toko tidak boleh kosong")
            state.value = StoreState.Validate(store_name = "Deskripsi toko tidak boleh kosong")
            return false
        }else if(store.phone.isNullOrEmpty() || store.phone.toString().length < 10){
            state.value = StoreState.ShowToast("Nomor telepon tidak valid")
            state.value = StoreState.Validate("Nomor telepon tidak valid")
            return false
        }else if(!PaperlessUtil.isValidEmail(store.email.toString())){
            state.value = StoreState.ShowToast("Email tidak valid")
            state.value = StoreState.Validate(store_name = "Email tidak valid")
            return false
        }else if(store.address.isNullOrEmpty()){
            state.value = StoreState.ShowToast("Alamat toko tidak boleh kosong")
            state.value = StoreState.Validate(store_name = "Alamat toko tidak boleh kosong")
            return false
        }
        return true
    }


    fun storeCreate(token : String, store: Store){
        try{
            state.value = StoreState.Reset
            state.value = StoreState.IsLoading(true)
            val file = File(store.store_logo.toString())
            val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
            val name = RequestBody.create(MultipartBody.FORM, store.name.toString())
            val desc = RequestBody.create(MultipartBody.FORM, store.description.toString())
            val address = RequestBody.create(MultipartBody.FORM, store.address.toString())
            val phone = RequestBody.create(MultipartBody.FORM, store.phone.toString())
            val email = RequestBody.create(MultipartBody.FORM, store.email.toString())
            val photo = MultipartBody.Part.createFormData("store_logo", file.name, requestBodyForFile)
            api.store_create(token, name, desc, email, phone, address, photo).enqueue(object : Callback<WrappedResponse<Store>>{
                override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                    println(t.message)
                    state.value = StoreState.ShowToast(t.message.toString())
                    state.value = StoreState.IsLoading(false)
                }

                override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                    if(response.isSuccessful){
                        val body = response.body()
                        body?.let {
                            if(it.status){
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
        }catch (e : Exception){
            println(e.message)
            state.value = StoreState.ShowToast(e.message.toString())
            state.value = StoreState.IsLoading(false)
        }

    }

    fun fetchStore(token: String){
        try {
            state.value = StoreState.IsLoading(true, false)
            api.store_get(token).enqueue(object : Callback<WrappedListResponse<Store>>{
                override fun onFailure(call: Call<WrappedListResponse<Store>>, t: Throwable) {
                    println(t.message.toString())
                    state.value = StoreState.IsLoading()
                    state.value = StoreState.ShowToast(t.message.toString())
                }

                override fun onResponse(call: Call<WrappedListResponse<Store>>, response: Response<WrappedListResponse<Store>>) {
                    if(response.isSuccessful){
                        val body = response.body() as WrappedListResponse<Store>
                        if(body.status){ myStores.postValue(body.data) }else{ state.value = StoreState.ShowToast("Kesalahan saat mengambil data dari server") }
                    }else{
                        println("Response is not successfull with error code ${response.code()}")
                        state.value = StoreState.ShowToast("Terjadi kesalahan. Coba lagi nanti")
                    }
                    state.value = StoreState.IsLoading()
                }
            })

        }catch (e: Exception){
            println(e.message)
            state.value = StoreState.ShowToast(e.message.toString())
            state.value = StoreState.IsLoading(false)
        }

    }

    fun fetchOtherStore(){
        state.value = StoreState.IsLoading(true, true)
        val list= mutableListOf<Store>()
//        for(i in 0..6){
//            list.add(Store().apply {
//                id = i
//                name = "Toko lain $i"
//                store_logo = "https://cdn.vox-cdn.com/thumbor/SVEQv9ZyogzkPLs4PwTCh1NBCHg=/0x0:2048x1365/1200x800/filters:focal(861x520:1187x846)/cdn.vox-cdn.com/uploads/chorus_image/image/59488337/20786021_1964885550462647_3189575152413374824_o.0.jpg"
//            })
//        }
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
                        var store_email : String? = null, var store_logo : String? = null) : StoreState()
    object Reset : StoreState()

}