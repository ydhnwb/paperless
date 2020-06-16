package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.EmployeeResponse
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeRepository (private val api: ApiService){
    fun getEmployees(token: String, storeId : String, completion: (EmployeeResponse?, Error?) -> Unit){
        api.store_employee(token, storeId).enqueue(object:
            Callback<WrappedResponse<EmployeeResponse>> {
            override fun onFailure(call: Call<WrappedResponse<EmployeeResponse>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<EmployeeResponse>>, response: Response<WrappedResponse<EmployeeResponse>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if(it.status){
                            completion(it.data!!, null)
                        }else{
                            completion(null, Error())
                        }
                    }
                }else{
                    completion(null, Error("Error ${response.errorBody()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun removeEmployee(token: String, storeId: String, employeeId: String, completion: (Boolean, Error?) -> Unit){
        api.employee_remove(token, storeId, employeeId).enqueue(object: Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println(t.message)
                completion(false, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    if(b!!.status){
                        completion(true, null)
                    }else{
                        completion(false, Error("Tidak dapat menghapus karyawan"))
                    }
                }else{
                    completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }

        })
    }
}