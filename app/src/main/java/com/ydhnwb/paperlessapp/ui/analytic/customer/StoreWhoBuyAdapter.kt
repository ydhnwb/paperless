package com.ydhnwb.paperlessapp.ui.analytic.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import kotlinx.android.synthetic.main.list_item_store_trans.view.*

class StoreWhoBuyAdapter (private val storeAndTrans : MutableList<StoreAndTransaction>) : RecyclerView.Adapter<StoreWhoBuyAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(storeAndTran : StoreAndTransaction){
            with(itemView){
                store_trans_image.load(storeAndTran.store.store_logo)
                store_trans_name.text = storeAndTran.store.name.toString()
                store_trans_sum.text = "${storeAndTran.sumOfTransaction} transaksi ke toko anda"
                setOnClickListener {
                    println()
                }
            }
        }
    }

    fun updateList(it: List<StoreAndTransaction>){
        storeAndTrans.clear()
        storeAndTrans.addAll(it)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(
        R.layout.list_item_store_trans, parent, false))

    override fun getItemCount() = storeAndTrans.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(storeAndTrans[position])
}