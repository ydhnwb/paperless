package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Order
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.webservices.ApiClient

class OrderViewModel : ViewModel(){
    private var api = ApiClient.instance()
    private var state : SingleLiveEvent<OrderState> = SingleLiveEvent()
    private var orders = MutableLiveData<List<Order>>()
}

sealed class OrderState {
    data class IsLoading(var state : Boolean) : OrderState()
    data class ShowToast(var message: String) : OrderState()
}