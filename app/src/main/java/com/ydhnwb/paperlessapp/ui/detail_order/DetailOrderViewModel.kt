package com.ydhnwb.paperlessapp.ui.detail_order

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.repositories.OrderRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class DetailOrderViewModel (private val orderRepository: OrderRepository): ViewModel(){
    private val state : SingleLiveEvent<DetailOrderActivityState> = SingleLiveEvent()

    fun downloadInvoice(token: String, orderId: String){
        orderRepository.downloadInvoice(token,orderId){ url, e ->
            e?.let { it.message?.let { x -> state.value = DetailOrderActivityState.ShowToast(x) } }
            url?.let {
                state.value = DetailOrderActivityState.Downloaded(it)
            }
        }
    }

    fun listenToState() = state

}

sealed class DetailOrderActivityState {
    data class ShowToast(val message : String) : DetailOrderActivityState()
    data class Downloaded(val url : String) : DetailOrderActivityState()
}