package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Category
import kotlinx.android.synthetic.main.list_item_category.view.*

class CategoryAdapter(private var categories : MutableList<Category>, private var context: Context) :
        RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_category, parent, false))
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(categories[position], context)

    fun updateList(cts : List<Category>){
        categories.clear()
        categories.addAll(cts)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(category : Category, context: Context){
            with(itemView){
                category_name.text = category.name
                setOnClickListener {
                    Toast.makeText(context, category.id.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}