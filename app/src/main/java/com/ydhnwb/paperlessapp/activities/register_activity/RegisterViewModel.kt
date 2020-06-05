package com.ydhnwb.paperlessapp.activities.register_activity

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.repositories.UserRepository
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class RegisterViewModel (private val userRepository: UserRepository) : ViewModel(){
    private val state: SingleLiveEvent<RegisterState> = SingleLiveEvent()

    private fun setLoading(){ state.value = RegisterState.IsLoading(true) }
    private fun hideLoading(){ state.value = RegisterState.IsLoading(false) }
    private fun toast(message: String) { state.value = RegisterState.ShowToast(message) }
    private fun success(param: String?) { state.value = RegisterState.Success(param) }
    private fun failed(optionalMessage: String?) { state.value = RegisterState.Failed(optionalMessage) }
    private fun resetState() { state.value = RegisterState.Reset }

    fun validate(name : String, email: String, password: String, confirmPassword : String) : Boolean{
        resetState()
        if(name.isEmpty() || name.length < 5){
            state.value = RegisterState.Validate(name = "Nama setidaknya lima karakter")
            return false
        }
        if (email.isEmpty() || !PaperlessUtil.isValidEmail(email)){
            state.value = RegisterState.Validate(email = "Email tidak valid")
            return false
        }
        if (password.isEmpty() || !PaperlessUtil.isValidPassword(password)){
            state.value = RegisterState.Validate(password = "Password tidak valid")
            return false
        }
        if (confirmPassword.isEmpty()){
            state.value = RegisterState.ShowToast("Isi semua form terlebih dahulu")
            return false
        }
        if(!confirmPassword.equals(password)){
            state.value = RegisterState.Validate(confirmPassword = "Konfirmasi password tidak cocok")
            return false
        }
        return true
    }

    fun register(name: String, email: String, password: String){
        setLoading()
        userRepository.register(name, email, password){ b, error ->
            hideLoading()
            error?.let { failed(it.message) }
            if(b){
                success(email)
            }
        }
    }

    fun listenToUIState() = state
}

sealed class RegisterState {
    data class Success(var message: String?) : RegisterState()
    data class Failed(var message: String? = null) : RegisterState()
    data class ShowToast(var message : String) : RegisterState()
    data class Validate(var name: String? = null, var email : String? = null, var password : String? = null, var confirmPassword: String? = null) : RegisterState()
    data class IsLoading(var state :Boolean = false) : RegisterState()
    object Reset : RegisterState()
}