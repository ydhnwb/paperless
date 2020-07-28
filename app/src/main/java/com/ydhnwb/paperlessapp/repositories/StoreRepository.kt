package com.ydhnwb.paperlessapp.repositories

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ydhnwb.paperlessapp.models.MyWorkplace
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.*
import com.ydhnwb.paperlessapp.webservices.ApiService
import com.ydhnwb.paperlessapp.webservices.UrlRes
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

interface StoreContract {
    fun updateStoreLogo(token: String, store: Store, listener: SingleResponse<Store>)
    fun updateStoreInfo(token: String, store: Store, listener: SingleResponse<Store>)
    fun downloadReport(token: String,storeId: String, listener: SingleResponse<String>)
    fun fetchStorePage(token: String, storeId : String, listener: SingleResponse<Store>)
    fun storeUpdateWithoutImage(token: String, store: Store, listener: SingleResponse<Store>)
    fun storeUpdateWithImage(token : String, store: Store, listener: SingleResponse<Store>)
    fun storeCreate(token : String, store: Store, listener: SingleResponse<Store>)
    fun fetchMyWorkplace(token: String, listener: SingleResponse<MyWorkplace>)
    fun getMyStores(token: String, listener: ArrayResponse<Store>)
    fun deleteStore(token: String, storeId: String, listener: SingleResponse<Store>)
}

class StoreRepository (private val api: ApiService) : StoreContract {
    override fun updateStoreLogo(token: String, store: Store, listener: SingleResponse<Store>) {
        val file = File(store.store_logo.toString())
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        val photo = MultipartBody.Part.createFormData("store_logo", file.name, requestBodyForFile)
        api.store_update_image(token, store.id.toString(), photo).enqueue(object : Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println(t.message)
                listener.onFailure(Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                when{
                    response.isSuccessful -> {
                        val res = response.body()!!
                        if(res.status) listener.onSuccess(res.data) else listener.onFailure(Error(res.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun updateStoreInfo(token: String, store: Store, listener: SingleResponse<Store>) {
        val gsonBuilder = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()
        val requestBody = gsonBuilder.toJson(store)
        println(Gson().toJson(store))
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody)
        api.store_update_no_image(token, store.id.toString(), body).enqueue(object : Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println(t.message)
                listener.onFailure(Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                when{
                    response.isSuccessful -> {
                        val res = response.body()!!
                        if(res.status) listener.onSuccess(res.data) else listener.onFailure(Error(res.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun downloadReport(token: String, storeId: String, listener: SingleResponse<String>) {
        api.download_report(token, storeId).enqueue(object: Callback<WrappedResponse<UrlRes>>{
            override fun onFailure(call: Call<WrappedResponse<UrlRes>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<UrlRes>>, response: Response<WrappedResponse<UrlRes>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data?.url)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun fetchStorePage(token: String, storeId: String, listener: SingleResponse<Store>) {
        api.store_general_get(token, storeId).enqueue(object: Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun storeUpdateWithoutImage(token: String, store: Store, listener: SingleResponse<Store>) {
        val kwargs = PaperlessUtil.jsonToMapRequestBody(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(store))
        api.store_update(token,store.id.toString(), kwargs).enqueue(object : Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val body = response.body()
                    body?.let {
                        if(it.status) listener.onSuccess(it.data) else listener.onFailure(Error(it.message))
                    }
                }else{
                    listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun storeUpdateWithImage(token: String, store: Store, listener: SingleResponse<Store>) {
        val kwargs = PaperlessUtil.jsonToMapRequestBody(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(store))
        val file = File(store.store_logo.toString())
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        val photo = MultipartBody.Part.createFormData("store_logo", file.name, requestBodyForFile)
        api.store_update(token,store.id.toString(), kwargs, photo).enqueue(object : Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                when {
                    response.isSuccessful -> {
                        val b = response.body()
                        if(b!!.status) listener.onSuccess(b.data) else listener.onFailure(Error(b.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun storeCreate(token: String, store: Store, listener: SingleResponse<Store>) {
        val file = File(store.store_logo.toString())
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        val photo = MultipartBody.Part.createFormData("store_logo", file.name, requestBodyForFile)
        val kwargs = PaperlessUtil.jsonToMapRequestBody(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(store))
        api.store_create(token, kwargs, photo).enqueue(object : Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body()
                        if(b!!.status) listener.onSuccess(b.data) else listener.onFailure(Error(b.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
             }
        })
    }

    override fun fetchMyWorkplace(token: String, listener: SingleResponse<MyWorkplace>) {
        api.my_workplace(token).enqueue(object: Callback<WrappedResponse<MyWorkplace>>{
            override fun onFailure(call: Call<WrappedResponse<MyWorkplace>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<MyWorkplace>>, response: Response<WrappedResponse<MyWorkplace>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun getMyStores(token: String, listener: ArrayResponse<Store>) {
        api.store_get(token).enqueue(object : Callback<WrappedListResponse<Store>> {
            override fun onFailure(call: Call<WrappedListResponse<Store>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedListResponse<Store>>, response: Response<WrappedListResponse<Store>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun deleteStore(token: String, storeId: String, listener: SingleResponse<Store>) {
        api.store_delete(token, storeId).enqueue(object: Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body()
                        if(b!!.status) listener.onSuccess(b.data) else listener.onFailure(Error(b.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }
}