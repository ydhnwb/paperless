package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Invitation
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvitationViewModel (private val api : ApiService) : ViewModel(){
    private var state : SingleLiveEvent<InvitationState> = SingleLiveEvent()
    private var invitationsSent = MutableLiveData<List<Invitation>>()
    private var invitationsIn = MutableLiveData<List<Invitation>>()

    fun setLoading(){ state.value = InvitationState.IsLoading(true) }
    fun hideLoading(){ state.value = InvitationState.IsLoading(false) }

    fun invitationSent(token: String, storeId: Int){
        setLoading()
        api.invitation_sent(token, storeId).enqueue(object : Callback<WrappedListResponse<Invitation>>{
            override fun onFailure(call: Call<WrappedListResponse<Invitation>>, t: Throwable) {
                println(t.message)
                hideLoading()
                state.value = InvitationState.ShowToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedListResponse<Invitation>>, response: Response<WrappedListResponse<Invitation>>) {
                if (response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if (it.status){
                            invitationsSent.postValue(it.data)
                        }
                    }
                }else{
                    state.value = InvitationState.ShowAlert("Tidak dapat mengambil invitasi yang masuk")
                }
                hideLoading()
            }
        })
    }

    fun invite(token : String, storeId: Int, role: Boolean, to : Int){
        setLoading()
        val r = if(role) 1 else 0
        api.invite(token, storeId, r, to).enqueue(object : Callback<WrappedResponse<Invitation>>{
            override fun onFailure(call: Call<WrappedResponse<Invitation>>, t: Throwable) {
                println(t.message)
                hideLoading()
                state.value = InvitationState.ShowToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedResponse<Invitation>>, response: Response<WrappedResponse<Invitation>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if(it.status){
                            state.value = InvitationState.Success
                        }else{
                            state.value = InvitationState.ShowAlert(it.message)
                        }
                    }
                }else{
                    state.value = InvitationState.ShowAlert("Tidak dapat mengundang pengguna ini.")
                }
                hideLoading()
            }
        })
    }

    fun invitationIn(token: String){
        setLoading()
        api.invitation_in(token).enqueue(object : Callback<WrappedListResponse<Invitation>>{
            override fun onFailure(call: Call<WrappedListResponse<Invitation>>, t: Throwable) {
                println(t.message)
                hideLoading()
                state.value = InvitationState.ShowToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedListResponse<Invitation>>, response: Response<WrappedListResponse<Invitation>>) {
                if (response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if (it.status){
                            invitationsIn.postValue(it.data)
                        }
                    }
                }else{
                    state.value = InvitationState.ShowAlert("Tidak dapat mengambil invitasi yang masuk")
                }
                hideLoading()
            }
        })
    }

    fun listenToUIState() = state
    fun listenToInvitationSent() = invitationsSent
    fun listenToInvitationIn() = invitationsIn
}

sealed class InvitationState {
    data class IsLoading(var state : Boolean) : InvitationState()
    object Success : InvitationState()
    data class ShowToast(var message : String) : InvitationState()
    data class ShowAlert(var message : String) : InvitationState()
}