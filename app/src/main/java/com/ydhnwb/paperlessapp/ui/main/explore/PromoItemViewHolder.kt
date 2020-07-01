package com.ydhnwb.paperlessapp.ui.main.explore

import android.content.Intent
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.ui.detail_product.DetailProductActivity
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.list_item_catalog.view.*

class PromoItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(product: Product){
        with(itemView){
            if(product.discountByPercent == null){
                catalog_store_discount_layout.gone()
                catalog_price.text = PaperlessUtil.setToIDR(product.price!!)
            }else{
                catalog_store_discount_layout.visible()
                catalog_store_actual_price.text = PaperlessUtil.setToIDR(product.price?:0)
                catalog_store_actual_price.paintFlags = catalog_store_actual_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                catalog_store_discount.text = " ${product.discountByPercent!!}% OFF"
                val p = product.price!! * product.discountByPercent!! / 100
                catalog_price.text = PaperlessUtil.setToIDR(product.price!! - p.toInt())
            }

            catalog_image.load(product.image)
            catalog_name.text = product.name
            if (product.store != null){
                catalog_store_name.text = product.store?.name!!
            }else{
                catalog_store_name.visibility = View.GONE
            }
            setOnClickListener {
                context.startActivity(Intent(context, DetailProductActivity::class.java).apply {
                    putExtra("product", product)
                })
            }
        }
    }
}