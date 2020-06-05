package com.ydhnwb.paperlessapp.models

import com.google.gson.annotations.SerializedName

data class HistorySendParam(
    @SerializedName("store_id") var store_id : Int? = null
)