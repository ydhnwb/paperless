package com.ydhnwb.paperlessapp.ui.analytic.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.paperlessapp.R
import kotlinx.android.synthetic.main.list_item_store_trans.view.*

class UserWhoBuyAdapter (private val userAndTrans : MutableList<UserAndTransaction>) : RecyclerView.Adapter<UserWhoBuyAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(userAndTran : UserAndTransaction){
            with(itemView){
                store_trans_name.text = userAndTran.user.name.toString()
                store_trans_sum.text = "${userAndTran.sumOfTransaction} transaksi ke toko anda"
                setOnClickListener {
                    println()
                }
            }
        }
    }

    fun updateList(it: List<UserAndTransaction>){
        userAndTrans.clear()
        userAndTrans.addAll(it)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
        R.layout.list_item_store_trans, parent, false))

    override fun getItemCount() = userAndTrans.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(userAndTrans[position])
}