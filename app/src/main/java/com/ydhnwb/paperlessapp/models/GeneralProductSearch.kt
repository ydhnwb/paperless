package com.ydhnwb.paperlessapp.models

import com.google.gson.annotations.SerializedName

data class GeneralProductSearch(
    @SerializedName("all_products") var allProducts : List<Product>? = null,
    @SerializedName("promo") var promosProducts : List<Product>? = null
)