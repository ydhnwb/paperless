package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store(
    @SerializedName("id") var id : Int?,
    @SerializedName("owner_id") var owner_id : Int?,
    @Expose
    @SerializedName("name") var name : String?,
    @Expose
    @SerializedName("description") var description : String?,
    @Expose
    @SerializedName("email") var email : String?,
    @Expose
    @SerializedName("phone") var phone : String?,
    @Expose
    @SerializedName("address") var address : String?,
    @SerializedName("rating") var rating : Float?,
    @SerializedName("store_logo") var store_logo : String?,
    @SerializedName("products") var products : List<Product> = mutableListOf()

) : Parcelable{
    constructor() : this(null, null, null, null, null, null,null,
        null, null)
}

@Parcelize
data class StoreOrderHistory(
    @SerializedName("id") var id : Int?,
    @SerializedName("owner_id") var owner_id : Int?,
    @SerializedName("name") var name : String?,
    @SerializedName("description") var description : String?,
    @SerializedName("email") var email : String?,
    @SerializedName("phone") var phone : String?,
    @SerializedName("address") var address : String?,
    @SerializedName("rating") var rating : Float?,
    @SerializedName("store_logo") var store_logo : String?,
    @SerializedName("in") var orderIn : List<OrderHistory> = mutableListOf(),
    @SerializedName("out") var orderOut : List<OrderHistory> = mutableListOf()

) : Parcelable{
    constructor() : this(null, null, null, null, null, null,null,
        null, null)
}