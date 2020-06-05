package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.invitation_activity.InvitationViewModel
import com.ydhnwb.paperlessapp.models.Invitation
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.list_item_invitation.view.*

class InvitationAdapter (private var invitations : MutableList<Invitation>, private var context: Context, private var userView : Boolean, private val invitationViewModel : InvitationViewModel)
    : RecyclerView.Adapter<InvitationAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_invitation, parent, false))
    }

    override fun getItemCount() = invitations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(invitations[position], context, userView, invitationViewModel)

    fun updateList(ins : List<Invitation>){
        invitations.clear()
        invitations.addAll(ins)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(i : Invitation, context: Context, userPov : Boolean, ivm : InvitationViewModel){
            with(itemView){
                if(userPov){
                    invitation_image.load(i.requestedByStore?.store_logo)
                    invitation_header.text = "${i.requestedByStore?.name} - ${i.date}"
                    invitation_title.text = "${i.requestedByStore?.name} mengundang Anda"
                    invitation_desc.text = "${i.requestedByStore?.name} mengundang Anda untuk menjadi karyawan di tokonya"
                    setOnClickListener {
                        AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme)).apply {
                            setMessage(context.resources.getString(R.string.info_are_you_sure_accept_invite))
                            setPositiveButton(resources.getString(R.string.btn_accept)){d, _ ->
                                ivm.acceptInvitation(PaperlessUtil.getToken(context), i.id.toString())
                                d.dismiss()
                            }
                            setNegativeButton(resources.getString(R.string.info_reject)){d, _ ->
                                ivm.rejectInvitation(PaperlessUtil.getToken(context), i.id.toString())
                                d.dismiss()
                            }
                            setNeutralButton(resources.getString(R.string.info_cancel)){
                                dialog, _ -> dialog.dismiss()
                            }
                        }.show()
                    }
                }else{
                    if(i.status == null){
                        invitation_title.text = "Anda mengundang ${i.to?.name}"
                        invitation_status.setTextColor(ContextCompat.getColor(context, R.color.colorGrey))
                        invitation_desc.text = "Anda mengundang ${i.to?.name} untuk menjadi karyawan di toko"
                        invitation_status.text = "Menunggu"
                    }else if(i.status != null){
                        if(!i.status!!){
                            invitation_status.setTextColor(ContextCompat.getColor(context, R.color.colorOrange))
                            invitation_status.text = "Ditolak"
                            invitation_title.text = "Undangan ke ${i.to?.name} telah ditolak"
                            invitation_desc.text = "Undangan anda kepada ${i.to?.name} telah ditolak. Anda bisa mencoba untuk mengundangnya lagi."
                        }else if(i.status!!){
                            invitation_status.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                            invitation_status.text = "Diterima"
                            invitation_title.text = "Undangan kepada ${i.to?.name} telah diterima"
                            invitation_desc.text = "Undangan anda kepada ${i.to?.name} telah diterima"
                        }
                    }
                }
            }
        }
    }
}