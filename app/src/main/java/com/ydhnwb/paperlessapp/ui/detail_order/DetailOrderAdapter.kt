package com.ydhnwb.paperlessapp.ui.detail_order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.OrderHistoryDetail
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.list_item_detail_order_item.view.*

class DetailOrderAdapter (private val products : List<OrderHistoryDetail> = mutableListOf(), private val detailOrderAdapterInterface: DetailOrderAdapterInterface, private val isPurchasement: Boolean) : RecyclerView.Adapter<DetailOrderAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: OrderHistoryDetail){
            with(itemView){
                order_detail_price.text = if(product.discountByPercent != null){
                    val temp = product.productPrice!! * product.discountByPercent!! / 100
                    PaperlessUtil.setToIDR((product.productPrice!! - temp).toInt())
                }else{
                    PaperlessUtil.setToIDR(product.productPrice!!)
                }
                if(isPurchasement){
                    order_detail_update.visible()
                    order_detail_update.setOnClickListener {
                        detailOrderAdapterInterface.goToStockActivity(product)
                    }
                }else{
                    order_detail_update.gone()
                }

                order_detail_qty.text = "${product.quantity}x"
                order_detail_name.text = product.productName.toString()
                order_detail_imageView.load(product.productImage)
                setOnClickListener { println() }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_detail_order_item, parent, false))
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(products[position])
}