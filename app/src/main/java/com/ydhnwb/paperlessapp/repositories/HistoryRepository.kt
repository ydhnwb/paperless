package com.ydhnwb.paperlessapp.repositories

import com.google.gson.GsonBuilder
import com.ydhnwb.paperlessapp.models.History
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.models.HistorySendParam
import com.ydhnwb.paperlessapp.webservices.ApiService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryRepository (private val api: ApiService){

    fun fetchHistory(token: String, storeId: Int?, completion: (History?, Error?) -> Unit){
        val histSend = HistorySendParam(storeId)
        val g = GsonBuilder().create()
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), g.toJson(histSend))
        api.history_get(token, body).enqueue(object : Callback<WrappedResponse<History>> {
            override fun onFailure(call: Call<WrappedResponse<History>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<History>>, response: Response<WrappedResponse<History>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    completion(b?.data, null)
                }else{
                    println(response.errorBody())
                    println(response.message())
                    println(response.headers())
                    completion(null, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }
}