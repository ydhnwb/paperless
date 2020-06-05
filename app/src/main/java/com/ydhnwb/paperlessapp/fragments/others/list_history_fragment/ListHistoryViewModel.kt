package com.ydhnwb.paperlessapp.fragments.others.list_history_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.History
import com.ydhnwb.paperlessapp.repositories.HistoryRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class ListHistoryViewModel (private val historyRepository: HistoryRepository) : ViewModel(){
    private val history = MutableLiveData<History>()
    private val state : SingleLiveEvent<ListHistoryState> = SingleLiveEvent()

    private fun setLoading(){ state.value = ListHistoryState.IsLoading(true) }
    private fun hideLoading(){ state.value = ListHistoryState.IsLoading(false) }
    private fun toast(message: String){ state.value = ListHistoryState.ShowToast(message) }

    fun fetchHistory(token: String, storeId: Int){
        setLoading()
        historyRepository.fetchHistory(token, storeId){ resultHistory, error ->
            hideLoading()
            error?.let { it.message?.let { m -> toast(m) } }
            resultHistory?.let { history.postValue(it) }
        }
    }

    fun listenToUIState() = state
    fun listenToHistory() = history
}

sealed class ListHistoryState {
    data class IsLoading(var state: Boolean) : ListHistoryState()
    data class ShowToast(var message: String) : ListHistoryState()
}