package com.ydhnwb.paperlessapp.ui.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
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
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.content_checkout.*
import kotlinx.android.synthetic.main.content_checkout.customer_desc
import kotlinx.android.synthetic.main.content_checkout.customer_name
import kotlinx.android.synthetic.main.list_item_customer.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutActivity : AppCompatActivity() {
    private val checkoutViewModel : CheckoutViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setSupportActionBar(toolbar)
        setupToolbar()
        listenerDiscount()
        behaviorDiscount()
        watcherDiscount()
        observe()
        fill()
        confirmOrder()
        selectCustomer()
        customerViewBehavior()
    }

    private fun setupToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun observe(){
        observeState()
        observeDiscountValue()
        observeSelectedProduct()
        observeCurrentCustomer()
    }

    private fun observeState() = checkoutViewModel.listenToUIState().observer(this, Observer { handleUIState(it) })
    private fun observeDiscountValue() = checkoutViewModel.listenToDiscountValue().observe(this, Observer { handleDiscountValue(it) })
    private fun observeSelectedProduct() = checkoutViewModel.listenToSelectedProduct().observe(this, Observer { calculateTotalPrice() })
    private fun observeCurrentCustomer() = checkoutViewModel.listenToCurrentCustomer().observe(this, Observer { handleCustomerChange(it) })
    private fun fill() = getSelectedProducts()?.let { checkoutViewModel.setSelectedProducts(it) }

    private fun listenerDiscount(){
        checkout_switch_discount.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)checkout_layout_discount.visible() else {
                et_discount.text?.clear()
                checkout_layout_discount.gone()
            }
        }
    }

    private fun behaviorDiscount() = checkout_btn_discount.setOnClickListener { checkout_switch_discount.isChecked = !checkout_switch_discount.isChecked }

    private fun watcherDiscount(){
        et_discount.filters = arrayOf(object : InputFilter {
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
                source?.let { s ->
                    if(s == ""){ return s }
                    if(s.toString().matches("[0-9]+".toRegex())){ return s }
                    return ""
                }
                return ""
            }
        })

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
                AlertDialog.Builder(this).apply {
                    setMessage(getString(R.string.ask_checkout))
                    setPositiveButton(getString(R.string.info_create_order)){ dialog, _ ->
                        dialog.dismiss()
                        PaperlessUtil.getToken(this@CheckoutActivity)?.let { it1 -> checkoutViewModel.createOrder(it1, orderSend) }
                    }
                    setNegativeButton(getString(R.string.info_cancel)){ d, _ -> d.cancel()}
                }.show()

            }else{
                val customer = checkoutViewModel.listenToCurrentCustomer().value!!
                if(customer.isStore){
                    AlertDialog.Builder(this).apply {
                        setMessage(resources.getString(R.string.ask_checkout))
                        setNegativeButton(resources.getString(R.string.info_cancel)){ d, _ -> d.cancel() }
                        setPositiveButton(resources.getString(R.string.info_create_order)){ dialog, _ ->
                            orderSend.boughtByUser = null
                            orderSend.boughtByStore = getAbsoluteId(customer.idCustomer)
                            PaperlessUtil.getToken(this@CheckoutActivity)?.let { it1 ->
                                checkoutViewModel.createOrder(it1, orderSend)
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

    private fun resetDiscount(){
        if (checkout_switch_discount.isChecked){
            et_discount.text?.clear()
        }
    }

    private fun successCheckout(){
        toast(resources.getString(R.string.info_successfully_checkout))
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun isLoading(b: Boolean){
        if(b){
            loading.visible()
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            loading.gone()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun handleUIState(it: CheckoutState){
        when(it){
            is CheckoutState.ShowToast -> showToast(it.message)
            is CheckoutState.ResetDiscount -> resetDiscount()
            is CheckoutState.Success -> successCheckout()
            is CheckoutState.ShowAlert -> showAlert(it.message)
            is CheckoutState.IsLoading -> isLoading(it.state)
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

    private fun checkCustomerTarget(code : String, isStore: Boolean) = PaperlessUtil.getToken(this@CheckoutActivity)?.let { checkoutViewModel.setCustomerTarget(it, Customer(code, isStore)) }

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
            customer_image
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
