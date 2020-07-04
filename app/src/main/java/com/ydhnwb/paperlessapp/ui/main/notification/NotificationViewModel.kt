package com.ydhnwb.paperlessapp.ui.main.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Notification
import com.ydhnwb.paperlessapp.models.NotificationDummy
import com.ydhnwb.paperlessapp.repositories.NotificationRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class NotificationViewModel(private val notificationRepository: NotificationRepository) : ViewModel(){
    private var state : SingleLiveEvent<NotificationState> = SingleLiveEvent()
    private var notifications = MutableLiveData<List<Notification>>()

    private fun setLoading(){
        state.value = NotificationState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = NotificationState.IsLoading(false)
    }

    private fun toast(message: String){
        state.value = NotificationState.ShowToast(message)
    }

    fun fetchNotification(token: String){
        setLoading()
        notificationRepository.getNotifications(token, object: ArrayResponse<Notification>{
            override fun onSuccess(datas: List<Notification>?) {
                hideLoading()
                datas?.let { notifications.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun listenToNotifications() = notifications
    fun listenToUIState() = state
}

sealed class NotificationState {
    data class AttachToRecycler(var notificationDummies : List<NotificationDummy>) : NotificationState()
    data class IsLoading(var state : Boolean = false) : NotificationState()
    data class ShowToast(var message : String) : NotificationState()
}