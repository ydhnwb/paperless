package com.ydhnwb.paperlessapp.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") var id : Int?,
    @SerializedName("name") var name : String?,
    @SerializedName("email") var email : String?,
    @SerializedName("phone") var phone : String?,
    @SerializedName("api_token") var api_token : String?
) {
    constructor() : this(null, null, null, null, null)
}