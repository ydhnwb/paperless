package com.ydhnwb.paperlessapp.ui.checkout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.OrderSend
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.repositories.OrderRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.models.Customer
import com.ydhnwb.paperlessapp.models.Order
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class CheckoutViewModel (private val orderRepository: OrderRepository) : ViewModel(){
    private val state : SingleLiveEvent<CheckoutState> = SingleLiveEvent()
    private var selectedProducts = MutableLiveData<List<Product>>().apply { postValue(mutableListOf()) }
    private var discountValue = MutableLiveData<String>()
    private var currentCustomer = MutableLiveData<Customer?>()

    private fun setLoading(){ state.value = CheckoutState.IsLoading(true) }
    private fun hideLoading(){ state.value = CheckoutState.IsLoading(false) }
    private fun toast(message: String){ state.value = CheckoutState.ShowToast(message) }
    private fun alert(message: String){ state.value = CheckoutState.ShowAlert(message) }
    private fun success() { state.value = CheckoutState.Success }

    fun setSelectedProducts(products: List<Product>){ selectedProducts.postValue(products) }
    fun setDiscountValue(value : String){ discountValue.value = value }
    fun deleteCustomer() = currentCustomer.postValue(null)

    private fun getDiscountValueOnly() : Int{
        if(discountValue.value != null && discountValue.value!!.isNotEmpty()){
            val discountAmount = discountValue.value!!.toIntOrNull()
            if (discountAmount != null){
                return discountAmount
            }
        }
        return 0
    }

    fun setCustomerTarget(token: String, customer: Customer) {
        currentCustomer.postValue(customer)
        orderRepository.setCustomerTarget(token, customer, object : SingleResponse<Customer>{
            override fun onSuccess(data: Customer?) {
                hideLoading()
                data?.let { currentCustomer.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
                currentCustomer.value = null
            }
        })
    }

    fun createOrder(token: String, orderSend: OrderSend){
        setLoading()
        orderSend.discountInPrice = getDiscountValueOnly()
        orderRepository.confirmOrder(token, orderSend, object: SingleResponse<Order>{
            override fun onSuccess(data: Order?) {
                hideLoading()
                data?.let { success() }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { alert(it) }
            }

        })
    }

    fun calculateTotalPrice(): Int {
        val totalPrice = selectedProducts.value!!.sumBy { product ->
            val temp = product.price!! * product.selectedQuantity!!
            if(product.discountByPercent != null){
                temp - (temp * product.discountByPercent!! / 100).toInt()
            }else{
                temp
            }
        }
        if(discountValue.value != null && discountValue.value!!.isNotEmpty()){
            if(discountValue.value!!.toString().substring(0,1) == "0"){
                state.value = CheckoutState.ResetDiscount
                alert("Diskon tidak bisa diawali dengan nol")
                return totalPrice
            }else{
                val discountAmount = discountValue.value!!.toIntOrNull()
                if (discountAmount != null){
                    if (discountAmount > totalPrice){
                        state.value = CheckoutState.ResetDiscount
                        alert("Diskon tidak boleh melebihi harga pembelian")
                    }else{
                        return totalPrice - discountAmount
                    }
                }else{
                    state.value = CheckoutState.ResetDiscount
                    alert("Diskon tidak valid")
                    return totalPrice
                }
            }
        }
        state.value = CheckoutState.ResetDiscount
        return totalPrice
    }

    fun listenToUIState() = state
    fun listenToDiscountValue() = discountValue
    fun listenToSelectedProduct() = selectedProducts
    fun listenToCurrentCustomer() = currentCustomer
}

sealed class CheckoutState {
    data class IsLoading(var state : Boolean) : CheckoutState()
    data class ShowToast(var message: String) : CheckoutState()
    data class ShowAlert(var message: String) : CheckoutState()
    object Success :CheckoutState()
    object ResetDiscount : CheckoutState()
}