package com.ydhnwb.paperlessapp.ui.invitation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Invitation
import com.ydhnwb.paperlessapp.repositories.InvitationRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class InvitationViewModel (private val invitationRepository: InvitationRepository) : ViewModel(){
    private val state : SingleLiveEvent<InvitationState> = SingleLiveEvent()
    private val invitationsIn = MutableLiveData<List<Invitation>>()
    private val invitationsSent = MutableLiveData<List<Invitation>>()

    fun setLoading(){ state.value = InvitationState.IsLoading(true) }
    fun hideLoading(){ state.value =InvitationState.IsLoading(false) }
    fun toast(m: String){ state.value = InvitationState.ShowToast(m) }
    fun alert(m: String){ state.value = InvitationState.ShowAlert(m) }
    fun success() { state.value = InvitationState.Success }


    fun fetchInvitationSent(token: String, storeId: Int){
        setLoading()
        invitationRepository.invitationSent(token, storeId, object: ArrayResponse<Invitation>{
            override fun onSuccess(datas: List<Invitation>?) {
                hideLoading()
                datas?.let { invitationsSent.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun fetchInvitationIn(token: String){
        setLoading()
        invitationRepository.invitationIn(token, object: ArrayResponse<Invitation>{
            override fun onSuccess(datas: List<Invitation>?) {
                hideLoading()
                datas?.let { invitationsIn.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun acceptInvitation(token: String, invitationId: String){
        setLoading()
        invitationRepository.acceptInvitation(token, invitationId, object: SingleResponse<Invitation>{
            override fun onSuccess(data: Invitation?) {
                hideLoading()
                data?.let { success() }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { alert(it) }
            }
        })
    }

    fun rejectInvitation(token: String, invitationId: String){
        setLoading()
        invitationRepository.rejectInvitation(token, invitationId, object: SingleResponse<Invitation>{
            override fun onSuccess(data: Invitation?) {
                hideLoading()
                data?.let { success() }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { alert(it) }
            }
        })
    }

    fun listenToUIState() = state
    fun listenToInvitationIn() = invitationsIn
    fun listenToInvitationSent() = invitationsSent
}

sealed class InvitationState {
    data class IsLoading(var state : Boolean) : InvitationState()
    object Success : InvitationState()
    data class ShowToast(var message : String) : InvitationState()
    data class ShowAlert(var message : String) : InvitationState()
}