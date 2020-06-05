package com.ydhnwb.paperlessapp.activities.checkout_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.OrderSend
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.repositories.OrderRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.models.Customer

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
    private fun failed(){ state.value = CheckoutState.Failed }

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
        orderRepository.setCustomerTarget(token, customer){ resultCustomer, e ->
            hideLoading()
            e?.let { it.message?.let { m -> toast(m) } }
            resultCustomer?.let {
                currentCustomer.postValue(it)
            }
        }
    }

    fun createOrder(token: String, orderSend: OrderSend){
        setLoading()
        orderSend.discountInPrice = getDiscountValueOnly()
        orderRepository.confirmOrder(token, orderSend){ resultBool, e ->
            hideLoading()
            e?.let { it.message?.let { m -> alert(m) } }
            if(resultBool){
                success()
            }
        }
    }

    fun calculateTotalPrice(): Int {
        val totalPrice = selectedProducts.value!!.sumBy { product -> product.price!! * product.selectedQuantity!! }
        if(discountValue.value != null && discountValue.value!!.isNotEmpty()){
            val discountAmount = discountValue.value!!.toIntOrNull()
            if (discountAmount != null){
                if (discountAmount > totalPrice){
                    state.value = CheckoutState.ResetDiscount
                    alert("Diskon tidak boleh melebihi harga pembelian")
                }else{
                    return totalPrice - discountAmount
                }
            }
            state.value = CheckoutState.ResetDiscount
            alert("Diskon tidak valid")
            return totalPrice
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
    object Failed : CheckoutState()
    object Success :CheckoutState()
    object ResetDiscount : CheckoutState()
}