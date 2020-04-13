package com.ydhnwb.paperlessapp.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil

import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.content_checkout.*

class CheckoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        fill()
        listenerDiscount()
        behaviorDiscount()
        listenerPercent()
    }

    private fun getSelectedProducts() : List<Product>? = intent.getParcelableArrayListExtra("PRODUCT")

    private fun fill(){
        getSelectedProducts()?.let {
            checkout_total_price.text = PaperlessUtil.setToIDR(it.sumBy { product ->
                product.selectedQuantity!! * product.price!!
            })
        }
    }

    private fun setVisibilityDiscountLayout(bool : Boolean){
        if(bool){ checkout_layout_discount.visibility = View.VISIBLE }else{ checkout_layout_discount.visibility = View.GONE }
    }

    private fun listenerDiscount(){
        checkout_switch_discount.setOnCheckedChangeListener { _, isChecked ->
            setVisibilityDiscountLayout(isChecked)
        }
    }

    private fun behaviorDiscount(){
        checkout_btn_discount.setOnClickListener {
            checkout_switch_discount.isChecked = !checkout_switch_discount.isChecked
        }

    }
    private fun listenerPercent(){
        checkout_checkbox_percent.setOnCheckedChangeListener { _, isChecked ->
            checkout_discount_indicator.text = if(isChecked) "%" else "Rp."
        }
    }

}
