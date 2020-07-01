package com.ydhnwb.paperlessapp.webservices

import com.ydhnwb.paperlessapp.models.*
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("v1/login")
    fun login(@Field("email") email : String, @Field("password") password : String) : Call<WrappedResponse<User>>

    @FormUrlEncoded
    @POST("v1/register")
    fun register(@Field("name") name : String, @Field("email") email : String, @Field("password") password : String) : Call<WrappedResponse<User>>

    @Multipart
    @POST("v1/own/store")
    fun store_create(@Header("Authorization") token: String, @PartMap kwargs : HashMap<String, RequestBody>, @Part store_logo : MultipartBody.Part) : Call<WrappedResponse<Store>>

    @Multipart
    @POST("v1/own/store")
    fun store_create(@Header("Authorization") token : String, @Part("name") name : RequestBody, @Part("description") description : RequestBody,
                     @Part("email") email : RequestBody, @Part("phone") phone : RequestBody, @Part("address") address : RequestBody,
                     @Part store_logo : MultipartBody.Part) : Call<WrappedResponse<Store>>
    @GET("v1/own/store")
    fun store_get(@Header("Authorization") token : String) : Call<WrappedListResponse<Store>>

    @Multipart
    @POST("v1/own/store/{id}")
    fun store_update(@Header("Authorization") token : String, @Path("id") id: String, @PartMap kwargs : HashMap<String, RequestBody>) : Call<WrappedResponse<Store>>

    @Multipart
    @POST("v1/own/store/{id}")
    fun store_update(@Header("Authorization") token : String, @Path("id") id: String, @PartMap kwargs : HashMap<String, RequestBody>, @Part store_logo : MultipartBody.Part) : Call<WrappedResponse<Store>>

    @DELETE("v1/own/store/{id}")
    fun store_delete(@Header("Authorization") token : String, @Path("id") id: String) : Call<WrappedResponse<Store>>

    @GET("v1/category")
    fun category_get() : Call<WrappedListResponse<Category>>

    @GET("v1/own/store/{id}/product")
    fun product_get(@Header("Authorization") token: String, @Path("id") id : String) : Call<WrappedListResponse<Product>>

    @Multipart
    @POST("v1/own/store/{id}/product")
    fun product_store(@Header("Authorization") token: String,
                      @Path("id") storeId : String,
                      @PartMap partMap : HashMap<String, RequestBody>,
                      @Part("category_id") categoryId : Int,
                      @Part image : MultipartBody.Part) : Call<WrappedResponse<Product>>

    @Multipart
    @POST("v1/own/store/{id}/product/{productId}")
    fun product_update(@Header("Authorization") token: String,
                       @Path("id") storeId : String,
                       @Path("productId") productId : String,
                       @PartMap partMap : HashMap<String, RequestBody>,
                       @Part("category_id") categoryId : Int,
                       @Part image : MultipartBody.Part
    ) : Call<WrappedResponse<Product>>


    @GET("v1/promo")
    fun get_promoted_product(@Header("Authorization") token : String) : Call<WrappedListResponse<Product>>


    @POST("v1/own/store/{id}/product/{productId}")
    fun product_update(@Header("Authorization") token: String,
                       @Path("id") storeId : String,
                       @Path("productId") productId : String,
                       @Body body : RequestBody
    ) : Call<WrappedResponse<Product>>

    @FormUrlEncoded
    @PATCH("v1/promo")
    fun promo_apply(@Header("Authorization") token : String, @Field("product_id") productId: String,
                    @Field("discount_by_percent") discountByPercent : Float) : Call<WrappedResponse<Product>>

    @DELETE("v1/own/store/{id}/product/{productId}")
    fun product_delete(@Header("Authorization") token : String, @Path("id") storeId : String, @Path("productId") productId: String)
            : Call<WrappedResponse<Product>>

    @GET("v1/users/profile")
    fun profile(@Header("Authorization") token: String) : Call<WrappedResponse<User>>

    @GET("v1/search")
    fun user_search(@Header("Authorization") token: String, @Query("query") q : String) : Call<WrappedListResponse<User>>

    @FormUrlEncoded
    @POST("v1/invitation/out")
    fun invite(@Header("Authorization") token : String, @Field("requested_by_store") requestedByStoreId : Int, @Field("role") role : Int, @Field("to") to : Int): Call<WrappedResponse<Invitation>>

    @GET("v1/own/store/{storeId}/invitation/out")
    fun invitation_sent(@Header("Authorization") token : String, @Path("storeId") storeId : Int) : Call<WrappedListResponse<Invitation>>

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
    fun catalog_search(@Header("Authorization") token : String, @Query("query") query : String) : Call<WrappedResponse<GeneralProductSearch>>

    @GET("v1/user/{id}")
    fun user_by_id(@Header("Authorization") token : String, @Path("id") userId : String) : Call<WrappedResponse<User>>

    @GET("v1/my_workplace")
    fun my_workplace(@Header("Authorization") token : String) : Call<WrappedResponse<MyWorkplace>>

    @DELETE("v1/own/store/{id_store}/employee/{id_employee}")
    fun employee_remove(@Header("Authorization")token:String, @Path("id_store") storeId: String, @Path("id_employee") employeeId : String) : Call<WrappedResponse<Store>>

    @FormUrlEncoded
    @POST("v1/report")
    fun download_report(@Header("Authorization") token : String, @Field("store_id") storeId : String) : Call<WrappedResponse<UrlRes>>

    @FormUrlEncoded
    @POST("v1/invoice")
    fun download_invoice(@Header("Authorization") token : String, @Field("order_id") orderId : String) : Call<WrappedResponse<UrlRes>>
}