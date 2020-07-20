package com.ydhnwb.paperlessapp.ui.analytic.performance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.*
import com.ydhnwb.paperlessapp.repositories.HistoryRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerformanceAnalyticViewModel (private val historyRepo: HistoryRepository) : ViewModel(){
    private val state : SingleLiveEvent<PerformanceAnalyticState> = SingleLiveEvent()
    private val employeePerformance = MutableLiveData<HashMap<User, Int>>()
    private val histories = MutableLiveData<List<OrderHistory>>()

    private fun setLoading(){
        state.value = PerformanceAnalyticState.Loading(true)
    }
    private fun hideLoading(){
        state.value = PerformanceAnalyticState.Loading(false)
    }
    private fun toast(message: String){
        state.value = PerformanceAnalyticState.ShowToast(message)
    }

    fun fetchOrderHistory(token: String, storeId: String){
        setLoading()
        historyRepo.fetchHistory(token, storeId.toInt(), object : SingleResponse<History> {
            override fun onSuccess(data: History?) {
                hideLoading()
                data?.let {
                    histories.postValue(it.store?.orderIn)
                    transformEmployee()
                }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    private fun transformEmployee(){
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                val e = mutableListOf<User>()
                histories.value?.let {
                    it.map { orderH ->
                        if(orderH.sellByUser != null && orderH.sellByUser?.id != null){
                            e.add(orderH.sellByUser!!)
                        }
                    }
                }

                val g  = e.groupBy { it.id }
                val x = hashMapOf<User, Int>()
                g.forEach { (_, u) ->
                    x.put(u[0],u.count())
                }
                employeePerformance.postValue(x)
            }
        }
    }

    fun getState() = state
    fun getEmployeePerformance() = employeePerformance
}

sealed class PerformanceAnalyticState{
    data class Loading(val isLoading: Boolean) : PerformanceAnalyticState()
    data class ShowToast(val message: String): PerformanceAnalyticState()
}