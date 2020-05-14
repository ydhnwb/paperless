package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.User
import kotlinx.android.synthetic.main.list_item_employee.view.*


class EmployeeAdapter (private var employees: MutableList<User>, private var context: Context) : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>(){
    fun updateList(e: List<User>){
        employees.clear()
        employees.addAll(e)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_employee, parent, false))

    override fun getItemCount() = employees.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(employees[position], context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(employee: User, context: Context){
            with(itemView){
                employee_name.text = employee.name
                employee_more.setOnClickListener {
                    PopupMenu(context, it).apply {
                        menuInflater.inflate(R.menu.menu_common_more, menu)
                        setOnMenuItemClickListener { menuItems ->
                            when(menuItems.itemId){
                                R.id.menu_detail -> {
                                    Toast.makeText(context, employee.name,Toast.LENGTH_LONG).show()
                                    true
                                }
                                else -> true
                            }
                        }
                    }.show()
                }
            }
        }
    }
}