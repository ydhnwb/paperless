package com.ydhnwb.paperlessapp.ui.main.profile

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.invitation.InvitationActivity
import com.ydhnwb.paperlessapp.ui.login.LoginActivity
import com.ydhnwb.paperlessapp.ui.user_history.UserHistoryActivity
import com.ydhnwb.paperlessapp.models.Preference
import com.ydhnwb.paperlessapp.ui.update_profile.UpdateProfileActivity
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.list_item_preference.view.*

class PreferenceAdapter (private var context: Context, private var prefs : List<Preference>) : RecyclerView.Adapter<PreferenceAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_preference, parent, false)
        )

    override fun getItemCount() = prefs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(prefs[position], context, position)

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(pref : Preference, context: Context, i : Int){
            itemView.pref_image.load(pref.image)
            itemView.pref_name.text = context.getString(pref.name)
            itemView.setOnClickListener {
                when(i){
                    0 -> context.startActivity(Intent(context, UpdateProfileActivity::class.java))
                    1 -> context.startActivity(Intent(context, InvitationActivity::class.java))
                    2 -> context.startActivity(Intent(context, UserHistoryActivity::class.java))
                    3 -> {
                        AlertDialog.Builder(context).apply {
                            setMessage(context.resources.getString(R.string.ask_logout))
                            setPositiveButton(context.resources.getString(R.string.logout)){ d, _ ->
                                PaperlessUtil.clearToken(context)
                                d.dismiss()
                                context.startActivity(Intent(context, LoginActivity::class.java))
                                context as AppCompatActivity
                                context.finish()
                            }
                            setNegativeButton(context.resources.getString(R.string.info_cancel)){ d, _ ->
                                d.cancel()
                            }
                        }.show()
                    }
                    else -> println("Ya")
                }
            }
        }
    }
}