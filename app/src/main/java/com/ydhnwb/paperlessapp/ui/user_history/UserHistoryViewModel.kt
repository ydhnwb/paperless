package com.ydhnwb.paperlessapp.ui.user_history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.History
import com.ydhnwb.paperlessapp.models.OrderHistory
import com.ydhnwb.paperlessapp.repositories.HistoryRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class UserHistoryViewModel (private val historyRepo: HistoryRepository) : ViewModel(){
    private val state : SingleLiveEvent<UserHistoryState> = SingleLiveEvent()
    private val histories = MutableLiveData<List<OrderHistory>>()

    private fun setLoading(){ state.value = UserHistoryState.Loading(true) }
    private fun hideLoading(){ state.value = UserHistoryState.Loading(false) }
    private fun toast(message: String){ state.value = UserHistoryState.ShowToast(message) }

    fun fetchHistory(token: String){
        setLoading()
        historyRepo.fetchHistory(token, null, object : SingleResponse<History>{
            override fun onSuccess(data: History?) {
                hideLoading()
                data?.let { histories.postValue(it.user?.orders) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }


    fun listenToState() = state
    fun listenToHistories() = histories
}

sealed class UserHistoryState {
    data class Loading(val state : Boolean) : UserHistoryState()
    data class ShowToast(val message: String) : UserHistoryState()
}