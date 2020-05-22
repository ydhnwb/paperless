package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class History (
    @SerializedName("user") var user : User? = null,
    @SerializedName("store") var store: StoreOrderHistory? = null
) : Parcelable