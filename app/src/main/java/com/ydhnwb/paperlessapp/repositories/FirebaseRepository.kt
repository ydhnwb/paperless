package com.ydhnwb.paperlessapp.repositories

import com.google.firebase.iid.FirebaseInstanceId
import com.ydhnwb.paperlessapp.utilities.SingleResponse

interface FirebaseContract{
    fun getToken(listener: SingleResponse<String>)
}

class FirebaseRepository : FirebaseContract{
    override fun getToken(listener: SingleResponse<String>) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            when{
                it.isSuccessful -> {
                    it.result?.let { result ->
                        listener.onSuccess(result.token)
                    } ?: listener.onFailure(Error("Failed to get firebase token"))
                }
                !it.isSuccessful -> listener.onFailure(Error("Cannot get firebase token"))
                else -> listener.onFailure(Error("Exception happen when get firebase token"))
            }
        }
    }


}