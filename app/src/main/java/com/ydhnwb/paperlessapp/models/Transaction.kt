package com.ydhnwb.paperlessapp.models

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("totalPrice") var totalPrice : Int? = null
)