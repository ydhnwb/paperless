package com.ydhnwb.paperlessapp.repositories

import com.google.gson.GsonBuilder
import com.ydhnwb.paperlessapp.models.Order
import com.ydhnwb.paperlessapp.models.OrderSend
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.models.Customer
import com.ydhnwb.paperlessapp.utilities.SingleResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import com.ydhnwb.paperlessapp.webservices.UrlRes
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface OrderContract {
    fun downloadInvoice(token: String, orderId: String, listener: SingleResponse<String>)
    fun setCustomerTarget(token: String, customer: Customer, listener: SingleResponse<Customer>)
    fun confirmOrder(token: String, orderSend: OrderSend, listener: SingleResponse<Order>)
}

class OrderRepository(private val api: ApiService) : OrderContract {

    override fun downloadInvoice(token: String, orderId: String, listener: SingleResponse<String>) {
        api.download_invoice(token, orderId).enqueue(object: Callback<WrappedResponse<UrlRes>>{
            override fun onFailure(call: Call<WrappedResponse<UrlRes>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<UrlRes>>, response: Response<WrappedResponse<UrlRes>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body()
                        println(b!!.data!!.url.toString())
                        if(b!!.status) listener.onSuccess(b.data!!.url) else listener.onFailure(Error(b.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun setCustomerTarget(token: String, customer: Customer, listener: SingleResponse<Customer>) {
        if (customer.isStore){
            val id = customer.idCustomer.replace("STR", "")
            api.store_general_get(token, id).enqueue(object : Callback<WrappedResponse<Store>>{
                override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) = listener.onFailure(Error(t.message))

                override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                    when{
                        response.isSuccessful -> {
                            val b = response.body()
                            if(b!!.status){
                                customer.name = b.data?.name.toString()
                                customer.desc = b.data?.address.toString()
                                listener.onSuccess(customer)
                            }else{ listener.onFailure(Error(b.message)) }
                        }
                        else -> listener.onFailure(Error(response.message()))
                    }
                }
            })
        }else{
            val id = customer.idCustomer.replace("USR", "")
            api.user_by_id(token, id).enqueue(object: Callback<WrappedResponse<User>>{
                override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) = listener.onFailure(Error(t.message))

                override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                    if(response.isSuccessful){
                        val x = response.body()
                        if (x!!.status){
                            customer.name = x.data?.name.toString()
                            customer.desc = x.data?.phone.toString()
                            listener.onSuccess(customer)
                        }else{
                            listener.onFailure(Error(x.message))
                        }
                    }else{
                        listener.onFailure(Error(response.message()))
                    }
                }
            })
        }
    }

    override fun confirmOrder(token: String, orderSend: OrderSend, listener: SingleResponse<Order>) {
        val g = GsonBuilder().create()
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), g.toJson(orderSend))
        println(g.toJson(orderSend))
        api.order_confirm(token, body).enqueue(object : Callback<WrappedResponse<Order>> {
            override fun onFailure(call: Call<WrappedResponse<Order>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Order>>, response: Response<WrappedResponse<Order>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body()
                        if(b!!.status) listener.onSuccess(b.data) else listener.onFailure(Error(b.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }
}