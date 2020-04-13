package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.list_item_employee.view.*

class SearchResultUserAdapter (private var users : MutableList<User>, private var context: Context, private var userViewModel: UserViewModel) :
        RecyclerView.Adapter<SearchResultUserAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_employee, parent, false))
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(users[position], context, userViewModel)

    fun updateList(us : List<User>){
        users.clear()
        users.addAll(us)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(user: User, context: Context, userViewModel: UserViewModel){
            itemView.employee_name.text = user.name
            itemView.setOnClickListener {
                Toast.makeText(context, user.name, Toast.LENGTH_LONG).show()
            }
        }

    }


}