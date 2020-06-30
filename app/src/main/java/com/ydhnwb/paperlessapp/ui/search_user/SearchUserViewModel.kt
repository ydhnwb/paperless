package com.ydhnwb.paperlessapp.ui.search_user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.repositories.UserRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class SearchUserViewModel (private val userRepository: UserRepository) : ViewModel(){
    private val state: SingleLiveEvent<SearchUserState> = SingleLiveEvent()
    private val users = MutableLiveData<List<User>>()

    private fun setLoading(){ state.value = SearchUserState.IsLoading(true) }
    private fun hideLoading(){ state.value = SearchUserState.IsLoading(false) }
    private fun toast(message: String){ state.value = SearchUserState.ShowToast(message) }

    fun fetchSearchUser(token: String, query: String){
        setLoading()
        userRepository.search(token, query, object: ArrayResponse<User>{
            override fun onSuccess(datas: List<User>?) {
                hideLoading()
                datas?.let { users.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun listenToUIState() = state
    fun listenToUsers() = users
}

sealed class SearchUserState{
    data class IsLoading(var state : Boolean) : SearchUserState()
    data class ShowToast(var message: String) : SearchUserState()
}