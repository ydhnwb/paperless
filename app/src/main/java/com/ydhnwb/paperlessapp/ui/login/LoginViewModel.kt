package com.ydhnwb.paperlessapp.ui.login

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.repositories.UserRepository
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class LoginViewModel (private val userRepository: UserRepository) : ViewModel() {
    private val state : SingleLiveEvent<LoginState> = SingleLiveEvent()

    private fun setLoading(){ state.value = LoginState.IsLoading(true) }
    private fun hideLoading(){ state.value = LoginState.IsLoading(false) }
    private fun toast(message: String) { state.value = LoginState.ShowToast(message) }
    private fun resetState() { state.value = LoginState.Reset }
    private fun success(param: String){ state.value = LoginState.Success(param) }

    fun validate(email: String, password: String) : Boolean {
        resetState()
        if (email.isEmpty() || !PaperlessUtil.isValidEmail(email)){
            state.value = LoginState.Validate(email = "Email tidak valid")
            return false
        }
        if (password.isEmpty() || !PaperlessUtil.isValidPassword(password)){
            state.value = LoginState.Validate(password = "Password tidak valid")
            return false
        }
        return true
    }

    fun login(email: String, password: String){
        setLoading()
        userRepository.login(email, password){ resultString, error ->
            hideLoading()
            error?.let { e -> toast(e.message.toString()) }
            resultString?.let { token -> success(token) }
        }
    }

    fun listenToUIState() = state
}

sealed class LoginState {
    data class ShowToast(var message : String) : LoginState()
    data class Validate(var email : String? = null, var password : String? = null) : LoginState()
    data class IsLoading(var state :Boolean = false) : LoginState()
    data class Success(var token: String) : LoginState()
    object Reset : LoginState()
}