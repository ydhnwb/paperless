package com.ydhnwb.paperlessapp.ui.quickupdate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.list_item_product_alt.view.*

class QuickUpdateProductAdapter (private val products : MutableList<Product>, private val quickUpdate : QuickUpdateInterface)
    : RecyclerView.Adapter<QuickUpdateProductAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product){
            with(itemView){
                product_image.load(product.image)
                product_name.text = product.name
                product_price.text = PaperlessUtil.setToIDR(product.price!!)
                product_category.text = product.category?.name!!
                product_stock.text = if(product.qty == null){
                    "Stok ${resources.getString(R.string.infinity)}"
                }else{
                    "Stok ${product.qty}"
                }
                if (product.discountByPercent == null){
                    product_isPromo.gone()
                }else{
                    product_isPromo.visible()
                    product_isPromo.text = resources.getString(R.string.in_promo)
                }
                setOnClickListener {
                    quickUpdate.click(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_product_alt, parent, false))
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(products[position])

    fun updateList(it: List<Product>){
        products.clear()
        products.addAll(it)
        notifyDataSetChanged()
    }
}