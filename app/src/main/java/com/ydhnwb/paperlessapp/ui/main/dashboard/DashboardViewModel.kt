package com.ydhnwb.paperlessapp.ui.main.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.MyWorkplace
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.repositories.StoreRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class DashboardViewModel(private val storeRepository: StoreRepository) : ViewModel() {
    private val state: SingleLiveEvent<DashboardState> = SingleLiveEvent()
    private val myStores = MutableLiveData<List<Store>>()
    private val myWorkplace = MutableLiveData<MyWorkplace>()

    private fun setLoading(isOther: Boolean){ state.value = DashboardState.IsLoading(true, isOther) }
    private fun hideLoading(isOther: Boolean){ state.value = DashboardState.IsLoading(false, isOther) }

    private fun toast(message: String){ state.value = DashboardState.ShowToast(message) }
    private fun successDelete() { state.value = DashboardState.SuccessDeleted }

    fun fetchMyStores(token: String){
        setLoading(false)
        storeRepository.getMyStores(token, object : ArrayResponse<Store>{
            override fun onSuccess(datas: List<Store>?) {
                hideLoading(false)
                datas?.let { myStores.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading(false)
                err.message?.let { toast(it) }
            }
        })
    }

    fun deleteStore(token: String, storeId: String){
        setLoading(false)
        storeRepository.deleteStore(token, storeId, object :SingleResponse<Store>{
            override fun onSuccess(data: Store?) {
                hideLoading(false)
                data?.let { successDelete() }
            }
            override fun onFailure(err: Error) {
                hideLoading(false)
                err.message?.let { toast(it) }
            }
        })
    }

    fun fetchMyWorkplace(token: String){
        setLoading(true)
        storeRepository.fetchMyWorkplace(token, object: SingleResponse<MyWorkplace>{
            override fun onSuccess(data: MyWorkplace?) {
                hideLoading(true)
                data?.let { myWorkplace.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading(true)
                err.message?.let { toast(it) }
            }
        })
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