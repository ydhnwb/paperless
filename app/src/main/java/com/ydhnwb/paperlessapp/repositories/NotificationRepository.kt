package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.Notification
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface NotificationContract {
    fun getNotifications(token: String, listener: ArrayResponse<Notification>)
}

class NotificationRepository (private val api: ApiService) : NotificationContract {
    override fun getNotifications(token: String, listener: ArrayResponse<Notification>) {
        api.notification_get(token).enqueue(object: Callback<WrappedListResponse<Notification>>{
            override fun onFailure(call: Call<WrappedListResponse<Notification>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedListResponse<Notification>>, response: Response<WrappedListResponse<Notification>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

}