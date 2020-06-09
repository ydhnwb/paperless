package com.ydhnwb.paperlessapp.fragments.analytic.selling

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.OrderHistory
import com.ydhnwb.paperlessapp.models.OrderHistoryDetail
import com.ydhnwb.paperlessapp.repositories.HistoryRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class SellingAnalyticViewModel (private val historyRepository: HistoryRepository) : ViewModel(){
    private val sellingHistory = MutableLiveData<List<OrderHistory>>()
    private val state: SingleLiveEvent<SellingAnalyticState> = SingleLiveEvent()
    private val sellingProductCluster = MutableLiveData<HashMap<String, Int>>()
    private val sellingByHour = MutableLiveData<HashMap<Int, Int>>()

    private fun setLoading() { state.value = SellingAnalyticState.IsLoading(true) }
    private fun hideLoading() { state.value = SellingAnalyticState.IsLoading(false) }
    private fun toast(message: String){ state.value = SellingAnalyticState.ShowToast(message) }

    fun fetchStoreInfo(token: String, storeId: String){
        setLoading()
        historyRepository.fetchHistory(token, storeId.toInt()){ s, e ->
            hideLoading()
            e?.let { it.message?.let { m -> toast(m) } }
            s?.let {
                hideLoading()
                val orders = it.store?.orderIn
                sellingHistory.postValue(orders)
                fetchSellingProducts()
                transformTransactionByHour()
            }
        }
    }

    private fun fetchSellingProducts() {
        GlobalScope.launch {
            withContext(Dispatchers.IO){
                val pivot = mutableListOf<OrderHistoryDetail>()
                sellingHistory.value?.let { temp -> temp.map { history -> history.orderDetails.map { pivot.add(it) } } }
                val h = hashMapOf<String, Int>()
                pivot.map {
                    if (h.containsKey(it.productName)){
                        val currentQuantity = h.get(it.productName)
                        h.put(it.productName!!, currentQuantity!! + it.quantity!!)
                    }else{
                        h.put(it.productName!!, it.quantity!!)
                    }
                }
                sellingProductCluster.postValue(h)
            }
        }
    }

    private fun transformTransactionByHour(){
        GlobalScope.launch {
            withContext(Dispatchers.IO){
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
                val cal = Calendar.getInstance()
                val pivot = hashMapOf<Int, Int>()
                sellingHistory.value?.let {
                    it.map { orderHistory ->  
                        val date = sdf.parse(orderHistory.datetime!!)
                        cal.time = date!!
                        if(pivot.containsKey(cal.get(Calendar.HOUR_OF_DAY))){
                            var i = pivot[cal.get(Calendar.HOUR_OF_DAY)]!!
                            pivot.put(cal.get(Calendar.HOUR_OF_DAY), ++i)
                        }else{
                            pivot.put(cal.get(Calendar.HOUR_OF_DAY), 1)
                        }
                    }
                }
                sellingByHour.postValue(pivot)
            }
        }
    }


    fun listenToUIState() = state
    fun listenToHistory() = sellingHistory
    fun listenToSellingProductCluster() = sellingProductCluster
    fun listenToSellingByHour() = sellingByHour
}

sealed class SellingAnalyticState {
    data class IsLoading(val state: Boolean) : SellingAnalyticState()
    data class ShowToast(val message: String): SellingAnalyticState()
}