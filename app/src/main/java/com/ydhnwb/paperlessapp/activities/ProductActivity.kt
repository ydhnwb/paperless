package com.ydhnwb.paperlessapp.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.viewmodels.CategoryState
import com.ydhnwb.paperlessapp.viewmodels.CategoryViewModel
import com.ydhnwb.paperlessapp.viewmodels.ProductState
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.content_product.*


class ProductActivity : AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel
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
    private fun setErrorQuantity(err: String?) = { in_product_quantity.error = err }
    private fun setErrorWeight(err: String?) = { in_product_weight.error = err }



}
