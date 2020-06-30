package com.ydhnwb.paperlessapp.webservices

import com.google.gson.annotations.SerializedName
import com.ydhnwb.paperlessapp.BuildConfig
import com.ydhnwb.paperlessapp.models.*
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object {
        private var retrofit : Retrofit? = null
        private val client = OkHttpClient.Builder().apply {
            writeTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
        }.build()
        private fun getClient() : Retrofit {
            return if(retrofit == null){
                retrofit = Retrofit.Builder().apply {
                    baseUrl(BuildConfig.ENDPOINT)
                    client(client)
                    addConverterFactory(ScalarsConverterFactory.create())
                    addConverterFactory(GsonConverterFactory.create())
                }.build()
                retrofit!!
            }else{
                retrofit!!
            }
        }
        fun instance() = getClient().create(ApiService::class.java)
    }
}



data class UrlRes(
    @SerializedName("url") var url : String
)