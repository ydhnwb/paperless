package com.ydhnwb.paperlessapp.ui.detail_order

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.repositories.OrderRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class DetailOrderViewModel (private val orderRepository: OrderRepository): ViewModel(){
    private val state : SingleLiveEvent<DetailOrderActivityState> = SingleLiveEvent()

    fun downloadInvoice(token: String, orderId: String){
        orderRepository.downloadInvoice(token, orderId, object : SingleResponse<String>{
            override fun onSuccess(data: String?) {
                data?.let { url ->
                    state.value = DetailOrderActivityState.Downloaded(url)
                }
            }
            override fun onFailure(err: Error) { err.message?.let { state.value = DetailOrderActivityState.ShowToast(it) }}
        })
    }
    fun listenToState() = state
}

sealed class DetailOrderActivityState {
    data class ShowToast(val message : String) : DetailOrderActivityState()
    data class Downloaded(val url : String) : DetailOrderActivityState()
}