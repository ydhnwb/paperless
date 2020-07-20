package com.ydhnwb.paperlessapp.ui.analytic.performance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.UserAndTransaction
import kotlinx.android.synthetic.main.list_item_performance.view.*

class PerformanceAnalyticAdapter (private val employeePerformance : MutableList<UserAndTransaction>) : RecyclerView.Adapter<PerformanceAnalyticAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(e: UserAndTransaction){
            with(itemView){
                performance_name.text = e.name
                performance_desc.text = "Melayani ${e.transactionNum} transaksi"
            }
        }
    }

    fun updateList(x : List<UserAndTransaction>){
        employeePerformance.clear()
        employeePerformance.addAll(x)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_performance, parent, false))
    }

    override fun getItemCount() = employeePerformance.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(employeePerformance[position])
}