package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyWorkplace(
    @SerializedName("role") var role : Int? = null,
    @SerializedName("store") var store: Store? = null
) : Parcelable