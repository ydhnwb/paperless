package com.ydhnwb.paperlessapp.ui.manage.etalase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.manage.ManageStoreViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
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
                product_price.text = PaperlessUtil.setToIDR(product.price!!)
                product_image.load(product.image)
                setOnClickListener {
                    val p = product.copy()
                    p.selectedQuantity = 1
                    pvm.addSelectedProduct(p)
                }
            }
        }
    }
}