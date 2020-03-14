package com.ydhnwb.paperlessapp.utilities

import com.google.gson.annotations.SerializedName

data class WrappedListResponse<T> (
    @SerializedName("message") var message : String,
    @SerializedName("status") var status : Boolean,
    @SerializedName("data") var data : List<T>? = null
)


data class WrappedResponse<T> (
    @SerializedName("message") var message : String,
    @SerializedName("status") var status : Boolean,
    @SerializedName("data") var data : T? = null
)
