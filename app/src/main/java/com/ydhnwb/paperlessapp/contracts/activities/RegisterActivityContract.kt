package com.ydhnwb.paperlessapp.contracts.activities

interface RegisterActivityContract {
    interface View {
        fun toast(message : String)
        fun success(email : String)
        fun failed()
        fun errorName(err : String?)
        fun errorEmail(err : String?)
        fun errorPassword(err : String?)
        fun errorPasswordConfirm(err : String?)
        fun isLoading(state : Boolean)
    }
    interface Interactor {
        fun validate(name : String, email : String, password : String, conf_password : String) : Boolean
        fun destroy()
        fun register(name : String, email : String, password : String)
    }
}