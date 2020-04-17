package com.ydhnwb.paperlessapp.fragments.dialog

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.InvitationState
import com.ydhnwb.paperlessapp.viewmodels.InvitationViewModel
import kotlinx.android.synthetic.main.dialog_invitation.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InviteDialog : DialogFragment(){
    companion object {
        fun instance(user: User, store: Store) : InviteDialog {
            val args = Bundle()
            args.putParcelable("user", user)
            args.putParcelable("store", store)
            return InviteDialog().apply {
                arguments = args
            }
        }
    }

    private val invitationViewModel : InvitationViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_invitation, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { arg ->
            invitationViewModel.listenToUIState().observer(viewLifecycleOwner, Observer {
                handleUIState(it)
            })
            val store : Store = arg.getParcelable<Store>("store")!!
            val user : User = arg.getParcelable("user")!!
            fill(user)
            sendInvitationBehavior(user, store)
        }
    }

    private fun fill(user : User){ view!!.user_name.text = user.name }
    private fun sendInvitationBehavior(user: User, store: Store){
        view!!.btn_add_cashier.setOnClickListener {
            invitationViewModel.invite(PaperlessUtil.getToken(activity!!), store.id!!, false, user.id!!)
        }
        view!!.btn_add_staff.setOnClickListener {
            invitationViewModel.invite(PaperlessUtil.getToken(activity!!), store.id!!, true, user.id!!)
        }
    }
    private fun toast(m : String) = Toast.makeText(activity, m, Toast.LENGTH_LONG).show()
    private fun handleUIState(it: InvitationState){
        when(it){
            is InvitationState.Success -> {
                toast(resources.getString(R.string.info_successfully_invite))
                this.dismiss()
            }
            is InvitationState.ShowToast -> toast(it.message)
            is InvitationState.ShowAlert -> {
                AlertDialog.Builder(activity).apply {
                    setMessage(it.message)
                    setPositiveButton(resources.getString(R.string.info_understand)){ d, which ->
                        d.dismiss()
                    }
                }.show()
            }
            is InvitationState.IsLoading -> {
                with(view!!){
                    btn_add_staff.isEnabled = !it.state
                    btn_add_cashier.isEnabled = !it.state
                    if (it.state){
                        loading.visibility = View.VISIBLE
                    }else{
                        loading.visibility = View.GONE
                    }
                }
            }
        }
    }
}