package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.ydhnwb.paperlessapp.models.History
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel (private val api : ApiService) : ViewModel(){
    private val state : SingleLiveEvent<HistoryState> = SingleLiveEvent()
    private val orderHistory = MutableLiveData<History>()

    private fun setLoading() {  state.value = HistoryState.IsLoading(true) }
    private fun hideLoading() {  state.value = HistoryState.IsLoading(false) }
    private fun toast(m : String) { state.value = HistoryState.ShowToast(m) }

    fun fetchHistory(token: String, storeId: Int?){
        setLoading()
        val histSend = HistorySendParam(storeId)
        val g = GsonBuilder().serializeNulls().create()
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), g.toJson(histSend))
        api.history_get(token, body).enqueue(object : Callback<WrappedResponse<History>>{
            override fun onFailure(call: Call<WrappedResponse<History>>, t: Throwable) {
                println(t.message)
                toast(t.message.toString())
                hideLoading()
            }

            override fun onResponse(call: Call<WrappedResponse<History>>, response: Response<WrappedResponse<History>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    orderHistory.postValue(b?.data)
                }else{
                    toast("Tidak dapat mengambil data riwayat transaksi")
                }
                hideLoading()
            }
        })
    }

    fun listenToState() = state
    fun listenToOrderHistory() = orderHistory
}

sealed class HistoryState {
    data class IsLoading(var state : Boolean) : HistoryState()
    data class ShowToast(var message : String) : HistoryState()
}

data class HistorySendParam(
    @SerializedName("store_id") var store_id : Int? = null
)