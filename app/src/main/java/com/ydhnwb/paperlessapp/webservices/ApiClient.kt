package com.ydhnwb.paperlessapp.webservices

import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object {
        const val END_POINT = "https://paperless-app.herokuapp.com/api/"
        private const val ENDPOINT = "https://paperless-app.herokuapp.com/api/"
        private var retrofit : Retrofit? = null
        private val client = OkHttpClient.Builder().apply {
            writeTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
        }.build()
        private fun getClient() : Retrofit {
            return if(retrofit == null){
                retrofit = Retrofit.Builder().apply {
                    baseUrl(ENDPOINT)
                    client(client)
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

interface ApiService {
    @FormUrlEncoded
    @POST("v1/login")
    fun login(@Field("email") email : String, @Field("password") password : String) : Call<WrappedResponse<User>>

    @FormUrlEncoded
    @POST("v1/register")
    fun register(@Field("name") name : String, @Field("email") email : String, @Field("password") password : String) : Call<WrappedResponse<User>>
}