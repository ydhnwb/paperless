package com.ydhnwb.paperlessapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.fxn.pix.Pix
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.CategoryState
import com.ydhnwb.paperlessapp.viewmodels.CategoryViewModel
import com.ydhnwb.paperlessapp.viewmodels.ProductState
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.content_product.*
import java.io.File


class ProductActivity : AppCompatActivity() {
    private val IMAGE_REQUEST_CODE = 123
    private lateinit var productViewModel: ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private var product = Product()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        checkBoxAvailableOnline()
        checkBoxHaveStock()
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        categoryViewModel.fetchCategory()
        categoryViewModel.listenCategories().observe(this, Observer { attachToSpinner(it) })
        categoryViewModel.listenToUIState().observe(this, Observer { handleCategoryState(it) })
        productViewModel.listenToUIState().observe(this, Observer { handleUIState(it) })
        chooseImage()
        saveChanges()
        fill()
    }




    private fun handleUIState(it: ProductState){
        when(it){
            is ProductState.IsLoading -> {
                if(it.state){
                    loading.visibility = View.VISIBLE
                }else{
                    loading.visibility = View.GONE
                }
                btn_submit.isEnabled = !it.state
            }
            is ProductState.Validate -> {
                it.name?.let { e -> setErrorName(e) }
                it.price?.let { e -> setErrorPrice(e) }
                it.qty?.let { e-> setErrorQuantity(e) }
                it.weight?.let { e -> setErrorWeight(e) }
                it.desc?.let { e -> setErrorDescription(e) }
            }
            is ProductState.Reset -> {
                setErrorName(null)
                setErrorPrice(null)
                setErrorQuantity(null)
                setErrorWeight(null)
                setErrorDescription(null)
            }
            is ProductState.ShowPopup -> popup(it.message)
            is ProductState.ShowToast -> toast(it.message)
            is ProductState.Success -> finish()
        }
    }



    private fun fill(){
        getPassedProduct()?.let {
            et_product_name.setText(it.name)
            et_prodouct_price.setText(it.price.toString())
            et_prodouct_desc.setText(it.description.toString())
            if(it.qty != null){ et_product_quantity.setText(it.qty.toString()) }
            cb_product_online_available.isChecked = it.availableOnline
            cb_product_have_stock.isChecked = (it.stock != null)
            if(cb_product_online_available.isChecked){ et_product_weight.setText(it.weight.toString()) }
            if(cb_product_have_stock.isChecked){ et_product_quantity.setText(it.stock!!.stock!!.toString()) }
            product_image.load(it.image)
            product.apply {
                id = it.id
                name = it.name
                description = it.description
                image = it.image
                price = it.price
                weight = it.weight
                availableOnline = it.availableOnline
                category = it.category
                status = it.status
                image = it.image
            }
        }
    }




    private fun saveChanges(){
        btn_submit.setOnClickListener {
            product.apply {
                this.name = et_product_name.text.toString().trim()
                this.description = et_prodouct_desc.text.toString().trim()
                this.price = et_prodouct_price.text.toString().trim().toIntOrNull()
                this.qty = et_product_quantity.text.toString().trim().toIntOrNull()
                this.availableOnline = cb_product_online_available.isChecked
                this.weight = et_product_weight.text.toString().trim().toDoubleOrNull()
                this.category = sp_product_category.selectedItem as Category?
            }.also { p ->
                if(p.weight == null){
                    p.weight = 1.0
                }
            }
            product.category?.let {cat ->
                if(productViewModel.validate(product.name.toString(), product.description.toString(),
                        product.price, product.qty, product.availableOnline, product.weight, cat.id)){
                    getPassedProduct()?.let { passedProduct ->
                        val isUpdateImage = !passedProduct.image.equals(product.image)
                        productViewModel.updateProduct(PaperlessUtil.getToken(this@ProductActivity), getPassedStore()?.id.toString(),
                        product, cat.id!!, isUpdateImage)

                    } ?: kotlin.run {
                        product.image?.let { imagePath ->
                            productViewModel.createProduct(PaperlessUtil.getToken(this@ProductActivity),
                                getPassedStore()?.id.toString(), product, cat.id!!)
                        } ?: kotlin.run {
                            popup("Mohon pilih gambar terlebih dahulu")
                        }
                    }
                }
            }
        }
    }

    private fun chooseImage(){ product_image.setOnClickListener { Pix.start(this, IMAGE_REQUEST_CODE) } }

    private fun getPassedProduct() : Product? = intent.getParcelableExtra<Product>("PRODUCT")
    private fun getPassedStore() : Store? = intent.getParcelableExtra<Store>("STORE")

    private fun checkBoxAvailableOnline(){ cb_product_online_available.setOnCheckedChangeListener { it , isChecked ->
        if(isChecked) in_product_weight.visibility = View.VISIBLE else in_product_weight.visibility = View.GONE } }
    private fun checkBoxHaveStock(){ cb_product_have_stock.setOnCheckedChangeListener { it, isChecked ->
        if (isChecked) in_product_quantity.visibility = View.VISIBLE else in_product_quantity.visibility = View.GONE } }

    private fun attachToSpinner(it: List<Category>){
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, it)
        sp_product_category.adapter = spinnerAdapter
        getPassedProduct()?.let {
            val pos = spinnerAdapter.getPosition(it.category)
            sp_product_category.setSelection(pos)
        }
    }
    private fun handleCategoryState(it: CategoryState){
        when(it){
            is CategoryState.IsLoading -> sp_product_category.isEnabled = !it.state
            is CategoryState.ShowToast -> toast(it.message)
        }
    }

    private fun popup(message: String) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme)).apply {
            setMessage(message)
            setPositiveButton("Mengerti"){ dialogInterface, _ ->
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
                productViewModel.delete(PaperlessUtil.getToken(this@ProductActivity),
                    getPassedStore()?.id.toString(), getPassedProduct()?.id.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
