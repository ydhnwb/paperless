package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Transaction
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionViewModel(private val api : ApiService) : ViewModel(){
    private var transactions = MutableLiveData<List<Transaction>>()
    private var state : SingleLiveEvent<TransactionState> = SingleLiveEvent()

    private fun setLoading(){ state.value = TransactionState.IsLoading(true) }
    private fun hideLoading(){ state.value = TransactionState.IsLoading(false) }
    private fun showToast(message: String) { state.value = TransactionState.ShowToast(message) }

    fun allTransaction(token: String, storeId : String){
        setLoading()
        api.transaction_store(token, storeId).enqueue(object : Callback<WrappedListResponse<Transaction>>{
            override fun onFailure(call: Call<WrappedListResponse<Transaction>>, t: Throwable) {
                println(t.message)
                showToast(t.message.toString())
                hideLoading()
            }

            override fun onResponse(call: Call<WrappedListResponse<Transaction>>, response: Response<WrappedListResponse<Transaction>>) {
                if(response.isSuccessful){
                    transactions.postValue(response.body()!!.data)
                }else{
                    showToast("Kesalahan saat mengambil data transaksi")
                }
                hideLoading()
            }
        })
    }

    fun listenToState() = state
    fun listenToTransactions() = transactions
}

sealed class TransactionState {
    data class IsLoading(var state : Boolean) : TransactionState()
    data class ShowToast(var message : String) : TransactionState()
}