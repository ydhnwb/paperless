package com.ydhnwb.paperlessapp.fragments.analytic.selling

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.OrderHistory
import com.ydhnwb.paperlessapp.models.OrderHistoryDetail
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

class SellingAnalyticViewModel (private val historyRepository: HistoryRepository) : ViewModel(){
    private val sellingHistory = MutableLiveData<List<OrderHistory>>()
    private val state: SingleLiveEvent<SellingAnalyticState> = SingleLiveEvent()
    private val sellingProductCluster = MutableLiveData<HashMap<String, Int>>()
    private val sellingByHour = MutableLiveData<HashMap<Int, Int>>()
    private val sellingByMonth = MutableLiveData<HashMap<String, Int>>()
    private val sellingProfitByMonth = MutableLiveData<HashMap<String, Int>>()

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
                transformTransactionByMonth()
                transformTransactionByMonth()
                transformProfitByMonth()
            }
        }
    }

    private fun fetchSellingProducts() {
        GlobalScope.launch {
            withContext(Dispatchers.Main){
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
            withContext(Dispatchers.Main){
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
                    sellingByHour.postValue(pivot)
                }
            }
        }
    }

    private fun transformProfitByMonth(){
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                val listOfOrder = mutableListOf<OrderHistory>()
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
                val cal = Calendar.getInstance()
                val pivot = mutableListOf<ProfitByMonth>()
                val pivot2 = hashMapOf<String, Int>()
                sellingHistory.value?.let {
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
                        val sumPrice = it.orderDetails.sumBy { orderDetail -> orderDetail.productPrice!! }
                        pivot.add(ProfitByMonth(concated, sumPrice))
                    }
                    for( o in pivot){
                        if (pivot2.containsKey(o.month)){
                            val r = pivot2.get(o.month)!!
                            pivot2.put(o.month, r+o.profit!!)
                        }else{
                            pivot2.put(o.month, o.profit!!)
                        }
                    }
                    sellingProfitByMonth.postValue(pivot2)
                }
            }
        }
    }


    private fun transformTransactionByMonth(){
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                val listOfdate = mutableListOf<Date>()
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
                val cal = Calendar.getInstance()
                val pivot = hashMapOf<String, Int>()
                sellingHistory.value?.let {
                    it.map { orderHistory ->
                        val date = sdf.parse(orderHistory.datetime!!)
                        date?.let { d ->
                            listOfdate.add(d)
                        }
                    }
                    listOfdate.sortDescending()
                    listOfdate.forEach { date ->
                        cal.time = date
                        val year = cal.get(Calendar.YEAR)
                        val month = PaperlessUtil.getMonthByMonthInt(cal.get(Calendar.MONTH)+1)
                        val concated = "$month $year"
                        if(pivot.containsKey(concated)){
                            var i = pivot[concated]!!
                            pivot.put(concated, ++i)
                        }else{
                            pivot.put(concated, 1)
                        }
                    }
                    sellingByMonth.postValue(pivot)
                }
            }
        }
    }


    fun listenToUIState() = state
    fun listenToHistory() = sellingHistory
    fun listenToSellingProductCluster() = sellingProductCluster
    fun listenToSellingByHour() = sellingByHour
    fun listenToSellingByMonth() = sellingByMonth
    fun listenToSellingByProfit() = sellingProfitByMonth
}

sealed class SellingAnalyticState {
    data class IsLoading(val state: Boolean) : SellingAnalyticState()
    data class ShowToast(val message: String): SellingAnalyticState()
}