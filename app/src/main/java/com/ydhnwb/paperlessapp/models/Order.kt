package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order (
    @SerializedName("id") var id : Int? = null,
    @SerializedName("code") var code : String? = null,
    @SerializedName("order") var orderDetail: OrderDetail? = null
) : Parcelable



@Parcelize
data class OrderDetail(
    @SerializedName("order_count") var orderCount : Int? = null,
    @SerializedName("total_price") var totalPrice : Int? = null,
    @SerializedName("user") var user : User? = null,
    @SerializedName("store") var store : Store? = null,
    @SerializedName("products") var products: List<Product> = mutableListOf()
) : Parcelable

@Parcelize
data class OrderAlt(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("quantity") var quantity : Int? = null
) : Parcelable

@Parcelize
data class OrderSend(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("user_id") var userId : Int? = null,
    @SerializedName("store_id") var storeId : Int? = null,
    @SerializedName("products") var products : List<ProductSend> = mutableListOf()
): Parcelable