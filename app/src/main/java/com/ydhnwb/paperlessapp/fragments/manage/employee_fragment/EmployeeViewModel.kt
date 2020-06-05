package com.ydhnwb.paperlessapp.fragments.manage.employee_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.repositories.EmployeeRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class EmployeeViewModel (private val employeeRepository: EmployeeRepository) : ViewModel(){
    private var state : SingleLiveEvent<EmployeeState> = SingleLiveEvent()
    private var employees = MutableLiveData<List<Employee>>()

    private fun setLoading(){ state.value = EmployeeState.IsLoading(true) }
    private fun hideLoading(){ state.value = EmployeeState.IsLoading(false) }
    private fun toast(message: String) = EmployeeState.ShowToast(message)
    private fun successDelete() { state.value = EmployeeState.SuccessDelete }

    fun fetchEmployees(token: String, storeId: String){
        setLoading()
        employeeRepository.getEmployees(token, storeId){ employeeResponse, error ->
            hideLoading()
            error?.let { it.message?.let { m -> toast(m) } }
            employeeResponse?.let {
                employees.postValue(it.employees)
            }
        }
    }

    fun removeEmployee(token: String, storeId: String, employeeId: String){
        setLoading()
        employeeRepository.removeEmployee(token, storeId, employeeId){ b, e ->
            hideLoading()
            e?.let { it.message?.let { m -> toast(m) } }
            if(b){
                successDelete()
            }
        }
    }

    fun listenToUIState() = state
    fun listenToEmployees() = employees

}
sealed class EmployeeState{
    object SuccessDelete : EmployeeState()
    data class IsLoading(var state : Boolean) : EmployeeState()
    data class ShowToast(var message: String) : EmployeeState()
    object Reset : EmployeeState()
}