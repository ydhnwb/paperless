package com.ydhnwb.paperlessapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class EmployeeResponse(
    @SerializedName("store") var store : Store? = null,
    @SerializedName("employees") var employees : List<Employee> = mutableListOf()
)

@Parcelize
data class Employee(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("user") var user : User? = null,
    @SerializedName("role") var role : Int? = null,
    @SerializedName("joined_at") var joined : String? = null
) : Parcelable