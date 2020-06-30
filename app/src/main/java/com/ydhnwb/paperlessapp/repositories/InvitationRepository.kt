package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.Invitation
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleResponse
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface InvitationContract {
    fun invitationSent(token: String, storeId: Int, listener: ArrayResponse<Invitation>)
    fun invite(token : String, storeId: Int, role: Boolean, to : Int, listener: SingleResponse<Invitation>)
    fun invitationIn(token: String, listener: ArrayResponse<Invitation>)
    fun acceptInvitation(token: String, invitationId: String, listener: SingleResponse<Invitation>)
    fun rejectInvitation(token: String, invitationId: String, listener: SingleResponse<Invitation>)
}

class InvitationRepository (private val api: ApiService) : InvitationContract {

    override fun invitationSent(token: String, storeId: Int, listener: ArrayResponse<Invitation>) {
        api.invitation_sent(token, storeId).enqueue(object : Callback<WrappedListResponse<Invitation>>{
            override fun onFailure(call: Call<WrappedListResponse<Invitation>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedListResponse<Invitation>>, response: Response<WrappedListResponse<Invitation>>) {
                when {
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun invite(token: String, storeId: Int, role: Boolean, to: Int, listener: SingleResponse<Invitation>) {
        val r = if(role) 1 else 0
        api.invite(token, storeId, r, to).enqueue(object : Callback<WrappedResponse<Invitation>>{
            override fun onFailure(call: Call<WrappedResponse<Invitation>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Invitation>>, response: Response<WrappedResponse<Invitation>>) {
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

    override fun invitationIn(token: String, listener: ArrayResponse<Invitation>) {
        api.invitation_in(token).enqueue(object : Callback<WrappedListResponse<Invitation>> {
            override fun onFailure(call: Call<WrappedListResponse<Invitation>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedListResponse<Invitation>>, response: Response<WrappedListResponse<Invitation>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun acceptInvitation(token: String, invitationId: String, listener: SingleResponse<Invitation>) {
        api.invitation_acc(token, invitationId).enqueue(object :
            Callback<WrappedResponse<Invitation>> {
            override fun onFailure(call: Call<WrappedResponse<Invitation>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Invitation>>, response: Response<WrappedResponse<Invitation>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body()
                        if(b!!.status) listener.onSuccess(b.data) else listener.onFailure(Error("Cannot accept invitation"))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun rejectInvitation(token: String, invitationId: String, listener: SingleResponse<Invitation>) {
        api.invitation_reject(token, invitationId).enqueue(object :
            Callback<WrappedResponse<Invitation>> {
            override fun onFailure(call: Call<WrappedResponse<Invitation>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Invitation>>, response: Response<WrappedResponse<Invitation>>) {
                when{
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