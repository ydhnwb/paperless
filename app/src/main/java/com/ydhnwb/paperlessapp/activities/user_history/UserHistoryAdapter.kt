package com.ydhnwb.paperlessapp.activities.user_history

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.detail_order_activity.DetailOrderActivity
import com.ydhnwb.paperlessapp.models.OrderHistory
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.list_item_history.view.*

class UserHistoryAdapter (private val orders : MutableList<OrderHistory>, private val context : Context) : RecyclerView.Adapter<UserHistoryAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_history, parent, false))
    }

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(orders[position])

    fun updateList(it: List<OrderHistory>){
        orders.clear()
        orders.addAll(it)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(order: OrderHistory){
            with(itemView){
                history_pov_store.text = order.sellByStore?.name.toString()
                history_pov_image.load(order.sellByStore?.store_logo)
                history_image.load(order.orderDetails[0].productImage)
                history_title.text = order.orderDetails.joinToString { d -> d.productName!! }
                history_total_price.text = PaperlessUtil.setToIDR(order.orderDetails.sumBy { detail -> detail.quantity!! * detail.productPrice!! })
                history_date.text = order.date.toString()
                setOnClickListener {
                    context.startActivity(Intent(context, DetailOrderActivity::class.java).apply {
                        putExtra("ORDER", order)
                    })
                }
            }
        }
    }
}