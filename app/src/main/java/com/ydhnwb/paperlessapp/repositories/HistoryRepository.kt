package com.ydhnwb.paperlessapp.repositories

import com.google.gson.GsonBuilder
import com.ydhnwb.paperlessapp.models.History
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.models.HistorySendParam
import com.ydhnwb.paperlessapp.utilities.SingleResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface HistoryContract {
    fun fetchHistory(token: String, storeId: Int?, listener: SingleResponse<History>)
}

class HistoryRepository (private val api: ApiService) : HistoryContract{

    override fun fetchHistory(token: String, storeId: Int?, listener: SingleResponse<History>) {
        val histSend = HistorySendParam(storeId)
        val g = GsonBuilder().create()
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), g.toJson(histSend))
        api.history_get(token, body).enqueue(object : Callback<WrappedResponse<History>> {
            override fun onFailure(call: Call<WrappedResponse<History>>, t: Throwable) {
                println(t.message)
                listener.onFailure(Error(t.message))
            }

            override fun onResponse(call: Call<WrappedResponse<History>>, response: Response<WrappedResponse<History>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }
}