package com.ydhnwb.paperlessapp.ui.manage.etalase

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.manage.ManageStoreViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showInfoAlert
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.list_item_product.view.*

class EtalaseAdapter (private var products : MutableList<Product>, private var context: Context, private var pvm: ManageStoreViewModel) : RecyclerView.Adapter<EtalaseAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_product, parent, false)
        )
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(products[position], context, pvm)

    fun updateList(prs : List<Product>){
        products.clear()
        products.addAll(prs)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context, pvm: ManageStoreViewModel){
            with(itemView){
                product_name.text = product.name

                if(product.discountByPercent == null){
                    product_discount_layout.gone()
                    product_price.text = PaperlessUtil.setToIDR(product.price!!)
                }else{
                    product_discount_layout.visible()
                    product_actual_price.text = PaperlessUtil.setToIDR(product.price ?: 0)
                    product_actual_price.paintFlags = product_actual_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    product_discount.text = " ${product.discountByPercent!!}% OFF"

                    val p = product.price!! * product.discountByPercent!! / 100
                    product_price.text = PaperlessUtil.setToIDR(product.price!! - p.toInt())
                }

                if(product.qty == null){
                    product_stock.text = resources.getString(R.string.infinity)
                    setOnClickListener {
                        val p = product.copy()
                        p.selectedQuantity = 1
                        pvm.addSelectedProduct(p)
                    }
                }else{
                    if(product.qty!! <= 0){
                        product_stock.text = resources.getString(R.string.empty_stock)
                        setOnClickListener {
                            context.showInfoAlert(resources.getString(R.string.empty_stock_info))
                        }
                    }else{
                        setOnClickListener {
                            val p = product.copy()
                            p.selectedQuantity = 1
                            pvm.addSelectedProduct(p)
                        }
                        product_stock.text = "Stok ${product.qty}"
                    }
                }
                product_image.load(product.image)

            }
        }
    }
}