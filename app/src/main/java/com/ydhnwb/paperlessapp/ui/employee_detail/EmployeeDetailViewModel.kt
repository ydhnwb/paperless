package com.ydhnwb.paperlessapp.ui.employee_detail

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.repositories.EmployeeRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class EmployeeDetailViewModel(private val employeeRepository: EmployeeRepository) : ViewModel(){
    private val state : SingleLiveEvent<EmployeeDetailState> = SingleLiveEvent()

    private fun setLoading(){
        state.value = EmployeeDetailState.Loading(true)
    }

    private fun hideLoading(){
        state.value = EmployeeDetailState.Loading(false)
    }

    private fun toast(message: String){
        state.value = EmployeeDetailState.ShowToast(message)
    }

    private fun success(){
        state.value = EmployeeDetailState.Success
    }

    fun updateRoleEmployee(token: String, storeId : String, role : Boolean, employeeId : Int){
        setLoading()
        employeeRepository.updateEmployeeRole(token, storeId, role, employeeId, object : SingleResponse<Employee>{
            override fun onSuccess(data: Employee?) {
                hideLoading()
                data?.let {
                    success()
                }
            }

            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun getState() = state
}

sealed class EmployeeDetailState {
    data class Loading(val isLoading : Boolean) : EmployeeDetailState()
    data class ShowToast(val message: String) : EmployeeDetailState()
    object Success : EmployeeDetailState()
}