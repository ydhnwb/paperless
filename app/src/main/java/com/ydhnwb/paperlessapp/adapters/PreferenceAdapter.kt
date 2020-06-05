package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.invitation_activity.InvitationActivity
import com.ydhnwb.paperlessapp.activities.login_activity.LoginActivity
import com.ydhnwb.paperlessapp.models.Preference
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.list_item_preference.view.*

class PreferenceAdapter (private var context: Context, private var prefs : List<Preference>) : RecyclerView.Adapter<PreferenceAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_preference, parent, false))

    override fun getItemCount() = prefs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(prefs[position], context, position)

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(pref : Preference, context: Context, i : Int){
            itemView.pref_image.load(pref.image)
            itemView.pref_name.text = context.getString(pref.name)
            itemView.setOnClickListener {
                when(i){
                    1 -> context.startActivity(Intent(context, InvitationActivity::class.java))
                    3 -> {
                        PaperlessUtil.clearToken(context)
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        context as AppCompatActivity
                        context.finish()
                    }
                    else -> println("Ya")
                }
            }
        }
    }
}