package com.ydhnwb.paperlessapp.models

import com.google.gson.annotations.SerializedName

data class Store(
    @SerializedName("id") var id : Int?,
    @SerializedName("owner_id") var owner_id : Int?,
    @SerializedName("name") var name : String?,
    @SerializedName("description") var description : String?,
    @SerializedName("email") var email : String?,
    @SerializedName("phone") var phone : String?,
    @SerializedName("address") var address : String?,
    @SerializedName("rating") var rating : Float?,
    @SerializedName("store_logo") var store_logo : String?

) {
    constructor() : this(null, null, null, null, null, null,null,
        null, null)
}