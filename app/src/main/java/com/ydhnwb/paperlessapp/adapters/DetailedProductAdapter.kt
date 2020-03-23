package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Product
import kotlinx.android.synthetic.main.list_item_product_alt.view.*

class DetailedProductAdapter (private var products : MutableList<Product>, private var context: Context) : RecyclerView.Adapter<DetailedProductAdapter.ViewHolder>(){
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context){
            itemView.product_image.load(product.image)
            itemView.product_name.text = product.name
            itemView.product_price.text = "Rp. 18.000"
        }
    }

    fun updateList(prds : List<Product>){
        products.clear()
        products.addAll(prds)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_product_alt, parent, false))
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(products[position], context)
}