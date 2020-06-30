package com.ydhnwb.paperlessapp.shared_adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.detail_product.DetailProductActivity
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.list_item_catalog.view.*

class CatalogAdapter (private var catalogs : MutableList<Product>, private var context: Context) : RecyclerView.Adapter<CatalogAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_catalog, parent, false))
    }

    override fun getItemCount() = catalogs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(catalogs[position], context)

    fun updateList(i : List<Product>){
        catalogs.clear()
        catalogs.addAll(i)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(catalog : Product, context: Context){
            with(itemView){
                if(catalog.discountByPercent == null){
                    catalog_store_discount_layout.gone()
                    catalog_price.text = PaperlessUtil.setToIDR(catalog.price!!)
                }else{
                    catalog_store_discount_layout.visible()
                    catalog_store_actual_price.text = PaperlessUtil.setToIDR(catalog.price?:0)
                    catalog_store_actual_price.paintFlags = catalog_store_actual_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    catalog_store_discount.text = " ${catalog.discountByPercent!!}% OFF"
                    val p = catalog.price!! * catalog.discountByPercent!! / 100
                    catalog_price.text = PaperlessUtil.setToIDR(catalog.price!! - p.toInt())
                }

                catalog_image.load(catalog.image)
                catalog_name.text = catalog.name
                if (catalog.store != null){
                    catalog_store_name.text = catalog.store?.name!!
                }else{
                    catalog_store_name.visibility = View.GONE
                }
                setOnClickListener {
                    context.startActivity(Intent(context, DetailProductActivity::class.java).apply {
                        putExtra("product", catalog)
                    })
                }
            }
        }
    }
}