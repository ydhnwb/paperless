package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Invitation
import kotlinx.android.synthetic.main.list_item_invitation.view.*

class InvitationAdapter (private var invitations : MutableList<Invitation>, private var context: Context, private var userView : Boolean) : RecyclerView.Adapter<InvitationAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_invitation, parent, false))
    }

    override fun getItemCount() = invitations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(invitations[position], context, userView)

    fun updateList(ins : List<Invitation>){
        invitations.clear()
        invitations.addAll(ins)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(i : Invitation, context: Context, userPov : Boolean){
            with(itemView){
                if(userPov){
                    invitation_header.text = "${i.requestedByStore?.name} - ${i.date}"
                    invitation_title.text = "${i.requestedByStore?.name} mengundang Anda"
                    invitation_desc.text = "${i.requestedByStore?.name} mengundang Anda untuk menjadi karyawan di tokonya"
                    setOnClickListener {
                        AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme)).apply {
                            setMessage(context.resources.getString(R.string.info_are_you_sure_accept_invite))
                            setPositiveButton(resources.getString(R.string.btn_accept)){d, w ->
                                d.dismiss()
                            }
                            setNegativeButton(resources.getString(R.string.info_cancel)){d, w ->
                                d.dismiss()
                            }
                        }.show()
                    }
                }else{
                    invitation_title.text = "Anda mengundang ${i.to?.name}"
                    invitation_desc.text = "Anda mengundang ${i.to?.name} untuk menjadi karyawan di toko"
                }
            }
        }
    }
}