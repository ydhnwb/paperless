package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ydhnwb.paperlessapp.models.Order
import com.ydhnwb.paperlessapp.models.OrderSend
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckoutViewModel (private val api : ApiService) : ViewModel(){
    private var state : SingleLiveEvent<CheckoutState> = SingleLiveEvent()
    private var selectedProducts = MutableLiveData<List<Product>>().apply { postValue(mutableListOf()) }
    private var discountValue = MutableLiveData<String>()
    private var currentCustomer = MutableLiveData<Customer?>()

    fun setSelectedProducts(products: List<Product>){ selectedProducts.postValue(products) }

    fun setDiscountValue(value : String){ discountValue.value = value }

    fun calculateTotalPrice(): Int {
        val totalPrice = selectedProducts.value!!.sumBy { product -> product.price!! * product.selectedQuantity!! }
        if(discountValue.value != null && discountValue.value!!.isNotEmpty()){
            val discountAmount = discountValue.value!!.toIntOrNull()
            if (discountAmount != null){
                if (discountAmount > totalPrice){
                    state.value = CheckoutState.ResetDiscount
                    state.value = CheckoutState.ShowToast("Diskon tidak boleh melebihi harga pembelian")
                }else{
                    return totalPrice - discountAmount
                }
            }
            state.value = CheckoutState.ResetDiscount
            state.value = CheckoutState.ShowToast("Diskon tidak valid")
            return totalPrice
        }
        state.value = CheckoutState.ResetDiscount
        return totalPrice
    }

    private fun getDiscountValueOnly() : Int{
        if(discountValue.value != null && discountValue.value!!.isNotEmpty()){
            val discountAmount = discountValue.value!!.toIntOrNull()
            if (discountAmount != null){
                return discountAmount
            }
            return 0
        }
        return 0
    }

    fun setCustomerTarget(token: String, customer: Customer) {
        currentCustomer.postValue(customer)
        if (customer.isStore){
            val id = customer.idCustomer.replace("STR", "")
            api.store_general_get(token, id).enqueue(object : Callback<WrappedResponse<Store>>{
                override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) { println(t.message) }

                override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                    if(response.isSuccessful){
                        val x = response.body()
                        if (x!!.status){
                            customer.name = x.data?.name.toString()
                            customer.desc = x.data?.address.toString()
                            currentCustomer.postValue(customer)
                        }
                    }else{
                        println(response.message())
                        println(response.code())
                    }
                }
            })
        }else{
            customer.name = "User ${customer.idCustomer}"
            customer.desc = "${customer.isStore}"
            currentCustomer.postValue(customer)
        }
    }

    fun confirmOrder(token: String, orderSend: OrderSend){
        state.value = CheckoutState.IsLoading(true)
        orderSend.discountInPrice = getDiscountValueOnly()
        val g = GsonBuilder().serializeNulls().create()
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), g.toJson(orderSend))
        println(body)
        api.order_confirm(token, body).enqueue(object : Callback<WrappedResponse<Order>>{
            override fun onFailure(call: Call<WrappedResponse<Order>>, t: Throwable) {
                println(t.message)
                state.value = CheckoutState.IsLoading(false)
                state.value = CheckoutState.ShowToast(t.message.toString())
                state.value = CheckoutState.Alert(AlertType.EXCEPTION, message = t.message.toString())
            }

            override fun onResponse(call: Call<WrappedResponse<Order>>, response: Response<WrappedResponse<Order>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    if (b?.status!!){
                        state.value = CheckoutState.Success
                    }else{
                        println(b.message)
                        state.value = CheckoutState.Alert(AlertType.FAILED)
                    }
                }else{
                    println(response.code())
                    state.value = CheckoutState.Alert(AlertType.FAILED_BY_CODE)
                }
            }
        })
    }

    fun deleteCustomer() = currentCustomer.postValue(null)

    fun listenToState() = state
    fun listenToDiscountValue() = discountValue
    fun listenToSelectedProduct() = selectedProducts
    fun listenToCurrentCustomer() = currentCustomer

}

sealed class CheckoutState {
    object Success : CheckoutState()
    object ResetDiscount : CheckoutState()
    data class IsLoading(var state : Boolean) : CheckoutState()
    data class ShowToast(var message : String) : CheckoutState()

    data class Alert(var alertType : AlertType, var message: String? = null) : CheckoutState()

}

data class Customer(
    var idCustomer : String,
    var isStore : Boolean,
    var name : String = "",
    var desc : String = ""
)

enum class AlertType {
    FAILED, FAILED_BY_CODE,EXCEPTION
}