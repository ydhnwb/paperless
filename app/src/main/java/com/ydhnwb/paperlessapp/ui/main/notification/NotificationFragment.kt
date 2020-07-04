package com.ydhnwb.paperlessapp.ui.main.notification

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Notification
import com.ydhnwb.paperlessapp.ui.invitation.InvitationActivity
import com.ydhnwb.paperlessapp.ui.user_history.UserHistoryActivity
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationFragment : Fragment(R.layout.fragment_notifications), NotificationAdapterInterface {
    private val notificationViewModel: NotificationViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observe()
     }

    private fun handleNotif(notifications: List<Notification>){
        requireView().rv_notification.adapter?.let { adapter ->
            if(adapter is NotificationAdapter){
                adapter.updateList(notifications)
            }
        }
    }

    private fun observe(){
        observeState()
        observeNotifications()
    }

    private fun observeState() = notificationViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleState(it) })
    private fun observeNotifications() = notificationViewModel.listenToNotifications().observe(viewLifecycleOwner, Observer { handleNotif(it) })

    private fun handleState(it: NotificationState){
        when(it){
            is NotificationState.ShowToast -> requireActivity().showToast(it.message)
            is NotificationState.IsLoading -> isLoading(it.state)
        }
    }

    private fun setupRecyclerView(){
        requireView().rv_notification.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter =
                NotificationAdapter(
                    mutableListOf(),
                    this@NotificationFragment
                )
        }
    }

    private fun isLoading(state: Boolean) {
        if(state) requireView().loading_bar.visible() else requireView().loading_bar.gone()
    }

    override fun onResume() {
        super.onResume()
        PaperlessUtil.getToken(requireActivity())?.let { notificationViewModel.fetchNotification(it) }
    }

    override fun click(notification: Notification) {
        when(notification.type){
            1 -> startActivity(Intent(requireActivity(), InvitationActivity::class.java))
            2 -> startActivity(Intent(requireActivity(), UserHistoryActivity::class.java))
            else -> println()
        }
    }
}