package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

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
    @SerializedName("sell_by_store") var sellByStore : Int? = null,
    @SerializedName("buy_by_store") var boughtByStore : Int? = null,
    @SerializedName("buy_by_user") var boughtByUser : Int? = null,
    @SerializedName("discount") var discountInPrice : Int = 0,
    @SerializedName("products") var products : List<ProductSend> = mutableListOf()
): Parcelable


@Parcelize
data class OrderHistory(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("sell_by_store") var sellByStore : Store? = null,
    @SerializedName("buy_by_store") var boughtByStore : Store? = null,
    @SerializedName("buy_by_user") var boughtByUser : User? = null,
    @SerializedName("datetime") var datetime : String? = null,
    @SerializedName("order_details") var orderDetails : List<OrderHistoryDetail> = mutableListOf(),
    @SerializedName("total_price") var totalPrice :  Int? = null,
    @SerializedName("total_price_with_discount") var totalPriceWithDiscount : Int? = null,
    @SerializedName("total_discount_by_percent") var totalDiscountByPercentInCurrency : Int? = null,
    var date : Date? = null
) : Parcelable

@Parcelize
data class OrderHistoryDetail (
    @SerializedName("id") var id : Int? = null,
    @SerializedName("product_id") var productId:  Int? = null,
    @SerializedName("name") var productName :  String? = null,
    @SerializedName("image") var productImage :  String? = null,
    @SerializedName("price") var productPrice :  Int? = null,
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("sold_at") var soldAt :  String? = null,
    @SerializedName("discount_by_percent") var discountByPercent: Float? = null

): Parcelable