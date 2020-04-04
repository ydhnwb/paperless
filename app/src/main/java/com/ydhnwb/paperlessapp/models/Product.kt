package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("code") var code : String? = null,
    @SerializedName("image") var image : String? = null,
    @SerializedName("price") var price : Int? = null,
    @SerializedName("weight") var weight : Float? = 1.0F,
    @SerializedName("status") var status : Boolean? = null,
    @SerializedName("available_online") var availableOnline : Boolean = false,
    @SerializedName("quantity") var qty : Int? = null,
    @SerializedName("category") var categoryId : Category? = null,
    var selectedQuantity : Int? = null
) : Parcelable