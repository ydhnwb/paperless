package com.ydhnwb.paperlessapp.ui.all_my_store

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.ui.manage.ManageActivity
import kotlinx.android.synthetic.main.list_item_store.view.*

class AllMyStoreAdapter(private val stores : MutableList<Store>,private val allMyStoreInterface: AllMyStoreInterface) : RecyclerView.Adapter<AllMyStoreAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_store, parent, false))
    }

    fun updateList(sts : List<Store>){
        stores.clear()
        stores.addAll(sts)
        notifyDataSetChanged()
    }

    override fun getItemCount() = stores.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(stores[position])

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(store : Store){
            with(itemView){
                store_name.text = store.name.toString()
                store_logo.load(store.store_logo)
                setOnClickListener {
                    context.startActivity(Intent(context, ManageActivity::class.java).apply {
                        putExtra("STORE", store)
                    })
                }

                store_more.setOnClickListener {
                    allMyStoreInterface.click(store, it)
                }
            }
        }
    }
}