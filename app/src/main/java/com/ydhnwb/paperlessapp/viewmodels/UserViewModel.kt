package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel(){
    private var state : SingleLiveEvent<UserState> = SingleLiveEvent()
    private var api = ApiClient.instance()


    fun validate(name : String?, email: String, password: String) : Boolean{
        state.value = UserState.Reset
        if(name != null){
            if(name.isEmpty()){
                state.value = UserState.ShowToast("Isi semua form terlebih dahulu")
                return false
            }
            if(name.length < 5){
                state.value = UserState.Validate(name = "Nama setidaknya lima karakter")
                return false
            }
        }

        if(email.isEmpty() || password.isEmpty()) {
            state.value = UserState.ShowToast("Isi semua form terlebih dahulu")
            return false
        }

        if (!PaperlessUtil.isValidEmail(email)){
            state.value = UserState.Validate(email = "Email tidak valid")
            return false
        }
        if (!PaperlessUtil.isValidPassword(password)){
            state.value = UserState.Validate(password = "Password tidak valid")
            return false
        }
        return true
    }


    fun login(email: String, password: String) {
        state.value = UserState.IsLoading(true)
        api.login(email, password).enqueue(object : Callback<WrappedResponse<User>> {
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                state.value = UserState.Error(t.message)
                state.value = UserState.IsLoading(false)
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let{
                        if(it.status!!){
                            state.value = UserState.Success("Bearer ${it.data!!.api_token}")
                        }else{
                            state.value = UserState.Failed(it.message.toString())
                        }
                    }
                }else{
                    state.value = UserState.Popup("Tidak dapat masuk. Pastikan email anda terdaftar dan sudah terverifikasi")
                }
                state.value = UserState.IsLoading(false)
            }
        })
    }

    fun getUIState() = state
}

sealed class UserState{
    data class Error(var err : String?) : UserState()
    data class ShowToast(var message : String) : UserState()
    data class Validate(var name : String? = null, var email : String? = null, var password : String? = null) : UserState()
    data class IsLoading(var state :Boolean = false) : UserState()
    data class Success(var token :String) : UserState()
    data class Failed(var message :String) : UserState()
    data class Popup(var message : String) : UserState()
    object Reset : UserState()
}