package com.ydhnwb.paperlessapp.fragments.main.dashboard_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.repositories.StoreRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class DashboardViewModel(private val storeRepository: StoreRepository) : ViewModel() {
    private val state: SingleLiveEvent<DashboardState> = SingleLiveEvent()
    private val myStores = MutableLiveData<List<Store>>()
    private val myWorkplace = MutableLiveData<Store>()

    private fun setLoading(){ state.value = DashboardState.IsLoading(true) }
    private fun hideLoading(){ state.value = DashboardState.IsLoading(false) }

    private fun setLoading(isOther: Boolean){ state.value = DashboardState.IsLoading(true, isOther) }
    private fun hideLoading(isOther: Boolean){ state.value = DashboardState.IsLoading(false, isOther) }

    private fun toast(message: String){ state.value = DashboardState.ShowToast(message) }
    private fun successDelete() { state.value = DashboardState.SuccessDeleted }

    fun fetchMyStores(token: String){
        setLoading()
        storeRepository.getMyStores(token){ stores, error ->
            hideLoading()
            error?.let { it.message?.let { message -> toast(message) } }
            stores?.let {
                myStores.postValue(it)
            }
        }
    }

    fun deleteStore(token: String, storeId: String){
        setLoading()
        storeRepository.deleteStore(token, storeId){ resultBool, error ->
            hideLoading()
            error?.let { it.message?.let { message -> toast(message) } }
            if(resultBool){
                successDelete()
            }
        }
    }

    fun fetchMyWorkplace(token: String){
        setLoading(isOther = true)
        storeRepository.fetchMyWorkplace(token){ storeResult, e ->
            hideLoading(true)
            e?.let { it.message?.let { m -> toast(m) } }
            storeResult?.let {
                myWorkplace.postValue(it)
            }
        }
    }

    fun listenToUIState() = state
    fun listenToMyStores() = myStores
    fun listenToMyWorkplace() = myWorkplace
}

sealed class DashboardState {
    object SuccessDeleted : DashboardState()
    data class IsLoading(var isLoading : Boolean = false, var isOther : Boolean = false) : DashboardState()
    data class ShowToast(var message: String) : DashboardState()
}