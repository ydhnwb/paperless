package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.EmployeeResponse
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface EmployeeContract {
    fun getEmployees(token: String, storeId: String, listener: SingleResponse<EmployeeResponse>)
    fun removeEmployee(token: String, storeId: String, employeeId: String, listener: SingleResponse<Store>)
}

class EmployeeRepository (private val api: ApiService) : EmployeeContract{

    override fun getEmployees(token: String, storeId: String, listener: SingleResponse<EmployeeResponse>) {
        api.store_employee(token, storeId).enqueue(object:
            Callback<WrappedResponse<EmployeeResponse>> {
            override fun onFailure(call: Call<WrappedResponse<EmployeeResponse>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<EmployeeResponse>>, response: Response<WrappedResponse<EmployeeResponse>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun removeEmployee(token: String, storeId: String, employeeId: String, listener: SingleResponse<Store>) {
        api.employee_remove(token, storeId, employeeId).enqueue(object: Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println(t.message)
                listener.onFailure(Error(t.message))
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    if(b!!.status){
                        listener.onSuccess(b.data)
                    }else{
                        listener.onFailure(Error(b.message))
                    }
                }else{
                    listener.onFailure(Error(response.message()))
                }
            }
        })
    }
}