package com.ydhnwb.paperlessapp.contracts.fragments

import com.ydhnwb.paperlessapp.models.Notification

interface NotificationFragmentContract {
    interface View {
        fun isLoading(state : Boolean)
        fun attachToRecycler(notifications : List<Notification>)
        fun toast(message : String)
    }

    interface Interactor {
        fun load()
        fun destroy()
    }
}
