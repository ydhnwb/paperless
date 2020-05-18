package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.models.EmployeeResponse
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeViewModel(private val api : ApiService) : ViewModel(){
    private var state : SingleLiveEvent<EmployeeState> = SingleLiveEvent()
    private var employees = MutableLiveData<List<Employee>>()

    private fun setLoading(){ state.value = EmployeeState.IsLoading(true) }
    private fun hideLoading(){ state.value = EmployeeState.IsLoading(false) }
    private fun showToast(message: String) = EmployeeState.ShowToast(message)

    fun fetchEmployees(token: String, storeId : String){
        setLoading()
        api.store_employee(token, storeId).enqueue(object: Callback<WrappedResponse<EmployeeResponse>> {
            override fun onFailure(call: Call<WrappedResponse<EmployeeResponse>>, t: Throwable) {
                println(t.message)
                showToast(t.message.toString())
                hideLoading()
            }

            override fun onResponse(call: Call<WrappedResponse<EmployeeResponse>>, response: Response<WrappedResponse<EmployeeResponse>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let { x -> x.data?.let { d -> employees.postValue(d.employees) } }
                }
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