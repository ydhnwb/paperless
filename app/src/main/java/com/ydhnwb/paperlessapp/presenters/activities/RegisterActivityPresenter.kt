package com.ydhnwb.paperlessapp.presenters.activities

import com.ydhnwb.paperlessapp.contracts.activities.RegisterActivityContract
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivityPresenter(private var view : RegisterActivityContract.View?) : RegisterActivityContract.Interactor {
    private var api = ApiClient.instance()

    override fun validate(name: String, email: String, password: String, conf_password: String) : Boolean {
        view?.let {
            it.errorEmail(null)
            it.errorName(null)
            it.errorPassword(null)
            it.errorPasswordConfirm(null)
        }

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || conf_password.isEmpty()){
            view?.toast("Isi semua form terlebih dahulu")
            return false
        }

        if(name.length < 5){
            view?.errorName("Nama setidaknya lima karakter")
            return false
        }

        if(!PaperlessUtil.isValidEmail(email)){
            view?.errorEmail("Email tidak valid")
            return false
        }

        if(!password.equals(conf_password)){
            view?.errorPassword("Password tidak sama")
            view?.errorPasswordConfirm("Password tidak sama")
            return false
        }

        if(!PaperlessUtil.isValidPassword(password)){
            view?.errorPassword("Password tidak valid")
            return false
        }

        if(!PaperlessUtil.isValidPassword(conf_password)){
            view?.errorPasswordConfirm("Password tidak valid")
            return false
        }
        return true
    }

    override fun destroy() { view = null }

    override fun register(name: String, email: String, password: String) {
        view?.isLoading(true)
        api.register(name, email, password).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                view?.toast("Kesalahan. Coba lagi nanti ${t.message}")
                view?.isLoading(false)
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if(it.status!!){
                            view?.success(email)
                        }else{
                            view?.failed()
                        }
                    }
                }else{
                    view?.toast("Tidak dapat membuat akun")
                }
                view?.isLoading(false)
            }
        })
    }
}