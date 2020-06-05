package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.EmployeeResponse
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
                        }
                    }
                }else{
                    completion(null, Error("Error ${response.errorBody()} with status code ${response.code()}"))
                }
            }
        })
    }
}