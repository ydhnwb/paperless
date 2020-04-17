package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.InvitationActivity
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.StoreMenu
import kotlinx.android.synthetic.main.list_item_store_menu.view.*

class StoreMenuAdapter (private var storeMenus : List<StoreMenu>, private var context : Context, private val store : Store) : RecyclerView.Adapter<StoreMenuAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_store_menu, parent, false))
    }

    override fun getItemCount() = storeMenus.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(storeMenus[position], context, position, store)

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(storeMenu: StoreMenu, context: Context, i : Int, store : Store){
            with(itemView){
                store_menu_title.text = storeMenu.title
                store_menu_img.load(storeMenu.image)
                store_menu_bg.setBackgroundColor(storeMenu.color)
                setOnClickListener {
                    when(i){
                        0 -> {
                            Toast.makeText(context, "Laporan", Toast.LENGTH_LONG).show()
                        }
                        1 -> context.startActivity(Intent(context, InvitationActivity::class.java).apply {
                            putExtra("store", store)
                        })
                        else -> println("Ya")
                    }
                }
            }
        }
    }
}