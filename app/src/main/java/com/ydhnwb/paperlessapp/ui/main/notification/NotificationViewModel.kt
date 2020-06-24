package com.ydhnwb.paperlessapp.ui.main.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Notification
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class NotificationViewModel : ViewModel(){
    private var state : SingleLiveEvent<NotificationState> = SingleLiveEvent()
    private var notifications = MutableLiveData<List<Notification>>()

    fun fetchNotification(){
        state.value = NotificationState.IsLoading(true)
        val list = mutableListOf<Notification>().apply {
            add(Notification(1, "Prieyudha Akadita S", "Menambahkan anda sebagai karyawan di tokonya", "Prieyudha Akadita S menambahkan anda sebagai karyawan di tokonya", "19 mins", "https://avatars1.githubusercontent.com/u/26734262?s=460&v=4"))
            add(Notification(2, "Tirta Aries", "Laporan bulanan", "Laporan bulanan telah dibuat secara otomatis oleh sistem", "23 mins", "https://media-cdn.tripadvisor.com/media/photo-s/0f/6e/e3/0e/interior-del-cafe-the.jpg"))
            add(Notification(3, "Tirta Aries", "Laporan bulanan", "Laporan bulanan telah dibuat secara otomatis oleh sistem", "31 days", "https://media-cdn.tripadvisor.com/media/photo-s/0f/6e/e3/0e/interior-del-cafe-the.jpg"))
            add(Notification(4, "Bambang Gentolet", "Menambahkan anda sebagai kasir di tokonya", "Bambang Gentolet menambahkan anda sebagai kasir di tokonya", "40 days", "https://www.biography.com/.image/t_share/MTQxNDkyMjM0OTA1MjAwMzc5/edward_snowden_getty_images_170248179_photo_by_the_guardian_via_getty_images_croppedjpg.jpg"))
            add(Notification(5, "Seblak Gledeg", "Laporan bulanan", "Laporan bulanan telah dibuat secara otomatis oleh sistem", "48 days", "https://2.bp.blogspot.com/-weQyFcB7zHU/WckBJLcRvgI/AAAAAAAABck/UTceOYdW-HUGlhFMgcPDlQy-VcLLxYeNgCEwYBhgL/s1600/fullarea.jpg"))
        }
        notifications.postValue(list)
        state.value = NotificationState.AttachToRecycler(list)
        state.value = NotificationState.IsLoading(false)
    }

    fun listenToNotifications() = notifications
    fun listenToUIState() = state
}

sealed class NotificationState {
    data class AttachToRecycler(var notifications : List<Notification>) : NotificationState()
    data class IsLoading(var state : Boolean = false) : NotificationState()
    data class ShowToast(var message : String) : NotificationState()
}