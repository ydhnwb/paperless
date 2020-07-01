package com.ydhnwb.paperlessapp.ui.checkout

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
import com.ydhnwb.paperlessapp.ui.scanner.ScannerActivity
import com.ydhnwb.paperlessapp.models.*
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.content_checkout.*
import kotlinx.android.synthetic.main.content_checkout.customer_desc
import kotlinx.android.synthetic.main.content_checkout.customer_name
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutActivity : AppCompatActivity() {
    companion object {
        private const val RES_CODE = 12
    }
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
        checkoutViewModel.listenToUIState().observer(this, Observer { handleUIState(it) })
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
                PaperlessUtil.getToken(this)?.let { it1 -> checkoutViewModel.createOrder(it1, orderSend) }
            }else{
                val customer = checkoutViewModel.listenToCurrentCustomer().value!!
                if(customer.isStore){
                    AlertDialog.Builder(this).apply {
                        setMessage(resources.getString(R.string.ask_checkout))
                        setNegativeButton(resources.getString(R.string.info_cancel)){ d, _ -> d.cancel() }
                        setPositiveButton(resources.getString(R.string.info_create_order)){ dialog, _ ->
                            orderSend.boughtByUser = null
                            orderSend.boughtByStore = getAbsoluteId(customer.idCustomer)
                            toast("Id store is ${getAbsoluteId(customer.idCustomer)}")
                            PaperlessUtil.getToken(this@CheckoutActivity)?.let { it1 ->
                                checkoutViewModel.createOrder(
                                    it1, orderSend)
                            }
                            dialog.dismiss()
                        }
                    }.show()
                }else{
                     AlertDialog.Builder(this).apply {
                         setMessage(resources.getString(R.string.ask_checkout))
                         setNegativeButton(resources.getString(R.string.info_cancel)){ d, _ -> d.cancel() }
                         setPositiveButton(resources.getString(R.string.info_create_order)){ dialog, _ ->
                             orderSend.boughtByStore = null
                             orderSend.boughtByUser = getAbsoluteId(customer.idCustomer)
                             PaperlessUtil.getToken(this@CheckoutActivity)?.let { it1 ->
                                 checkoutViewModel.createOrder(
                                     it1, orderSend)
                             }
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
                setResult(Activity.RESULT_OK)
                finish()
            }
            is CheckoutState.ShowAlert -> {
                showAlert(it.message)
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
            checkCustomerTarget(data.getStringExtra("CODE")!!, data.getBooleanExtra("IS_STORE", false))
        }
    }

    private fun checkCustomerTarget(code : String, isStore: Boolean){
        PaperlessUtil.getToken(this@CheckoutActivity)?.let { checkoutViewModel.setCustomerTarget(it, Customer(code, isStore)) }
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
