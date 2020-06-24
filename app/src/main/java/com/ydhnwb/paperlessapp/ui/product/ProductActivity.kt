package com.ydhnwb.paperlessapp.ui.product

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.api.load
import com.fxn.pix.Pix
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.scanner.ScannerActivity
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showInfoAlert
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.content_product.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class ProductActivity : AppCompatActivity() {
    companion object {
        const val IMAGE_REQUEST_CODE = 123
    }
    private val productCreateEditViewModel: ProductCreateEditViewModel by viewModel()
    private var product = Product()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        setSupportActionBar(toolbar)
        setupToolbar()
        checkBoxHaveStock()
        checkBoxIsPromo()
        priceEditTextBehavior()
        setupPromoEditText()
        observe()
        fetchCategories()
        chooseImage()
        saveChanges()
        fill()
        scanBarcode()
    }

    private fun setupToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun fetchCategories() = productCreateEditViewModel.fetchCategories()

    private fun observe(){
        observeCategories()
        observeState()
    }

    private fun observeState() = productCreateEditViewModel.listenToUIState().observer(this, Observer { handleUIState(it) })
    private fun observeCategories() = productCreateEditViewModel.listenToCategories().observe(this, Observer { attachToSpinner(it) })

    private fun handleDiscountValue(discountValue: Float?){
        if(discountValue != null){
            et_product_promo.setText(discountValue.toString())
        }else{
            et_product_promo.text?.clear()
        }

    }

    private fun isLoading(b: Boolean){
        btn_submit.isEnabled = !b
        sp_product_category.isEnabled = !b
        if (b) loading.visible() else loading.gone()
    }

    private fun handleValidation(validatePayload: ProductCreateEditState.Validate){
        validatePayload.name?.let { error -> setErrorName(error) }
        validatePayload.price?.let { error -> setErrorPrice(error) }
        validatePayload.qty?.let { error -> setErrorQuantity(error) }
        validatePayload.desc?.let { error -> setErrorDescription(error) }
    }

    private fun resetValidation(){
        setErrorName(null)
        setErrorPrice(null)
        setErrorQuantity(null)
        setErrorDescription(null)
    }

    private fun success(isCreate: Boolean){
        if(isCreate){
            showToast(resources.getString(R.string.info_success_create_product))
            finish()
        }else{
            showToast(resources.getString(R.string.info_success_update_product))
            finish()
        }
    }

    private fun successDelete(){
        showToast(resources.getString(R.string.info_success_delete_product))
        finish()
    }

    private fun handleUIState(it: ProductCreateEditState){
        when(it){
            is ProductCreateEditState.IsLoading -> isLoading(it.state)
            is ProductCreateEditState.Validate ->  handleValidation(it)
            is ProductCreateEditState.Reset -> resetValidation()
            is ProductCreateEditState.ShowToast -> showToast(it.message)
            is ProductCreateEditState.Success-> success(it.isCreate)
            is ProductCreateEditState.SuccessDelete -> successDelete()
        }
    }


    private fun setupPromoEditText(){
        et_product_promo.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString()
                if(value.isNotEmpty()){
                    val discountValue = value.substring(0,1).toFloatOrNull()
                    discountValue?.let {
                        if(it > 100 || it <= 0){
                            setErrorPromo(resources.getString(R.string.error_discount_not_valid))
                            et_product_promo.text?.clear()
                        }
                    } ?:run {
                        setErrorPromo(resources.getString(R.string.error_discount_exception))
                        et_product_promo.text?.clear()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }


    private fun fill(){
        getPassedProduct()?.let {
            et_product_name.setText(it.name)
            et_prodouct_price.setText(it.price.toString())
            et_prodouct_desc.setText(it.description.toString())
            if(it.qty != null) et_product_quantity.setText(it.qty.toString())
            cb_product_have_stock.isChecked = (it.qty != null)
            if(cb_product_have_stock.isChecked){
                et_product_quantity.setText(it.qty.toString())
            }
            product_image.load(it.image)
            if(it.discountByPercent != null && it.discountByPercent != 0F){
                fillDiscountByPercent(it.discountByPercent!!)
            }
            it.code?.let { code ->
                et_prodouct_code.setText(code)
            }
            copyValueToProduct(it)
        } ?: run{
            cb_product_promo.gone()
            in_product_promo.gone()
        }
    }

    private fun fillDiscountByPercent(discountValue : Float){
        cb_product_promo.isChecked = true
        et_product_promo.setText(discountValue.toString())
    }

    private fun copyValueToProduct(it: Product){
        product.apply {
            id = it.id
            name = it.name
            description = it.description
            image = it.image
            price = it.price
            category = it.category
            status = true
            image = it.image
            code = it.code
        }
    }

    private fun scanBarcode(){
        btn_product_scan.setOnClickListener {
            startActivityForResult(Intent(this, ScannerActivity::class.java), 0)
        }
    }

    private fun saveChanges(){
        btn_submit.setOnClickListener {
            showToast("is checked ${cb_product_promo.isChecked} and ${cb_product_have_stock.isChecked}")
            product.apply {
                this.discountByPercent = if (cb_product_promo.isChecked) {
                    et_product_promo.text.toString().trim().toFloatOrNull()
                } else null
                this.name = et_product_name.text.toString().trim()
                this.code = if (et_prodouct_code.text.toString().trim().isNotEmpty()) et_prodouct_code.text.toString().trim() else null
                this.description = et_prodouct_desc.text.toString().trim()
                this.price = et_prodouct_price.text.toString().trim().toIntOrNull()
                this.category = sp_product_category.selectedItem as Category?
                this.qty = if(cb_product_have_stock.isChecked) et_product_quantity.text.toString().trim().toIntOrNull() else null
            }

            product.category?.let { cat ->
                if(productCreateEditViewModel.validate(product.name.toString(), product.description.toString(), product.price, product.qty, cat.id, cb_product_have_stock.isChecked)){
                    getPassedProduct()?.let { passedProduct ->
                        val isUpdateImage = !passedProduct.image.equals(product.image)
                        product.qty = if (cb_product_have_stock.isChecked) product.qty else null
                        productCreateEditViewModel.updateProduct(PaperlessUtil.getToken(this@ProductActivity), getPassedStore()?.id.toString(), product, cat.id!!, isUpdateImage)
                    } ?: kotlin.run {
                        product.image?.let { _ ->
                            productCreateEditViewModel.createProduct(PaperlessUtil.getToken(this@ProductActivity), getPassedStore()?.id.toString(), product, cat.id!!)
                        } ?: kotlin.run {
                            showInfoAlert(resources.getString(R.string.info_please_select_image))
                        }
                    }
                }
            }
        }
    }

    private fun chooseImage(){ product_image.setOnClickListener { Pix.start(this, IMAGE_REQUEST_CODE) } }

    private fun getPassedProduct() : Product? = intent.getParcelableExtra("PRODUCT")
    private fun getPassedStore() : Store? = intent.getParcelableExtra("STORE")

    private fun checkBoxIsPromo(){
        cb_product_promo.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                in_product_promo.visible()
            } else {
                et_product_promo.text?.clear()
                in_product_promo.gone()
            }
        }
    }


    private fun checkBoxHaveStock(){
        cb_product_have_stock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) in_product_quantity.visible() else in_product_quantity.gone()
        }
    }

    private fun attachToSpinner(it: List<Category>){
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, it)
        sp_product_category.adapter = spinnerAdapter
        getPassedProduct()?.let {
            val pos = spinnerAdapter.getPosition(it.category)
            sp_product_category.setSelection(pos)
        }
    }


    private fun setErrorName(err: String?) { in_product_name.error = err }
    private fun setErrorPrice(err: String?) { in_product_price.error = err }
    private fun setErrorQuantity(err: String?) { in_product_quantity.error = err }
    private fun setErrorDescription(err: String?) { in_product_desc.error = err }
    private fun setErrorPromo(err: String?){ in_product_promo.error = err }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val selectedImageUri = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            selectedImageUri?.let{
                product.image = it[0]
                product_image.load(File(it[0]))
            }
        }else if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null ){
            et_prodouct_code.setText(data.getStringExtra("CODE"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getPassedProduct()?.let {
            menuInflater.inflate(R.menu.menu_common_product, menu)
            return true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_delete -> {
                productCreateEditViewModel.deleteProduct(PaperlessUtil.getToken(this@ProductActivity), getPassedStore()?.id.toString(), getPassedProduct()?.id.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun priceEditTextBehavior(){
        et_prodouct_price.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().trim().isNotEmpty()){
                    validatePrice(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    private fun validatePrice(price: String){
        if(price.trim().substring(0,1).equals("0")){
            et_prodouct_price.text?.clear()
            showInfoAlert(resources.getString(R.string.validate_price_not_valid))
        }
    }
}
