package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Invitation
import com.ydhnwb.paperlessapp.models.InvitationAlt
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
    private var hasFetched = MutableLiveData<Boolean>()

    fun setLoading(){ state.value = InvitationState.IsLoading(true) }
    fun hideLoading(){ state.value = InvitationState.IsLoading(false) }

    fun invitationSent(token: String, storeId: Int){
        hasFetched.value = true
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

    fun acceptInvitation(token: String, invitationId : String){
        setLoading()
        api.invitation_acc(token, invitationId).enqueue(object : Callback<WrappedResponse<InvitationAlt>>{
            override fun onFailure(call: Call<WrappedResponse<InvitationAlt>>, t: Throwable) {
                println(t.message)
                state.value = InvitationState.ShowToast(t.message.toString())
                hideLoading()
            }

            override fun onResponse(call: Call<WrappedResponse<InvitationAlt>>, response: Response<WrappedResponse<InvitationAlt>>) {
                if(response.isSuccessful){
                    if(response.body()!!.status){
                        state.value = InvitationState.Success
                    }else{
                        state.value = InvitationState.ShowAlert("Tidak dapat menerima undangan")
                    }
                }else{
                    state.value = InvitationState.ShowAlert("Tidak dapat menerima invitasi")
                }
                hideLoading()
            }
        })
    }

    fun rejectInvitation(token: String, invitationId : String){
        setLoading()
        api.invitation_reject(token, invitationId).enqueue(object : Callback<WrappedResponse<InvitationAlt>>{
            override fun onFailure(call: Call<WrappedResponse<InvitationAlt>>, t: Throwable) {
                println(t.message)
                state.value = InvitationState.ShowToast(t.message.toString())
                hideLoading()
            }

            override fun onResponse(call: Call<WrappedResponse<InvitationAlt>>, response: Response<WrappedResponse<InvitationAlt>>) {
                if(response.isSuccessful){
                    if(response.body()!!.status){
                        state.value = InvitationState.Success
                    }else{
                        state.value = InvitationState.ShowAlert("Gagal menolak undangan")
                    }
                }else{
                    println(response.body())
                    println(response.errorBody())
                    println(response.message())
                    println(response.headers())
                    state.value = InvitationState.ShowToast(response.code().toString())
                    state.value = InvitationState.ShowToast(response.body().toString())
                    state.value = InvitationState.ShowAlert("Gagal menolak invitasi")
                }
                hideLoading()
            }
        })
    }


    fun listenToHasFetched() = hasFetched
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