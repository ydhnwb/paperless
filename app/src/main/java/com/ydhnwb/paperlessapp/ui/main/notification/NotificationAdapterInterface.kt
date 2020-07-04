package com.ydhnwb.paperlessapp.ui.main.notification

import com.ydhnwb.paperlessapp.models.Notification

interface NotificationAdapterInterface{
    fun click(notification: Notification)
}