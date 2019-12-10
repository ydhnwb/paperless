package com.ydhnwb.paperlessapp.contracts.activities

interface LoginActivityContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun success(token : String)
        fun failed(message : String)
        fun toast(message : String)
        fun emailError(message : String?)
        fun passwordError(message : String?)
    }
    interface Interactor {
        fun validate(email: String, password: String) : Boolean
        fun doLogin(email : String, password : String)
        fun destroy()
    }
}