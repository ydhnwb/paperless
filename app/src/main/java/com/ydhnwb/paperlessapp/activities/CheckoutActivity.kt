package com.ydhnwb.paperlessapp.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.OrderSend
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.ProductSend
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.CheckoutState
import com.ydhnwb.paperlessapp.viewmodels.CheckoutViewModel
import com.ydhnwb.paperlessapp.viewmodels.OrderState
import com.ydhnwb.paperlessapp.viewmodels.OrderViewModel
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.content_checkout.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutActivity : AppCompatActivity() {
    private val checkoutViewModel : CheckoutViewModel by viewModel()
    private val orderViewModel: OrderViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        listenerDiscount()
        behaviorDiscount()
        watcherDiscount()
        orderViewModel.listenToState().observer(this, Observer { handleOrderState(it) })
        checkoutViewModel.listenToState().observer(this, Observer { handleUIState(it) })
        checkoutViewModel.listenToDiscountValue().observe(this, Observer { handleDiscountValue(it) })
        checkoutViewModel.listenToSelectedProduct().observe(this, Observer { calculateTotalPrice() })
        fill()
        confirmOrder()
    }


    private fun fill() = getSelectedProducts()?.let { checkoutViewModel.setSelectedProducts(it) }

    private fun listenerDiscount(){
        checkout_switch_discount.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                checkout_layout_discount.visibility = View.VISIBLE
            }else{
                et_discount.text?.clear()
                checkout_layout_discount.visibility = View.GONE
            }
        }
    }

    private fun behaviorDiscount() = checkout_btn_discount.setOnClickListener {
        checkout_switch_discount.isChecked = !checkout_switch_discount.isChecked
    }

    private fun watcherDiscount(){
        et_discount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (checkout_switch_discount.isChecked){
                    checkoutViewModel.setDiscountValue(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun confirmOrder(){
        checkout_cash.setOnClickListener {
            val productsToSend : List<ProductSend> = getSelectedProducts()!!.map{ p ->
                ProductSend(id = p.id!!, price = p.price!!, quantity = p.selectedQuantity!!)
            }.toList()
            //3 is izzatur royhan for example
            val orderSend = OrderSend(userId = 3, storeId = getParentStore().id, products = productsToSend)
            orderViewModel.confirmOrder(PaperlessUtil.getToken(this@CheckoutActivity), orderSend)
        }
    }

    private fun handleUIState(it: CheckoutState){
        when(it){
            is CheckoutState.ShowToast -> toast(it.message)
            is CheckoutState.ResetDiscount -> {
                if (checkout_switch_discount.isChecked){
                    et_discount.text?.clear()
                }
            }
        }
    }

    private fun handleOrderState(it: OrderState){
        when(it){
            is OrderState.Success -> finish()
            is OrderState.ShowToast -> toast(it.message)
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    private fun handleDiscountValue(it: String){ calculateTotalPrice() }
    private fun calculateTotalPrice(){ checkout_total_price.text = PaperlessUtil.setToIDR(checkoutViewModel.calculateTotalPrice()) }
    private fun getSelectedProducts() : List<Product>? = intent.getParcelableArrayListExtra("PRODUCT")
    private fun getParentStore() : Store = intent.getParcelableExtra("STORE")!!
}
