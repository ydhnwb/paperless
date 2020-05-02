package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.ydhnwb.paperlessapp.models.Order
import com.ydhnwb.paperlessapp.models.OrderSend
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel(private val api : ApiService) : ViewModel(){
    private var state : SingleLiveEvent<OrderState> = SingleLiveEvent()
    private var orders = MutableLiveData<List<Order>>()

    fun setLoading(){ state.value = OrderState.IsLoading(true) }
    fun hideLoading(){ state.value = OrderState.IsLoading(false) }

    fun confirmOrder(token: String, orderSend: OrderSend){
        setLoading()
        val convertedToJson = Gson().toJson(orderSend)
        val body: RequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), convertedToJson)
        api.order_confirm(token, body).enqueue(object : Callback<WrappedResponse<Order>>{
            override fun onFailure(call: Call<WrappedResponse<Order>>, t: Throwable) {
                println(t.message)
                state.value = OrderState.ShowToast(t.message.toString())
                state.value = OrderState.Failed(t.message.toString())
                hideLoading()
            }

            override fun onResponse(call: Call<WrappedResponse<Order>>, response: Response<WrappedResponse<Order>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    if(b!!.status){
                        println(b.data!!.toString())
                        state.value = OrderState.Success(b.data!!)
                        state.value = OrderState.ShowToast("Sukses")
                    }else{
                        state.value = OrderState.ShowToast("Tidak dapat membuat pesanan")
                        state.value = OrderState.Failed("Tidak dapat membuat pesanan")
                    }
                }else{
                    state.value = OrderState.ShowToast("Gagal saat membuat pesanan")
                    state.value = OrderState.Failed("Gagal saat membuat pesanan")
                }
                hideLoading()
            }
        })
    }

    fun listenToState() = state

}

sealed class OrderState {
    data class IsLoading(var state : Boolean) : OrderState()
    data class ShowToast(var message: String) : OrderState()
    data class Success(var order : Order) : OrderState()
    data class Failed(var message : String) : OrderState()
}