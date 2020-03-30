package com.ydhnwb.paperlessapp.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("order_details") var orderDetails : List<Product>
)