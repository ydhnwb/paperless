package com.ydhnwb.paperlessapp.viewmodels

import androidx.lifecycle.MutableLiveData
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
    private var currentUser = MutableLiveData<User>()

    fun validate(name : String?, email: String, password: String, confirmPassword : String?) : Boolean{
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
        if(confirmPassword != null){
            if (confirmPassword.isEmpty()){
                state.value = UserState.ShowToast("Isi semua form terlebih dahulu")
                return false
            }
            if(!confirmPassword.equals(password)){
                state.value = UserState.Validate(confirmPassword = "Konfirmasi password tidak cocok")
                return false
            }
        }

        return true
    }

    fun login(email: String, password: String) {
        state.value = UserState.IsLoading(true)
        api.login(email, password).enqueue(object : Callback<WrappedResponse<User>> {
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                state.value = UserState.ShowToast(t.message.toString())
                state.value = UserState.IsLoading(false)
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let{
                        if(it.status){
                            state.value = UserState.Success("Bearer ${it.data!!.api_token}")
                        }else{
                            state.value = UserState.ShowToast(it.message.toString())
                        }
                    }
                }else{
                    state.value = UserState.Popup("Tidak dapat masuk. Pastikan email anda terdaftar dan sudah terverifikasi")
                }
                state.value = UserState.IsLoading(false)
            }
        })
    }

    fun register(name: String, email: String, password: String) {
        state.value = UserState.IsLoading(true)
        api.register(name, email, password).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                state.value = UserState.ShowToast(t.message.toString())
                state.value = UserState.IsLoading(false)
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if(it.status){
                            state.value = UserState.Success(email)
                        }else{
                            state.value = UserState.ShowToast(it.message)
                        }
                    }
                }else{
                    state.value = UserState.Popup("Tidak dapat membuat akun. Mungkin email sudah pernah didaftarkan")
                }
                state.value = UserState.IsLoading(false)
            }
        })
    }

    fun profile(token: String){
        state.value = UserState.IsLoading(true)
        api.profile(token).enqueue(object: Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                state.value = UserState.ShowToast(t.message.toString())
                state.value = UserState.IsLoading(false)
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if(it.status){
                            currentUser.postValue(it.data)
                        }else{
                            state.value = UserState.ShowToast("Tidak dapat memuat info")
                        }
                    }
                }else{
                    state.value = UserState.ShowToast("Kesalahan saat mengambil info user")
                }
                state.value = UserState.IsLoading(false)
            }
        })
    }

    fun getUIState() = state
    fun listenToCurrentUser() = currentUser
}

sealed class UserState{
    data class ShowToast(var message : String) : UserState()
    data class Validate(var name : String? = null, var email : String? = null, var password : String? = null, var confirmPassword : String? = null) : UserState()
    data class IsLoading(var state :Boolean = false) : UserState()
    data class Success(var token :String) : UserState()
    data class Popup(var message : String) : UserState()
    object Reset : UserState()
}