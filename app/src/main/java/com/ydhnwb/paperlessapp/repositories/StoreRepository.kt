package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoreRepository (private val api: ApiService){

    fun getMyStores(token: String, completion: (List<Store>?, Error?) -> Unit){
        api.store_get(token).enqueue(object : Callback<WrappedListResponse<Store>> {
            override fun onFailure(call: Call<WrappedListResponse<Store>>, t: Throwable) {
                println(t.message.toString())
                completion(null, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedListResponse<Store>>, response: Response<WrappedListResponse<Store>>) {
                if(response.isSuccessful){
                    val body = response.body() as WrappedListResponse<Store>
                    if(body.status){
                        completion(body.data, null)
                    }else{
                        completion(null, Error("Cannot get list of store. Please check your internet connection"))
                    }
                }else{
                    completion(null, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun deleteStore(token: String, storeId: String, completion: (Boolean, Error?) -> Unit){
        api.store_delete(token, storeId).enqueue(object: Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println("onFailure storeDelete -> ${t.message.toString()}")
                completion(false, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val b = response.body() as WrappedResponse
                    if(b.status){
                        completion(true, null)
                    }else{
                        completion(false, Error("Cannot delete store"))
                    }
                }else{
                    completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun storeCreate(token : String, store: Store, completion: (Boolean, Error?) -> Unit){
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
                completion(false, Error(t.message.toString()))
            }
            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val body = response.body()
                    body?.let {
                        if(it.status){
                            completion(true, null)
                        }else{
                            completion(false, Error("Gagal saat membuat toko"))
                        }
                    }
                }else{
                    completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun storeUpdateWithImage(token: String, store: Store, completion: (Boolean, Error?) -> Unit){
        val map = HashMap<String, RequestBody>().apply {
            put("name", RequestBody.create(MultipartBody.FORM, store.name.toString()))
            put("description", RequestBody.create(MultipartBody.FORM, store.description.toString()))
            put("address", RequestBody.create(MultipartBody.FORM, store.address.toString()))
            put("phone", RequestBody.create(MultipartBody.FORM, store.phone.toString()))
            put("email", RequestBody.create(MultipartBody.FORM, store.email.toString()))
        }
        val file = File(store.store_logo.toString())
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        val photo = MultipartBody.Part.createFormData("store_logo", file.name, requestBodyForFile)
        api.store_update(token,store.id.toString(), map, photo).enqueue(object : Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println("store_update onFailure -> ${t.message}")
                completion(false, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val body = response.body()
                    body?.let {
                        if(it.status){
                            completion(true, null)
                        }else{
                            completion(false, Error("Cannot update the store. (${it.message})"))
                        }
                    }
                }else{
                    completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun storeUpdateWithoutImage(token: String, store: Store, completion: (Boolean, Error?) -> Unit){
        val map = HashMap<String, RequestBody>().apply {
            put("name", RequestBody.create(MultipartBody.FORM, store.name.toString()))
            put("description", RequestBody.create(MultipartBody.FORM, store.description.toString()))
            put("address", RequestBody.create(MultipartBody.FORM, store.address.toString()))
            put("phone", RequestBody.create(MultipartBody.FORM, store.phone.toString()))
            put("email", RequestBody.create(MultipartBody.FORM, store.email.toString()))
        }

        api.store_update(token,store.id.toString(), map).enqueue(object : Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println("store_update onFailure -> ${t.message}")
                completion(false, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val body = response.body()
                    body?.let {
                        if(it.status){
                            completion(true, null)
                        }else{
                            completion(false, Error("Cannot update the store. (${it.message})"))
                        }
                    }
                }else{
                    completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun fetchStorePage(token: String, storeId : String, completion: (Store?, Error?) -> Unit){
        api.store_general_get(token, storeId).enqueue(object: Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    if(b!!.status){
                        completion(b.data, null)
                    }else{
                        completion(null, Error("Cannot get store data. Try again later"))
                    }
                }else{
                    completion(null, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun fetchMyWorkplace(token: String, completion: (Store?, Error?) -> Unit){
        api.my_workplace(token).enqueue(object: Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    if(b!!.status){
                        completion(b.data, null)
                    }else{
                        completion(null, Error())
                    }
                }else{
                    completion(null, Error("${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }
}