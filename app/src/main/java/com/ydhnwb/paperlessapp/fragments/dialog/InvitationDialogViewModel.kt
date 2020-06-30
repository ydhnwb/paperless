package com.ydhnwb.paperlessapp.fragments.dialog

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Invitation
import com.ydhnwb.paperlessapp.repositories.InvitationRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class InvitationDialogViewModel (private val invitationRepository: InvitationRepository) : ViewModel(){
    private val state : SingleLiveEvent<InvitationDialogState> = SingleLiveEvent()

    private fun setLoading(){ state.value =  InvitationDialogState.IsLoading(true) }
    private fun hideLoading(){ state.value =  InvitationDialogState.IsLoading(false) }
    private fun alert(message: String){ state.value =  InvitationDialogState.ShowAlert(message) }
    private fun success() { state.value = InvitationDialogState.Success }

    fun invite(token: String, storeId: Int, role: Boolean, to: Int){
        setLoading()
        invitationRepository.invite(token, storeId, role, to, object : SingleResponse<Invitation>{
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
}

sealed class InvitationDialogState {
    object Success: InvitationDialogState()
    data class IsLoading(val state : Boolean) : InvitationDialogState()
    data class ShowAlert(val message : String) : InvitationDialogState()
}