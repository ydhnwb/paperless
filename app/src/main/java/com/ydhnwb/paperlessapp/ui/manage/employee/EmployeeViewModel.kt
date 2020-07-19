package com.ydhnwb.paperlessapp.ui.manage.employee

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.repositories.EmployeeRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class EmployeeViewModel (private val employeeRepository: EmployeeRepository) : ViewModel(){
    private var state : SingleLiveEvent<EmployeeState> = SingleLiveEvent()
    private var employees = MutableLiveData<List<Employee>>()

    private fun setLoading(){ state.value = EmployeeState.IsLoading(true) }
    private fun hideLoading(){ state.value = EmployeeState.IsLoading(false) }
    private fun toast(message: String) = EmployeeState.ShowToast(message)
    private fun successDelete() { state.value = EmployeeState.SuccessDelete }

    fun fetchEmployees(token: String, storeId: String){
        setLoading()
        employeeRepository.getEmployees(token, storeId, object: ArrayResponse<Employee>{
            override fun onSuccess(datas: List<Employee>?) {
                hideLoading()
                datas?.let { employees.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun removeEmployee(token: String, storeId: String, employeeId: String){
        setLoading()
        employeeRepository.removeEmployee(token, storeId, employeeId, object: SingleResponse<Store>{
            override fun onSuccess(data: Store?) {
                hideLoading()
                data?.let { successDelete() }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
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