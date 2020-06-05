package com.ydhnwb.paperlessapp.activities.store_page_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.repositories.StoreRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class StorePageViewModel (private val storeRepository: StoreRepository) : ViewModel(){
    private val state : SingleLiveEvent<StorePageState> = SingleLiveEvent()
    private val store = MutableLiveData<Store>()

    private fun setLoading(){ state.value = StorePageState.IsLoading(true) }
    private fun hideLoading(){ state.value = StorePageState.IsLoading(false) }
    private fun toast(message: String){ state.value = StorePageState.ShowToast(message) }

    fun fetchStorePage(token: String, storeId: String){
        setLoading()
        storeRepository.fetchStorePage(token, storeId){ resultStore, e ->
            hideLoading()
            e?.let { it.message?.let { m ->toast(m) } }
            resultStore?.let { store.postValue(it) }
        }
    }

    fun listenToUIState() = state
    fun listenToStore() = store
}

sealed class StorePageState {
    data class IsLoading(var state : Boolean) : StorePageState()
    data class ShowToast(var message: String) : StorePageState()
}