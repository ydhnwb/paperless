package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Notification
import kotlinx.android.synthetic.main.list_item_notification.view.*

class NotificationAdapter(private var context: Context, private var notifications : List<Notification>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_notification, parent, false))

    override fun getItemCount() = notifications.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(context, notifications[position])

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(context: Context, notif : Notification){
            itemView.notif_from_and_when.text = "${notif.from} - ${notif.date}"
            itemView.notif_title.text = notif.title
            itemView.notif_desc.text = notif.description
            itemView.notif_image.load(notif.image){ transformations(CircleCropTransformation())}
            itemView.setOnClickListener {
                Toast.makeText(context, notif.id.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}