package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleResponse
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface UserContract {
    fun search(token: String, query: String, listener: ArrayResponse<User>)
    fun getCurrentProfile(token: String, listener: SingleResponse<User>)
    fun register(name: String, email: String, password: String, fcmToken: String, listener: SingleResponse<User>)
    fun login(email: String, password: String, listener: SingleResponse<User>)
}

class UserRepository (private val api: ApiService) : UserContract {

    override fun search(token: String, query: String, listener: ArrayResponse<User>) {
        api.user_search(token,query).enqueue(object: Callback<WrappedListResponse<User>>{
            override fun onFailure(call: Call<WrappedListResponse<User>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedListResponse<User>>, response: Response<WrappedListResponse<User>>) {
                when {
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun getCurrentProfile(token: String, listener: SingleResponse<User>) {
        api.profile(token).enqueue(object: Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun register(name: String, email: String, password: String, fcmToken: String, listener: SingleResponse<User>) {
        api.register(name, email, password, fcmToken).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body()
                        b?.let {
                            if(it.status) listener.onSuccess(it.data) else listener.onFailure(Error(it.message))
                        }
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun login(email: String, password: String, listener: SingleResponse<User>) {
        api.login(email, password).enqueue(object : Callback<WrappedResponse<User>> {
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                when {
                    response.isSuccessful -> {
                        val b = response.body()
                        if(b!!.status) listener.onSuccess(b.data) else listener.onFailure(Error(b.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }
}