package com.ydhnwb.paperlessapp.ui.store_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.repositories.StoreRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class StorePageViewModel (private val storeRepository: StoreRepository) : ViewModel(){
    private val state : SingleLiveEvent<StorePageState> = SingleLiveEvent()
    private val store = MutableLiveData<Store>()

    private fun setLoading(){ state.value = StorePageState.IsLoading(true) }
    private fun hideLoading(){ state.value = StorePageState.IsLoading(false) }
    private fun toast(message: String){ state.value = StorePageState.ShowToast(message) }

    fun fetchStorePage(token: String, storeId: String){
        setLoading()
        storeRepository.fetchStorePage(token, storeId, object : SingleResponse<Store>{
            override fun onSuccess(data: Store?) {
                hideLoading()
                data?.let { store.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun listenToUIState() = state
    fun listenToStore() = store
}

sealed class StorePageState {
    data class IsLoading(var state : Boolean) : StorePageState()
    data class ShowToast(var message: String) : StorePageState()
}