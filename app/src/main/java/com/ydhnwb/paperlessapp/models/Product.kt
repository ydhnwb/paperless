package com.ydhnwb.paperlessapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("description") var description : String? = null,
    @SerializedName("code") var code : String? = null,
    @SerializedName("image") var image : String? = null,
    @SerializedName("price") var price : Int? = null,
    @SerializedName("weight") var weight : Double? = 1.0,
    @SerializedName("status") var status : Boolean? = null,
    @SerializedName("available_online") var availableOnline : Boolean = false,
    @SerializedName("quantity") var qty : Int? = null,
    @SerializedName("category") var category : Category? = null,
    @SerializedName("stock") var stock : Stock? = null,
    @SerializedName("store") var store : Store? = null,
    @SerializedName("owner") var owner : User? = null,
    var selectedQuantity : Int? = null
) : Parcelable

@Parcelize
data class Stock(@SerializedName("id") var id : Int? = null, @SerializedName("quantity") var stock : Int? = null) : Parcelable