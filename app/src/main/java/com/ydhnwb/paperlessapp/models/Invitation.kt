package com.ydhnwb.paperlessapp.models

import com.google.gson.annotations.SerializedName

data class Invitation(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("requested_by_store") var requestedByStore : Store? = null,
    @SerializedName("to") var to : User? = null,
    @SerializedName("invited_at") var date : String? = null,
    @SerializedName("status") var status : Short? = null
)

data class InvitationAlt(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("requested_by_store") var requestedByStore : Store? = null,
    @SerializedName("to") var to : User? = null,
    @SerializedName("invited_at") var date : String? = null,
    @SerializedName("status") var status : Boolean? = null
)