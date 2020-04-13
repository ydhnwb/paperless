package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.webservices.ApiClient
import com.ydhnwb.paperlessapp.webservices.ApiService

class EmployeeViewModel(private val api : ApiService) : ViewModel(){
    private var state : SingleLiveEvent<EmployeeState> = SingleLiveEvent()
    private var employees = MutableLiveData<List<Employee>>()

    fun fetchAllEmployee(){
        val e = mutableListOf<Employee>().apply {
            add(Employee(1, "Ardhan Faiz Kautsar", "KASIR"))
            add(Employee(2, "Fanni Naditya Putra", "STAFF"))
            add(Employee(3, "Afif Maulana Iskandar", "STAFF"))
        }
        employees.postValue(e)
    }
    fun listenToEmployees() = employees
    fun listenToUIState() = state
}

sealed class EmployeeState{
    data class IsLoading(var state : Boolean) : EmployeeState()
    data class ShowToast(var message: String) : EmployeeState()
    object Reset : EmployeeState()
}