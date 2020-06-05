package com.ydhnwb.paperlessapp.repositories

import com.google.gson.GsonBuilder
import com.ydhnwb.paperlessapp.models.Order
import com.ydhnwb.paperlessapp.models.OrderSend
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.models.Customer
import com.ydhnwb.paperlessapp.webservices.ApiService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderRepository(private val api: ApiService) {
    fun confirmOrder(token: String, orderSend: OrderSend, completion: (Boolean, Error?) -> Unit){
        val g = GsonBuilder().serializeNulls().create()
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), g.toJson(orderSend))
        api.order_confirm(token, body).enqueue(object : Callback<WrappedResponse<Order>> {
            override fun onFailure(call: Call<WrappedResponse<Order>>, t: Throwable) {
                println(t.message)
                completion(false, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Order>>, response: Response<WrappedResponse<Order>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    if (b?.status!!){
                        completion(true, null)
                    }else{
                        completion(false, Error("Gagal saat membuat pesanan (${b.message}"))
                    }
                }else{
                    completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun setCustomerTarget(token: String, customer: Customer, completion: (Customer?, Error?) -> Unit) {
        if (customer.isStore){
            val id = customer.idCustomer.replace("STR", "")
            api.store_general_get(token, id).enqueue(object : Callback<WrappedResponse<Store>>{
                override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                    println(t.message)
                    completion(null, Error(t.message.toString()))
                }

                override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                    if(response.isSuccessful){
                        val x = response.body()
                        if (x!!.status){
                            customer.name = x.data?.name.toString()
                            customer.desc = x.data?.address.toString()
                            completion(customer, null)
                        }else{
                            completion(null, Error("Pengguna tidak ditemukan"))
                        }
                    }else{
                        completion(null, Error("Error ${response.message()} with status code ${response.code()}"))
                    }
                }
            })
        }else{
            val id = customer.idCustomer.replace("USR", "")
            api.user_by_id(token, id).enqueue(object: Callback<WrappedResponse<User>>{
                override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                    println(t.message)
                    completion(null, Error(t.message.toString()))
                }

                override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                    if(response.isSuccessful){
                        val x = response.body()
                        if (x!!.status){
                            customer.name = x.data?.name.toString()
                            customer.desc = x.data?.phone.toString()
                            completion(customer, null)
                        }else{
                            completion(null, Error("Pengguna tidak ditemukan"))
                        }
                    }else{
                        completion(null, Error("Error ${response.message()} with status code ${response.code()}"))
                    }
                }
            })
        }
    }
}