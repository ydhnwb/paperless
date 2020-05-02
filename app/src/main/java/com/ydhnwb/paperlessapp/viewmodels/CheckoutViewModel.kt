package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.webservices.ApiService

class CheckoutViewModel (private val api : ApiService) : ViewModel(){
    private var state : SingleLiveEvent<CheckoutState> = SingleLiveEvent()
    private var selectedProducts = MutableLiveData<List<Product>>().apply { postValue(mutableListOf()) }
    private var discountValue = MutableLiveData<String>()

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
        return totalPrice
    }

    fun listenToState() = state
    fun listenToDiscountValue() = discountValue
    fun listenToSelectedProduct() = selectedProducts

}

sealed class CheckoutState {
    object ResetDiscount : CheckoutState()
    data class ShowToast(var message : String) : CheckoutState()
}