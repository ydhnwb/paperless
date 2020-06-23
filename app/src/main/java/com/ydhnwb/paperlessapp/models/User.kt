package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @SerializedName("id") var id : Int?,
    @SerializedName("name") var name : String?,
    @SerializedName("email") var email : String?,
    @SerializedName("phone") var phone : String?,
    @SerializedName("api_token") var api_token : String?,
    @SerializedName("orders") var orders : List<OrderHistory> = mutableListOf()
) : Parcelable {
    constructor() : this(null, null, null, null, null)
}