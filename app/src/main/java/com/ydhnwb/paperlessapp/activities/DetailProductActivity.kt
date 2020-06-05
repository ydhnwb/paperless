package com.ydhnwb.paperlessapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.google.android.material.appbar.AppBarLayout
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.store_page_activity.StorePageActivity
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.activity_detail_product.*
import kotlinx.android.synthetic.main.content_detail_product.*

class DetailProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        initCollapsingToolbar()
        fill()
    }
    private fun initCollapsingToolbar(){
        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            var scrollRange = -1
            if (scrollRange == -1) { scrollRange = app_bar.totalScrollRange }
            when {
                scrollRange + verticalOffset == 0 -> { toolbar_layout.title = getPassedProduct()?.name ?: kotlin.run { "" } }
                scrollRange + verticalOffset > 0 -> {
                    toolbar_layout.title = " "
                }
            }
        })
    }

    private fun getPassedProduct() = intent.getParcelableExtra<Product?>("product")
    private fun fill(){
        getPassedProduct()?.let {
            product_image.load(it.image)
            product_name.text = it.name
            product_price.text = PaperlessUtil.setToIDR(it.price!!)
            it.qty?.let { stock -> product_stock.text = "Sisa ${stock}" } ?: run { product_stock.visibility = View.GONE}
            product_desc.text = it.description
            it.store?.let { store ->
                product_store_image.load(store.store_logo)
                product_store_name.text = store.name
                product_store_address.text = store.address
                product_store.setOnClickListener {
                    startActivity(Intent(this@DetailProductActivity, StorePageActivity::class.java).apply {
                        putExtra("store_id", store.id)
                    })
                }
            } ?: kotlin.run {
                product_store.visibility = View.GONE
            }
        }
    }



}
