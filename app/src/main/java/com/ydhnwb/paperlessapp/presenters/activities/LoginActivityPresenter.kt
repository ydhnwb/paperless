package com.ydhnwb.paperlessapp.presenters.activities

import com.ydhnwb.paperlessapp.contracts.activities.LoginActivityContract
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivityPresenter(var view : LoginActivityContract.View?) : LoginActivityContract.Interactor {
    private var api = ApiClient.instance()

    override fun validate(email: String, password: String) : Boolean{
        view?.emailError(null)
        view?.passwordError(null)

        if(email.isEmpty() || password.isEmpty()) {
            view?.toast("Email dan password tak boleh kosong")
            return false
        }

        if (!PaperlessUtil.isValidEmail(email)){
            view?.emailError("Email tidak valid")
            return false
        }
        if (!PaperlessUtil.isValidPassword(password)){
            view?.passwordError("Password harus lebih dari delapan karakter")
            return false
        }
        return true
    }

    override fun doLogin(email: String, password: String) {
        view?.showLoading()
        api.login(email, password).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                view?.hideLoading()
                view?.toast("Terjadi kesalahan ${t.message}")
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let{
                        if(it.status!!){
                            view?.success(email)
                        }else{
                            view?.failed(it.message.toString())
                        }
                    }
                }else{
                    view?.failed("Tidak dapat masuk. Pastikan email terverifikasi dan password benar")
                }
                view?.hideLoading()
            }
        })
    }

    override fun destroy() { view = null }
}