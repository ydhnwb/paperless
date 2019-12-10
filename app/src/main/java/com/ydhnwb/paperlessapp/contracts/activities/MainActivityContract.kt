package com.ydhnwb.paperlessapp.contracts.activities

interface MainActivityContract {
    interface View {
        fun showIntro()
        fun showLogin()
        fun showSheet()
        fun closeSheet()
        fun toast(message : String)
    }
    interface Interactor {
        fun authChecker()
        fun destroy()
    }
}