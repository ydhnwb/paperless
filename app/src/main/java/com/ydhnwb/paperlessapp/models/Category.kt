package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name: String? = null
) : Parcelable {
    override fun toString(): String {
        return this.name.toString()
    }
}