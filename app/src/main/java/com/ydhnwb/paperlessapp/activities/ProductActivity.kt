package com.ydhnwb.paperlessapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
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
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        categoryViewModel.fetchCategory()
        categoryViewModel.listenCategories().observe(this, Observer { attachToSpinner(it) })
        categoryViewModel.listenToUIState().observe(this, Observer { handleCategoryState(it) })
        productViewModel.listenToUIState().observe(this, Observer { handleUIState(it) })
        chooseImage()
        saveChanges()
    }

    private fun handleCategoryState(it: CategoryState){
        when(it){
            is CategoryState.IsLoading -> sp_product_category.isEnabled = !it.state
            is CategoryState.ShowToast -> toast(it.message)
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    private fun attachToSpinner(it: List<Category>){
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, it)
        sp_product_category.adapter = spinnerAdapter
        sp_product_category.setOnItemSelectedListener(object : OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory : Category = parent?.selectedItem as Category
                println(selectedCategory.name.toString() + " selected")
            }
        })
    }
    private fun handleUIState(it: ProductState){
        when(it){
            is ProductState.IsLoading -> {
                if(it.state){ loading.visibility = View.VISIBLE
                }else{ loading.visibility = View.GONE }
            }
            is ProductState.Validate -> {
                it.name?.let { e -> setErrorName(e) }
                it.price?.let { e -> setErrorPrice(e) }
                it.qty?.let { e-> setErrorQuantity(e) }
            }
            is ProductState.Reset -> {
                setErrorName(null)
                setErrorPrice(null)
                setErrorQuantity(null)
                setErrorWeight(null)
            }
            is ProductState.ShowToast -> toast(it.message)
        }
    }
    private fun checkBoxAvailableOnline(){
        cb_product_online_available.setOnCheckedChangeListener { it , isChecked ->
            if(isChecked){ in_product_weight.visibility = View.VISIBLE
            }else{ in_product_weight.visibility = View.GONE }
        }
    }

    private fun setErrorName(err: String?) { in_product_name.error = err }
    private fun setErrorPrice(err: String?) { in_product_price.error = err }
    private fun setErrorQuantity(err: String?) { in_product_quantity.error = err }
    private fun setErrorWeight(err: String?) { in_product_weight.error = err }
    private fun getPassedProduct() = intent.getParcelableExtra<Product>("PRODUCT")

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

    private fun chooseImage(){
        product_image.setOnClickListener {
            Pix.start(this, IMAGE_REQUEST_CODE)
        }
    }

    private fun saveChanges(){
        btn_submit.setOnClickListener {
            val name = et_product_name.text.toString().trim()
            val price = et_prodouct_price.text .toString().trim()
            val quantity = et_product_quantity.text.toString()
            val weight = et_product_weight.text.toString()
            val category : Category? = sp_product_category.selectedItem as Category
            category?.let {
                if(productViewModel.validate(name, price, quantity, weight, category.id)){
                    toast("Add product")
                }
            }
        }
    }

    private fun popup(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme)).apply {
            setMessage(message)
            setPositiveButton("Mengerti"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
        }
    }

}
