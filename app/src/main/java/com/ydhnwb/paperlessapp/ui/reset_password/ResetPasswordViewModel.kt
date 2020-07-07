package com.ydhnwb.paperlessapp.ui.reset_password

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.repositories.UserRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class ResetPasswordViewModel(private val userRepository: UserRepository) : ViewModel(){
    private val state: SingleLiveEvent<ResetPasswordState> = SingleLiveEvent()

    private fun setLoading(){
        state.value = ResetPasswordState.IsLoading(true)
    }
    private fun hideLoading(){
        state.value = ResetPasswordState.IsLoading(false)
    }
    private fun success(e: String){
        state.value = ResetPasswordState.Success(e)
    }

    private fun alert(message: String){
        state.value = ResetPasswordState.Alert(message)
    }

    fun resetPassword(email: String){
        setLoading()
        userRepository.resetPassword(email, object : SingleResponse<String>{
            override fun onSuccess(data: String?) {
                hideLoading()
                data?.let { success(email) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { alert(it) }
            }
        })
    }

    fun getState() = state
}

sealed class ResetPasswordState {
    data class IsLoading(val state : Boolean) : ResetPasswordState()
    data class Success(val email: String) : ResetPasswordState()
    data class Alert(val message: String) : ResetPasswordState()
}