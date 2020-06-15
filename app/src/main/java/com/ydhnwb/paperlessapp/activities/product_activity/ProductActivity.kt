package com.ydhnwb.paperlessapp.activities.product_activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import coil.api.load
import com.fxn.pix.Pix
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.ScannerActivity
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.showInfoAlert
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.content_product.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class ProductActivity : AppCompatActivity() {
    private val IMAGE_REQUEST_CODE = 123
    private val productCreateEditViewModel: ProductCreateEditViewModel by viewModel()

    private var product = Product()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
//        checkBoxAvailableOnline()
        checkBoxHaveStock()
        priceEditTextBehavior()
        productCreateEditViewModel.fetchCategories()
        productCreateEditViewModel.listenToCategories().observe(this, Observer { attachToSpinner(it) })
        productCreateEditViewModel.listenToUIState().observer(this, Observer { handleUIState(it) })
        chooseImage()
        saveChanges()
        fill()
        scanBarcode()
    }




    private fun handleUIState(it: ProductCreateEditState){
        when(it){
            is ProductCreateEditState.IsLoading -> {
                sp_product_category.isEnabled = !it.state
                if(it.state){
                    loading.visibility = View.VISIBLE
                } else {
                    loading.visibility = View.GONE
                }
                btn_submit.isEnabled = !it.state
            }
            is ProductCreateEditState.Validate -> {
                it.name?.let { e -> setErrorName(e) }
                it.price?.let { e -> setErrorPrice(e) }
                it.qty?.let { e-> setErrorQuantity(e) }
                it.weight?.let { e -> setErrorWeight(e) }
                it.desc?.let { e -> setErrorDescription(e) }
            }
            is ProductCreateEditState.Reset -> {
                setErrorName(null)
                setErrorPrice(null)
                setErrorQuantity(null)
                setErrorWeight(null)
                setErrorDescription(null)
            }
            is ProductCreateEditState.ShowToast -> toast(it.message)
            is ProductCreateEditState.Success-> {
                if(it.isCreate){
                    toast(resources.getString(R.string.info_success_create_product))
                    finish()
                }else{
                    toast(resources.getString(R.string.info_success_update_product))
                    finish()
                }
            }
            is ProductCreateEditState.SuccessDelete -> {
                toast(resources.getString(R.string.info_success_delete_product))
                finish()
            }
        }
    }



    private fun fill(){
        getPassedProduct()?.let {
            et_product_name.setText(it.name)
            et_prodouct_price.setText(it.price.toString())
            et_prodouct_desc.setText(it.description.toString())
            if(it.qty != null){ et_product_quantity.setText(it.qty.toString()) }
//            cb_product_online_available.isChecked = it.availableOnline
            cb_product_have_stock.isChecked = (it.qty != null)
//            if(cb_product_online_available.isChecked){ et_product_weight.setText(it.weight.toString()) }
            if(cb_product_have_stock.isChecked){ et_product_quantity.setText(it.qty.toString()) }
            product_image.load(it.image)
            it.code?.let { code -> et_prodouct_code.setText(code) }
            product.apply {
                id = it.id
                name = it.name
                description = it.description
                image = it.image
                price = it.price
                weight = 1.0
                availableOnline = false
                category = it.category
                status = true
                image = it.image
                code = it.code
            }
        }
    }


    private fun scanBarcode(){
        btn_product_scan.setOnClickListener {
            startActivityForResult(Intent(this, ScannerActivity::class.java), 0)
        }
    }

    private fun saveChanges(){
        btn_submit.setOnClickListener {
//            var qty: Int? = null
//            if(cb_product_have_stock.isChecked){
//                qty = et_product_quantity.text.toString().trim().toIntOrNull()
//            }else{
//                qty = null
//            }
            product.apply {
                this.name = et_product_name.text.toString().trim()
                this.code = if (et_prodouct_code.text.toString().trim().isNotEmpty()) et_prodouct_code.text.toString().trim() else null
                this.description = et_prodouct_desc.text.toString().trim()
                this.price = et_prodouct_price.text.toString().trim().toIntOrNull()
                this.availableOnline = false
                this.weight = 1.0
                this.category = sp_product_category.selectedItem as Category?
                this.qty = if(cb_product_have_stock.isChecked) et_product_quantity.text.toString().trim().toIntOrNull() else null
            }.also { p ->
                if(p.weight == null){
                    p.weight = 1.0
                }
            }
            product.category?.let {cat ->
                if(productCreateEditViewModel.validate(product.name.toString(), product.description.toString(), product.price, product.qty, product.availableOnline, product.weight, cat.id, cb_product_have_stock.isChecked)){
                    getPassedProduct()?.let { passedProduct ->
                        val isUpdateImage = !passedProduct.image.equals(product.image)
                        product.qty = if (cb_product_have_stock.isChecked) product.qty else null
                        productCreateEditViewModel.updateProductWithImage(PaperlessUtil.getToken(this@ProductActivity), getPassedStore()?.id.toString(), product, cat.id!!, isUpdateImage)
                    } ?: kotlin.run {
                        product.image?.let { _ ->
                            productCreateEditViewModel.createProduct(PaperlessUtil.getToken(this@ProductActivity), getPassedStore()?.id.toString(), product, cat.id!!)
                        } ?: kotlin.run {
                            popup(resources.getString(R.string.info_please_select_image))
                        }
                    }
                }
            }
        }
    }

    private fun chooseImage(){ product_image.setOnClickListener { Pix.start(this, IMAGE_REQUEST_CODE) } }

    private fun getPassedProduct() : Product? = intent.getParcelableExtra("PRODUCT")
    private fun getPassedStore() : Store? = intent.getParcelableExtra("STORE")

    private fun checkBoxAvailableOnline(){ cb_product_online_available.setOnCheckedChangeListener { _ , isChecked ->
        if(isChecked) in_product_weight.visibility = View.VISIBLE else in_product_weight.visibility = View.GONE } }
    private fun checkBoxHaveStock(){ cb_product_have_stock.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) in_product_quantity.visibility = View.VISIBLE else in_product_quantity.visibility = View.GONE } }

    private fun attachToSpinner(it: List<Category>){
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, it)
        sp_product_category.adapter = spinnerAdapter
        getPassedProduct()?.let {
            val pos = spinnerAdapter.getPosition(it.category)
            sp_product_category.setSelection(pos)
        }
    }


    private fun popup(message: String) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme)).apply {
            setMessage(message)
            setPositiveButton(resources.getString(R.string.info_understand)){ dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
        }
    }
    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private fun setErrorName(err: String?) { in_product_name.error = err }
    private fun setErrorPrice(err: String?) { in_product_price.error = err }
    private fun setErrorQuantity(err: String?) { in_product_quantity.error = err }
    private fun setErrorWeight(err: String?) { in_product_weight.error = err }
    private fun setErrorDescription(err: String?) { in_product_desc.error = err }

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
