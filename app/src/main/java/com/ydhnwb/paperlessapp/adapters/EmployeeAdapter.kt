package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.fragments.manage.employee_fragment.EmployeeViewModel
import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.list_item_employee.view.*

class EmployeeAdapter (private val employees: MutableList<Employee>, private val context: Context,
                       private val employeeViewModel: EmployeeViewModel, private val storeId: String)
    : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>(){
    fun updateList(e: List<Employee>){
        employees.clear()
        employees.addAll(e)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_employee, parent, false))

    override fun getItemCount() = employees.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(employees[position], context, employeeViewModel, storeId)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(employee: Employee, context: Context, evm: EmployeeViewModel, storeId : String){
            with(itemView){
                employee_name.text = employee.user!!.name
                employee_more.setOnClickListener {
                    PopupMenu(context, it).apply {
                        menuInflater.inflate(R.menu.menu_employee_adapter, menu)
                        setOnMenuItemClickListener { menuItems ->
                            when(menuItems.itemId){
                                R.id.menu_delete -> {
                                    evm.removeEmployee(PaperlessUtil.getToken(context), storeId, employee.id.toString())
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