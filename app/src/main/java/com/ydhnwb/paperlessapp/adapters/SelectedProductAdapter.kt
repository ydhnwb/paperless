package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.manage_activity.ManageStoreViewModel
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.component_number_picker.view.*
import kotlinx.android.synthetic.main.list_item_selected_product.view.*

class SelectedProductAdapter (private var selectedProducts : MutableList<Product>, private var context: Context, private var parentViewModel : ManageStoreViewModel)
    : RecyclerView.Adapter<SelectedProductAdapter.ViewHolder>(){

    fun updateList(sps : List<Product>){
        selectedProducts.clear()
        selectedProducts.addAll(sps)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_selected_product, parent, false))

    override fun getItemCount() = selectedProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(selectedProducts[position], context, parentViewModel)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context, evm: ManageStoreViewModel){
            with(itemView){
                product_incrementQuantity.setOnClickListener { evm.incrementQuantity(product) }
                product_decrementQuantity.setOnClickListener { evm.decrementQuantity(product) }
                product_name.text = product.name
                product_price.text = PaperlessUtil.setToIDR(product.price!!*product.selectedQuantity!!)
                product_image.load(product.image)
                product_selectedQuantity.text = product.selectedQuantity.toString()
                product_more.setOnClickListener {
                    PopupMenu(context, it).apply {
                        menuInflater.inflate(R.menu.menu_common_selected_product, menu)
                        setOnMenuItemClickListener { menuItems ->
                            when(menuItems.itemId){
                                R.id.menu_delete -> {
                                    evm.deleteSelectedProduct(product)
                                    true
                                }
                                else -> true
                            }
                        }
                    }.show()
                }
                setOnClickListener {
                }
            }
        }
    }
}