package com.ydhnwb.paperlessapp.ui.search_user

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.fragments.dialog.InviteDialog
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import kotlinx.android.synthetic.main.list_item_employee.view.*

class SearchResultUserAdapter (private var users : MutableList<User>, private var context: Context, private var store : Store) : RecyclerView.Adapter<SearchResultUserAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_employee, parent, false)
        )

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(users[position], context, store)

    fun updateList(us : List<User>){
        users.clear()
        users.addAll(us)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(user: User, context: Context, store: Store){
            with(itemView){
                employee_image.load(user.image)
                employee_role.gone()
                employee_more.gone()
                employee_name.text = user.name
                setOnClickListener {
                    val c = context as AppCompatActivity
                    InviteDialog.instance(user, store).show(c.supportFragmentManager, "invite_dialog")
                }
            }
        }
    }
}