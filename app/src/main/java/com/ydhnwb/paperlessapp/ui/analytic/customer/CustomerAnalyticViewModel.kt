package com.ydhnwb.paperlessapp.ui.analytic.customer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.OrderHistory
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.repositories.HistoryRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerAnalyticViewModel(private val historyRepo: HistoryRepository) : ViewModel(){
    private val state : SingleLiveEvent<CustomerAnalyticFragmentState> = SingleLiveEvent()
    private val histories = MutableLiveData<List<OrderHistory>>()
    private val storesWhoBuy = MutableLiveData<HashMap<Store, Int>>()
    private val usersWhoBuy = MutableLiveData<HashMap<User, Int>>()

    private fun setLoading(){ state.value = CustomerAnalyticFragmentState.IsLoading(true) }
    private fun hideLoading(){ state.value = CustomerAnalyticFragmentState.IsLoading(false) }
    private fun toast(m : String){ state.value = CustomerAnalyticFragmentState.ShowToast(m) }

    fun fetchStoreInfo(token: String, storeId: String){
        setLoading()
        historyRepo.fetchHistory(token, storeId.toInt()){ s, e ->
            hideLoading()
            e?.let { it.message?.let { m -> toast(m) } }
            s?.let {
                hideLoading()
                val orders = it.store?.orderIn
                histories.postValue(orders)
                transformBuyerData()
                transformUser()
            }
        }
    }

    private fun transformBuyerData(){
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                val storeBuyers = mutableListOf<Store>()
                histories.value?.let {
                    it.map { orderH ->
                        if(orderH.boughtByStore != null && orderH.boughtByStore?.id != null){
                            storeBuyers.add(orderH.boughtByStore!!)
                            println("Store logo ${orderH.boughtByStore!!.store_logo}")
                        }
                    }
                }

                val g  = storeBuyers.groupBy { it.id }
                val x = hashMapOf<Store, Int>()
                g.forEach { (_, u) ->
                    x.put(u[0],u.count())
                }
                storesWhoBuy.postValue(x)
            }
        }
    }

    private fun transformUser(){
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                val userBuyer = mutableListOf<User>()
                histories.value?.let {
                    it.map { orderH ->
                        if(orderH.boughtByUser != null && orderH.boughtByUser?.id != null){
                            userBuyer.add(orderH.boughtByUser!!)
                            println("User ${orderH.boughtByUser!!.name}")
                        }
                    }
                }

                val t = userBuyer.groupBy { it.id }
                val j = hashMapOf<User, Int>()
                t.forEach { (_, v) -> j.put(v[0], v.count()) }
                usersWhoBuy.postValue(j)
            }
        }
    }

    fun listenToState() = state
    fun listenStoreWhoBuy() = storesWhoBuy
    fun listenToUserWhoBuy() = usersWhoBuy
}

sealed class CustomerAnalyticFragmentState{
    data class IsLoading(val state: Boolean) : CustomerAnalyticFragmentState()
    data class ShowToast(val message: String): CustomerAnalyticFragmentState()
}