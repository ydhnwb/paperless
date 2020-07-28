package com.ydhnwb.paperlessapp.fragments.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
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

    private val invitationViewModel : InvitationDialogViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.dialog_invitation, container)

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

    private fun fill(user : User){
        view!!.user_image.load(user.image)
        view!!.user_name.text = user.name
    }
    private fun sendInvitationBehavior(user: User, store: Store){
        requireView().btn_add_cashier.setOnClickListener {
            PaperlessUtil.getToken(requireActivity())?.let { it1 -> invitationViewModel.invite(it1, store.id!!, false, user.id!!) }
        }
        requireView().btn_add_staff.setOnClickListener {
            PaperlessUtil.getToken(activity!!)?.let { it1 -> invitationViewModel.invite(it1, store.id!!, true, user.id!!) }
        }
    }
    private fun alert(m : String) = AlertDialog.Builder(requireActivity()).apply {
        setMessage(m)
        setPositiveButton(resources.getString(R.string.info_understand)){ d, _ -> d.dismiss()}
    }.show()

    private fun handleUIState(it: InvitationDialogState){
        when(it){
            is InvitationDialogState.Success -> {
                alert(resources.getString(R.string.info_successfully_invite))
                this.dismiss()
            }
            is InvitationDialogState.ShowAlert -> alert(it.message)
            is InvitationDialogState.IsLoading -> {
                with(requireView()){
                    btn_add_staff.isEnabled = !it.state
                    btn_add_cashier.isEnabled = !it.state
                    if (it.state){ loading.visibility = View.VISIBLE }else{ loading.visibility = View.GONE }
                }
            }
        }
    }
}