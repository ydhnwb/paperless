package com.ydhnwb.paperlessapp.models

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("subtitle") var subtitle: String? = null,
    @SerializedName("sender") var storeSender: Store? = null,
    @SerializedName("receiver") var userReceiver: User? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("date") var date : String? = null
)