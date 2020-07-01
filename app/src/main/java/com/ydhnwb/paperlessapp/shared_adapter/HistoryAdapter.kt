package com.ydhnwb.paperlessapp.shared_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.OrderHistory
import com.ydhnwb.paperlessapp.ui.manage.history.list_history.ListHistoryAdapterInterface
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.list_item_history.view.*

class HistoryAdapter(private val histories : MutableList<OrderHistory>,
                     private val context: Context,
                     private var isIn : Boolean,
                     private val historyAdapterInterface: ListHistoryAdapterInterface) :
        RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(order : OrderHistory, context: Context, isIn : Boolean){
            with(itemView){
                if(order.orderDetails.isNotEmpty()){
                    history_date.text = order.orderDetails[0].soldAt
                    history_image.load(order.orderDetails[0].productImage)
                }
                history_total_price.text = PaperlessUtil.setToIDR(order.totalPriceWithDiscount!!)
                history_title.text = order.orderDetails.joinToString { d -> d.productName!! }
                if(isIn){
                    if(order.boughtByStore?.id == null && order.boughtByUser?.id == null){
                        history_pov.visibility = View.GONE
                    }else{
                        history_pov.visibility = View.VISIBLE
                        when {
                            order.boughtByUser?.id != null -> {
                                history_pov_store.text = order.boughtByUser!!.name.toString()
                            }
                            order.boughtByStore?.id != null -> {
                                history_pov_store.text = order.boughtByStore!!.name.toString()
                                history_pov_image.load(order.boughtByStore!!.store_logo)
                            }
                            else -> {
                            }
                        }
                    }
                }else{
                    history_pov_store.text = order.sellByStore?.name
                    history_pov_image.load(order.sellByStore?.store_logo)
                }
                setOnClickListener {
                    historyAdapterInterface.click(order)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_history, parent, false))
    }

    override fun getItemCount() = histories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(histories[position], context, isIn)

    fun updateList(it: List<OrderHistory>, i : Boolean){
        isIn = i
        histories.clear()
        histories.addAll(it)
        notifyDataSetChanged()
    }
}