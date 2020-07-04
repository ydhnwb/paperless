package com.ydhnwb.paperlessapp.ui.main.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Notification
import kotlinx.android.synthetic.main.list_item_notification.view.*

class NotificationAdapter(private val notifications : MutableList<Notification>, private val notifInterface: NotificationAdapterInterface) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_notification, parent, false))

    override fun getItemCount() = notifications.size

    fun updateList(nfs : List<Notification>){
        notifications.clear()
        notifications.addAll(nfs)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(notifications[position])

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(notif : Notification){
            with(itemView){
                notif_from_and_when.text = "${notif.storeSender?.name} - ${notif.date}"
                notif_title.text = notif.title
                notif_desc.text = notif.subtitle
                notif_image.load(notif.storeSender?.store_logo){ transformations(CircleCropTransformation())}
                setOnClickListener {
                    notifInterface.click(notif)
                }
            }

        }
    }
}