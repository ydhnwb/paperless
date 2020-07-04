package com.ydhnwb.paperlessapp.models

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("subtitle") var subtitle: String? = null,
    @SerializedName("sender") var storeSender: Store? = null,
    @SerializedName("receiver") var userReceiver: User? = null,
    @SerializedName("type") var type: Int? = null,
    @SerializedName("created_at") var date : String? = null
)