package com.ydhnwb.paperlessapp.ui.analytic.purchasement

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.OrderHistory
import com.ydhnwb.paperlessapp.models.ProfitByMonth
import com.ydhnwb.paperlessapp.repositories.HistoryRepository
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PurchasementFragmentViewModel (private val historyRepository: HistoryRepository) : ViewModel(){
    private val state : SingleLiveEvent<PurchasementFragmentState> = SingleLiveEvent()
    private val history = MutableLiveData<List<OrderHistory>>()

    private val spentByMonth = MutableLiveData<HashMap<String, Int>>()

    private fun setLoading() { state.value = PurchasementFragmentState.IsLoading(true) }
    private fun hideLoading() { state.value = PurchasementFragmentState.IsLoading(false) }
    private fun toast(message: String){ state.value = PurchasementFragmentState.ShowToast(message) }

    fun fetchStoreInfo(token: String, storeId: String){
        setLoading()
        historyRepository.fetchHistory(token, storeId.toInt()){ s, e ->
            hideLoading()
            e?.let { it.message?.let { m -> toast(m) } }
            s?.let {
                hideLoading()
                val orders = it.store?.orderOut
                history.postValue(orders)
                transformSpentByMonth()
            }
        }
    }

    fun transformSpentByMonth(){
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                val listOfOrder = mutableListOf<OrderHistory>()
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
                val cal = Calendar.getInstance()
                val pivot = mutableListOf<ProfitByMonth>()
                val pivot2 = hashMapOf<String, Int>()
                history.value?.let {
                    it.map { orderHistory ->
                        val date = sdf.parse(orderHistory.datetime!!)
                        orderHistory.date = date
                        listOfOrder.add(orderHistory)
                    }

                    listOfOrder.map {
                        cal.time = it.date!!
                        val year = cal.get(Calendar.YEAR)
                        val month = PaperlessUtil.getMonthByMonthInt(cal.get(Calendar.MONTH)+1)
                        val concated = "$month $year"
                        val sumPrice = it.orderDetails.sumBy { orderDetail -> orderDetail.productPrice!! * orderDetail.quantity!! }
                        pivot.add(ProfitByMonth(concated, sumPrice))
                    }
                    for(o in pivot){
                        if (pivot2.containsKey(o.month)){
                            val r = pivot2.get(o.month)!!
                            pivot2.put(o.month, r+o.profit!!)
                        }else{
                            pivot2.put(o.month, o.profit!!)
                        }
                    }
                    spentByMonth.postValue(pivot2)
                }
            }
        }
    }

    fun listenToState() = state
    fun listenToSpentByMonth() = spentByMonth
}

sealed class PurchasementFragmentState{
    data class IsLoading(val state: Boolean) : PurchasementFragmentState()
    data class ShowToast(val message : String) : PurchasementFragmentState()
}