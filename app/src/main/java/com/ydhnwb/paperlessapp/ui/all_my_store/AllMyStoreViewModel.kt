package com.ydhnwb.paperlessapp.ui.all_my_store

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.repositories.StoreRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class AllMyStoreViewModel (private val storeRepository: StoreRepository) : ViewModel(){
    private val state : SingleLiveEvent<AllMyStoreState> = SingleLiveEvent()
    private val stores = MutableLiveData<List<Store>>()

    private fun successDelete(){
        state.value = AllMyStoreState.SuccessDelete
    }

    private fun setLoading(){
        state.value = AllMyStoreState.Loading(true)
    }
    private fun hideLoading(){
        state.value = AllMyStoreState.Loading(false)
    }
    private fun toast(message: String){
        state.value = AllMyStoreState.ShowToast(message)
    }

    fun deleteStore(token: String, storeId: String){
        setLoading()
        storeRepository.deleteStore(token, storeId, object : SingleResponse<Store> {
            override fun onSuccess(data: Store?) {
                hideLoading()
                data?.let { successDelete() }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun fetchMyStores(token: String){
        setLoading()
        storeRepository.getMyStores(token, object: ArrayResponse<Store>{
            override fun onSuccess(datas: List<Store>?) {
                hideLoading()
                datas?.let { stores.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun getState() = state
    fun getStores() = stores
}

sealed class AllMyStoreState {
    object SuccessDelete : AllMyStoreState()
    data class Loading(val isLoading : Boolean) : AllMyStoreState()
    data class ShowToast(val message : String) : AllMyStoreState()
}