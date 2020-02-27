package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("owner") var ownerId : Int? = null,
    @SerializedName("name") var name : String? = null
) : Parcelable