package com.ydhnwb.paperlessapp.ui.invitation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Invitation
import com.ydhnwb.paperlessapp.repositories.InvitationRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

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
        invitationRepository.invitationSent(token, storeId){ resultInvitations, e ->
            hideLoading()
            e?.let { it.message?.let { m -> toast(m) } }
            resultInvitations?.let { invitationsSent.postValue(it) }
        }
    }




    fun fetchInvitationIn(token: String){
        setLoading()
        invitationRepository.invitationIn(token){ resultInvitations, e ->
            hideLoading()
            e?.let { it.message?.let { m -> toast(m) } }
            resultInvitations?.let {
                invitationsIn.postValue(it)
            }
        }
    }

    fun acceptInvitation(token: String, invitationId: String){
        setLoading()
        invitationRepository.acceptInvitation(token, invitationId){ b, e ->
            hideLoading()
            e?.let { it.message?.let { m -> alert(m) } }
            if(b){ success() }
        }
    }

    fun rejectInvitation(token: String, invitationId: String){
        setLoading()
        invitationRepository.rejectInvitation(token, invitationId){ b, e ->
            hideLoading()
            e?.let { it.message?.let { m -> alert(m) } }
            if(b){ success() }
        }
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