package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    @SerializedName("id") var id : Int? = null,

    @Expose
    @SerializedName("name") var name : String? = null,

    @Expose
    @SerializedName("description") var description : String? = null,

    @Expose
    @SerializedName("code") var code : String? = null,

    @SerializedName("image") var image : String? = null,

    @Expose
    @SerializedName("price") var price : Int? = null,

    @SerializedName("status") var status : Boolean? = null,

    @Expose
    @SerializedName("quantity") var qty : Int? = null,

    @SerializedName("category") var category : Category? = null,

    @SerializedName("store") var store : Store? = null,

    @SerializedName("owner") var owner : User? = null,
    @SerializedName("order") var orderAlt: OrderAlt? = null,

    @Expose
    @SerializedName("discount_by_percent") var discountByPercent : Float? = null,

    @Expose
    @SerializedName("category_id") var categoryId : Int? = null,

    var selectedQuantity : Int? = null
) : Parcelable

@Parcelize
data class ProductSend(
    @SerializedName("id") var id : Int,
    @SerializedName("quantity") var quantity : Int,
    @SerializedName("price") var price : Int

): Parcelable