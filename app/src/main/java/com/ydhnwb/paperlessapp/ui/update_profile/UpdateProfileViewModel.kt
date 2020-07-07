package com.ydhnwb.paperlessapp.ui.update_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.repositories.UserRepository
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.SingleResponse

class UpdateProfileViewModel (private val userRepository: UserRepository) : ViewModel(){
    private val state: SingleLiveEvent<UpdateProfileState> = SingleLiveEvent()
    private val user = MutableLiveData<User>()
    private val currentImagePath = MutableLiveData<String>()

    private fun setLoading(){ state.value = UpdateProfileState.Loading(true) }
    private fun hideLoading(){ state.value = UpdateProfileState.Loading(false) }
    private fun toast(message: String){ state.value = UpdateProfileState.ShowToast(message) }
    private fun success(){ state.value = UpdateProfileState.Success }

    fun updateProfilePicture(token: String){
        currentImagePath.value?.let {
            setLoading()
            userRepository.updateProfilePic(token, it, object : SingleResponse<User>{
                override fun onSuccess(data: User?) {
                    hideLoading()
                    data?.let { success() }
                }
                override fun onFailure(err: Error) {
                    hideLoading()
                    err.message?.let { toast(it) }
                }
            })
        }
    }


    fun updateProfile(token: String, u: User){
        user.value?.let {
            it.name = u.name
            it.phone = u.phone
            setLoading()
            userRepository.updateProfile(token, it, object : SingleResponse<User> {
                override fun onSuccess(data: User?) {
                    hideLoading()
                    data?.let {
                        updateProfilePicture(token)
                        success()
                    }
                }
                override fun onFailure(err: Error) {
                    hideLoading()
                    err.message?.let { toast(it) }
                }
            })
        }
    }


    fun fetchProfile(token: String){
        if(user.value == null){
            setLoading()
            userRepository.getCurrentProfile(token, object : SingleResponse<User>{
                override fun onSuccess(data: User?) {
                    hideLoading()
                    data?.let { user.postValue(it) }
                }
                override fun onFailure(err: Error) {
                    hideLoading()
                    err.message?.let { toast(it) }
                }
            })
        }
    }

    fun setImagePath(imagePath : String){
        currentImagePath.value = imagePath
    }

    fun getState() = state
    fun getUser() = user
    fun getImagePath() = currentImagePath
}

sealed class UpdateProfileState {
    object Success : UpdateProfileState()
    data class Loading(val isLoading: Boolean) : UpdateProfileState()
    data class ShowToast(val message : String) : UpdateProfileState()
}