package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
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
                catalog_image.load(catalog.image)
                catalog_name.text = catalog.name
                catalog_price.text = PaperlessUtil.setToIDR(catalog.price!!)
                catalog_store_name.text = catalog.store?.name!!
                setOnClickListener {
                    println()
                }
            }
        }
    }
}