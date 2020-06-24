package com.ydhnwb.paperlessapp.fragments.dialog

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.repositories.InvitationRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class InvitationDialogViewModel (private val invitationRepository: InvitationRepository) : ViewModel(){
    private val state : SingleLiveEvent<InvitationDialogState> = SingleLiveEvent()

    private fun setLoading(){ state.value =  InvitationDialogState.IsLoading(true) }
    private fun hideLoading(){ state.value =  InvitationDialogState.IsLoading(false) }
    private fun alert(message: String){ state.value =  InvitationDialogState.ShowAlert(message) }
    private fun success() { state.value = InvitationDialogState.Success }

    fun invite(token: String, storeId: Int, role: Boolean, to: Int){
        setLoading()
        invitationRepository.invite(token, storeId, role, to){ bool, e ->
            hideLoading()
            e?.let { it.message?.let { m -> alert(m) } }
            if(bool){ success() }
        }
    }
    fun listenToUIState() = state
}

sealed class InvitationDialogState {
    object Success: InvitationDialogState()
    data class IsLoading(val state : Boolean) : InvitationDialogState()
    data class ShowAlert(val message : String) : InvitationDialogState()
}