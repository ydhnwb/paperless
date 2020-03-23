package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.list_item_selected_product.view.*

class SelectedProductAdapter (private var selectedProducts : MutableList<Product>, private var context: Context, private var productViewModel : ProductViewModel)
    : RecyclerView.Adapter<SelectedProductAdapter.ViewHolder>(){

    fun updateList(sps : List<Product>){
        selectedProducts.clear()
        selectedProducts.addAll(sps)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(
        R.layout.list_item_selected_product, parent, false))

    override fun getItemCount() = selectedProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(selectedProducts[position], context, productViewModel)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context, productViewModel: ProductViewModel){
            itemView.product_name.text = product.name
            itemView.product_image.load(product.image)
            itemView.setOnClickListener {
            }
        }
    }
}