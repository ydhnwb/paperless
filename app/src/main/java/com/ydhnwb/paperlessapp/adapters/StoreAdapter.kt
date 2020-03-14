package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.ManageActivity
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.webservices.ApiClient
import kotlinx.android.synthetic.main.list_item_store.view.*

class StoreAdapter(private var stores : MutableList<Store>, private var context: Context) : RecyclerView.Adapter<StoreAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        return if(viewType == 1){
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_store_more, parent, false))
        }else{
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_store, parent, false))
        }

    }
    fun updateList(sts : List<Store>){
        stores.clear()
        stores.addAll(sts)
        notifyDataSetChanged()
    }

    override fun getItemCount() = stores.size

    override fun getItemViewType(i: Int): Int {
        return if(itemCount > 5){
            if(itemCount == i+1){
                1
            }else{
                0
            }
        }else {
            0
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) != 1){
            holder.bind(stores[position], context)
        }else{
            holder.bindMore(context)
        }
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(store : Store, context : Context){
            itemView.store_name.text = store.name.toString()
            println("${ApiClient.END_POINT}/${store.store_logo}")
            itemView.store_logo.load("${ApiClient.END_POINT}images/${store.store_logo}")
            itemView.setOnClickListener {
                context.startActivity(Intent(context, ManageActivity::class.java))
            }
        }
        fun bindMore(context: Context){
            itemView.setOnClickListener {
                Toast.makeText(context, "More", Toast.LENGTH_LONG).show()
            }
        }
    }
}