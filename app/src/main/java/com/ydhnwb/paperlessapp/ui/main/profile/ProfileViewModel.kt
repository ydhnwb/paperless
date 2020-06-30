package com.ydhnwb.paperlessapp.ui.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.repositories.UserRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class ProfileViewModel (private val userRepository: UserRepository) : ViewModel(){
    private val state: SingleLiveEvent<ProfileState> = SingleLiveEvent()
    private val currentUser = MutableLiveData<User>()

    private fun setLoading(){ state.value = ProfileState.IsLoading(true) }
    private fun hideLoading() { state.value = ProfileState.IsLoading(false) }
    private fun toast(message: String) { state.value = ProfileState.ShowToast(message) }

    fun fetchProfile(token: String){
        setLoading()
        userRepository.getCurrentProfile(token, object: SingleResponse<User>{
            override fun onSuccess(data: User?) {
                hideLoading()
                data?.let { currentUser.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun listenToUIState() = state
    fun listenToCurrentUser() = currentUser
}

sealed class ProfileState {
    data class IsLoading(var state : Boolean) : ProfileState()
    data class ShowToast(var message : String) : ProfileState()
}