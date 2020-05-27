package com.ydhnwb.paperlessapp.webservices

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

interface ApiService {
    @FormUrlEncoded
    @POST("v1/login")
    fun login(@Field("email") email : String, @Field("password") password : String) : Call<WrappedResponse<User>>

    @FormUrlEncoded
    @POST("v1/register")
    fun register(@Field("name") name : String, @Field("email") email : String, @Field("password") password : String) : Call<WrappedResponse<User>>

    @Multipart
    @POST("v1/own/store")
    fun store_create(@Header("Authorization") token : String, @Part("name") name : RequestBody, @Part("description") description : RequestBody,
                     @Part("email") email : RequestBody, @Part("phone") phone : RequestBody, @Part("address") address : RequestBody,
                     @Part store_logo : MultipartBody.Part) : Call<WrappedResponse<Store>>
    @GET("v1/own/store")
    fun store_get(@Header("Authorization") token : String) : Call<WrappedListResponse<Store>>

    @Multipart
    @POST("v1/own/store/{id}")
    fun store_update(@Header("Authorization") token : String, @Path("id") id: String,@PartMap kwargs : HashMap<String, RequestBody>) : Call<WrappedResponse<Store>>

    @Multipart
    @POST("v1/own/store/{id}")
    fun store_update(@Header("Authorization") token : String, @Path("id") id: String,@PartMap kwargs : HashMap<String, RequestBody>, @Part store_logo : MultipartBody.Part) : Call<WrappedResponse<Store>>

    @DELETE("v1/own/store/{id}")
    fun store_delete(@Header("Authorization") token : String, @Path("id") id: String) : Call<WrappedResponse<Store>>

    @GET("v1/category")
    fun category_get() : Call<WrappedListResponse<Category>>

    @GET("v1/own/store/{id}/product")
    fun product_get(@Header("Authorization") token: String, @Path("id") id : String) : Call<WrappedListResponse<Product>>

    @Multipart
    @POST("v1/own/store/{id}/product")
    fun product_store(@Header("Authorization") token : String,
                      @Path("id") storeId : String,
                      @Part("name") name : String,
                      @Part("description") description : String,
                      @Part("code") code : String?,
                      @Part("price") price : Int,
                      @Part("category_id") categoryId : Int,
                      @Part("available_online") isOnline : Boolean = false,
                      @Part("weight") weight : Double? = 1.0,
                      @Part("status") status : Boolean = true,
                      @Part("quantity") qty : Int? = null,
                      @Part image : MultipartBody.Part)
            : Call<WrappedResponse<Product>>

    @Multipart
    @POST("v1/own/store/{id}/product/{productId}")
    fun product_update(@Header("Authorization") token : String,
                      @Path("id") storeId : String,
                      @Path("productId") productId : String,
                      @Part("name") name : String,
                      @Part("description") description : String,
                      @Part("code") code : String?,
                      @Part("price") price : Int,
                      @Part("category_id") categoryId : Int,
                      @Part("available_online") isOnline : Boolean = false,
                      @Part("weight") weight : Double? = 1.0,
                      @Part("status") status : Boolean = true,
                      @Part("quantity") qty : Int?)
            : Call<WrappedResponse<Product>>

    @Multipart
    @POST("v1/own/store/{id}/product/{productId}")
    fun product_update(@Header("Authorization") token : String,
                       @Path("id") storeId : String,
                       @Path("productId") productId : String,
                       @Part("name") name : String,
                       @Part("description") description : String,
                       @Part("code") code : String?,
                       @Part("price") price : Int,
                       @Part("category_id") categoryId : Int,
                       @Part("available_online") isOnline : Boolean = false,
                       @Part("weight") weight : Double? = 1.0,
                       @Part("status") status : Boolean = true,
                       @Part("quantity") qty : Int?,
                       @Part image : MultipartBody.Part)
            : Call<WrappedResponse<Product>>

    @DELETE("v1/own/store/{id}/product/{productId}")
    fun product_delete(@Header("Authorization") token : String, @Path("id") storeId : String, @Path("productId") productId: String)
    : Call<WrappedResponse<Product>>

    @GET("v1/users/profile")
    fun profile(@Header("Authorization") token: String) : Call<WrappedResponse<User>>

    @GET("v1/search")
    fun user_search(@Header("Authorization") token: String, @Query("query") q : String) : Call<WrappedListResponse<User>>

    @FormUrlEncoded
    @POST("v1/invitation/out")
    fun invite(@Header("Authorization") token : String, @Field("requested_by_store") requestedByStoreId : Int,
               @Field("role") role : Int, @Field("to") to : Int)
            : Call<WrappedResponse<Invitation>>

    @GET("v1/own/store/{storeId}/invitation/out")
    fun invitation_sent(@Header("Authorization") token : String, @Path("storeId") storeId : Int) :
            Call<WrappedListResponse<Invitation>>

    @GET("v1/invitation/in")
    fun invitation_in(@Header("Authorization") token : String) : Call<WrappedListResponse<Invitation>>

    @GET("v1/invitation/in/{id}/accept")
    fun invitation_acc(@Header("Authorization") token : String, @Path("id") invitationId : String) : Call<WrappedResponse<Invitation>>

    @GET("v1/invitation/in/{id}/reject")
    fun invitation_reject(@Header("Authorization") token : String, @Path("id") invitationId : String) : Call<WrappedResponse<Invitation>>

    @Headers("Content-Type: application/json")
    @POST("v1/order")
    fun order_confirm(@Header("Authorization") token : String, @Body body : RequestBody) : Call<WrappedResponse<Order>>

    @POST("v1/order/history")
    fun history_get(@Header("Authorization") token : String, @Body b : RequestBody) : Call<WrappedResponse<History>>


    @GET("v1/store/{id}")
    fun store_general_get(@Header("Authorization") token : String, @Path("id") id : String) : Call<WrappedResponse<Store>>


    @GET("v1/own/store/{storeId}/employee")
    fun store_employee(@Header("Authorization") token : String, @Path("storeId") storeId : String) : Call<WrappedResponse<EmployeeResponse>>

    @GET("v1/product")
    fun catalog_search(@Header("Authorization") token : String, @Query("query") query : String) : Call<WrappedListResponse<Product>>

    @GET("v1/store_as_employee")
    fun store_as_employee(@Header("Authorization") token : String) : Call<WrappedResponse<Store>>


    @GET("v1/user/{id}")
    fun user_by_id(@Header("Authorization") token : String, @Path("id") userId : String) : Call<WrappedResponse<User>>
}