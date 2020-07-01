package com.ydhnwb.paperlessapp.ui.main.explore

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_promo_header.view.*

class HeaderItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    fun bind(title: String){
        itemView.promo_header_category.text = title
    }
}