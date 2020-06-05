package com.ydhnwb.paperlessapp.fragments.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.repositories.UserRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class ProfileViewModel (private val userRepository: UserRepository) : ViewModel(){
    private val state: SingleLiveEvent<ProfileState> = SingleLiveEvent()
    private val currentUser = MutableLiveData<User>()

    private fun setLoading(){ state.value = ProfileState.IsLoading(true) }
    private fun hideLoading() { state.value = ProfileState.IsLoading(false) }
    private fun toast(message: String) { state.value = ProfileState.ShowToast(message) }

    fun fetchProfile(token: String){
        setLoading()
        userRepository.getCurrentProfile(token){ resultUser, error ->
            hideLoading()
            error?.let { it.message?.let { message -> toast(message) } }
            resultUser?.let {
                currentUser.postValue(it)
            }
        }
    }

    fun listenToUIState() = state
    fun listenToCurrentUser() = currentUser
}

sealed class ProfileState {
    data class IsLoading(var state : Boolean) : ProfileState()
    data class ShowToast(var message : String) : ProfileState()
}