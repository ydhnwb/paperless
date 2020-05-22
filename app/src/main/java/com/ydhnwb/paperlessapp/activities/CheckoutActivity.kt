package com.ydhnwb.paperlessapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.OrderSend
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.ProductSend
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.*
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.content_checkout.*
import kotlinx.android.synthetic.main.content_checkout.customer_desc
import kotlinx.android.synthetic.main.content_checkout.customer_name
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutActivity : AppCompatActivity() {
    private val checkoutViewModel : CheckoutViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        listenerDiscount()
        behaviorDiscount()
        watcherDiscount()
        checkoutViewModel.listenToState().observer(this, Observer { handleUIState(it) })
        checkoutViewModel.listenToDiscountValue().observe(this, Observer { handleDiscountValue(it) })
        checkoutViewModel.listenToSelectedProduct().observe(this, Observer { calculateTotalPrice() })
        checkoutViewModel.listenToCurrentCustomer().observe(this, Observer { handleCustomerChange(it) })
        fill()
        confirmOrder()
        selectCustomer()
        customerViewBehavior()
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

    private fun behaviorDiscount() = checkout_btn_discount.setOnClickListener { checkout_switch_discount.isChecked = !checkout_switch_discount.isChecked }

    private fun watcherDiscount(){
        et_discount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (checkout_switch_discount.isChecked){
                    checkoutViewModel.setDiscountValue(s.toString())
                }else{
                    checkoutViewModel.setDiscountValue(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun confirmOrder(){
        checkout_cash.setOnClickListener {
            val productsToSend : List<ProductSend> = getSelectedProducts()!!.map{ p -> ProductSend(id = p.id!!, price = p.price!!, quantity = p.selectedQuantity!!) }.toList()
            val orderSend = OrderSend(sellByStore = getParentStore().id, products = productsToSend)
            if(checkoutViewModel.listenToCurrentCustomer().value == null){
                checkoutViewModel.confirmOrder(PaperlessUtil.getToken(this), orderSend)
            }else{
                val customer = checkoutViewModel.listenToCurrentCustomer().value!!
                if(customer.isStore){
                    //send note to store
                    orderSend.boughtByStore = getAbsoluteId(customer.idCustomer)
                    checkoutViewModel.confirmOrder(PaperlessUtil.getToken(this), orderSend)
                }else{
                    AlertDialog.Builder(this).apply {
                        setMessage(resources.getString(R.string.etc_coming_soon))
                        setPositiveButton(resources.getString(R.string.info_understand)){ dialog, _ ->
                            dialog.dismiss()
                        }
                    }.show()
                }
            }
        }
    }

    private fun getAbsoluteId(id : String) : Int{
        return if(id.contains("STR")){
            val i = id.replace("STR", "")
            i.toInt()
        }else{
            val i = id.replace("USR", "")
            i.toInt()
        }
    }

    private fun selectCustomer(){
        checkout_customer.setOnClickListener {
            startActivityForResult(Intent(this@CheckoutActivity, ScannerActivity::class.java).apply {
                putExtra("IS_STORE", false)
            }, 0)
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
            is CheckoutState.Success -> {
                toast(resources.getString(R.string.info_successfully_checkout))
                finish()
            }
            is CheckoutState.Alert -> {
                when(it.alertType){
                    AlertType.EXCEPTION -> showAlert(it.message.toString())
                    AlertType.FAILED -> showAlert(resources.getString(R.string.alert_failed))
                    AlertType.FAILED_BY_CODE -> showAlert(resources.getString(R.string.alert_failed_by_code))
                }
            }
            is CheckoutState.IsLoading -> {
                if(it.state){
                    loading.visibility = View.VISIBLE
                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }else{
                    loading.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    private fun showAlert(m: String){
        AlertDialog.Builder(this).apply {
            setMessage(m)
            setPositiveButton(resources.getString(R.string.info_understand)){d , _ ->
                d.dismiss()
            }
        }.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null ){
            checkCustomerTarget(data.getStringExtra("CODE"), data.getBooleanExtra("IS_STORE", false))
        }
    }

    private fun checkCustomerTarget(code : String, isStore: Boolean){
        checkoutViewModel.setCustomerTarget(PaperlessUtil.getToken(this@CheckoutActivity), Customer(code, isStore))
    }

    private fun customerViewBehavior(){
        customer_delete.setOnClickListener { checkoutViewModel.deleteCustomer() }
        if(checkoutViewModel.listenToCurrentCustomer().value == null){
            customer_name.text = ""
            customer_desc.text = ""
//            customer_image.load(R.drawable.image_placeholder)
            checkout_customer_info.visibility = View.GONE
        }else{
            checkout_customer_info.visibility = View.VISIBLE
        }
    }

    private fun handleCustomerChange(it : Customer?){
        customerViewBehavior()
        it?.let { x ->
            customer_name.text = x.name
            customer_desc.text = x.desc
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    private fun handleDiscountValue(it: String){ calculateTotalPrice() }
    private fun calculateTotalPrice(){ checkout_total_price.text = PaperlessUtil.setToIDR(checkoutViewModel.calculateTotalPrice()) }
    private fun getSelectedProducts() : List<Product>? = intent.getParcelableArrayListExtra("PRODUCT")
    private fun getParentStore() : Store = intent.getParcelableExtra("STORE")!!
}
