package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.webservices.ApiClient
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeViewModel(private val api : ApiService) : ViewModel(){
    private var state : SingleLiveEvent<EmployeeState> = SingleLiveEvent()
    private var employees = MutableLiveData<List<User>>()

    private fun setLoading(){ state.value = EmployeeState.IsLoading(true) }
    private fun hideLoading(){ state.value = EmployeeState.IsLoading(false) }
    private fun showToast(message: String) = EmployeeState.ShowToast(message)

    fun fetchAllEmployee(){
        val e = mutableListOf<User>()
        employees.postValue(e)
    }

    fun fetchEmployees(token: String, storeId : String){
        setLoading()
        api.store_employee(token, storeId).enqueue(object : Callback<WrappedListResponse<User>> {
            override fun onFailure(call: Call<WrappedListResponse<User>>, t: Throwable) {
                println(t.message)
                hideLoading()
                showToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedListResponse<User>>, response: Response<WrappedListResponse<User>>) {
                if(response.isSuccessful){
                    val body = response.body()
                    if(body?.status!!){
                        employees.postValue(body.data)
                    }
                }else{ showToast("Tidak dapat mengambil data karyawan") }
                hideLoading()
            }
        })
    }

    fun listenToEmployees() = employees
    fun listenToUIState() = state
}

sealed class EmployeeState{
    data class IsLoading(var state : Boolean) : EmployeeState()
    data class ShowToast(var message: String) : EmployeeState()
    object Reset : EmployeeState()
}