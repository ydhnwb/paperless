package com.ydhnwb.paperlessapp.models

import com.google.gson.annotations.SerializedName

data class EmployeeResponse(
    @SerializedName("store") var store : Store? = null,
    @SerializedName("employees") var employees : List<Employee> = mutableListOf()
)

data class Employee(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("user") var user : User? = null,
    @SerializedName("role") var role : Int? = null,
    @SerializedName("joined)at") var joined : String? = null
)