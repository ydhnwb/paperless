package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository (private val api: ApiService) {

    fun login(email: String, password: String, completion: (String?, Error?) -> Unit) {
        api.login(email, password).enqueue(object : Callback<WrappedResponse<User>> {
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message))
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let{
                        if(it.status){
                            completion("Bearer ${it.data!!.api_token}", null)
                        }else{
                            completion(null, Error("${it.message} with status code ${response.code()}"))
                        }
                    }
                }else{
                    completion(null, Error("Tidak dapat masuk. Periksa email dan kata sandi anda"))
                }
            }
        })
    }

    fun register(name: String, email: String, password: String, completion: (Boolean, Error?) -> Unit){
        api.register(name, email, password).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                completion(false, Error(t.message))
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if(it.status){
                            completion(true, null)
                        }else{
                            completion(false, Error(it.message))
                        }
                    }
                }else{
                    completion(false, Error("${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun getCurrentProfile(token: String, completion: (User?, Error?) -> Unit){
        api.profile(token).enqueue(object: Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message))
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if(it.status){
                            completion(it.data, null)
                        }else{
                            completion(null, Error("Cannot get user info. Please check your internet connection"))
                        }
                    }
                }else{
                    completion(null, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun search(token: String, query: String, completion: (List<User>?, Error?) -> Unit){
         api.user_search(token,query).enqueue(object: Callback<WrappedListResponse<User>>{
            override fun onFailure(call: Call<WrappedListResponse<User>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message))
            }

            override fun onResponse(call: Call<WrappedListResponse<User>>, response: Response<WrappedListResponse<User>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if(it.status){
                            val res = mutableListOf<User>()
                            if (it.data!!.size > 20){
                                res.addAll(it.data!!.take(20))
                            }else{
                                res.addAll(it.data!!)
                            }
                            completion(res, null)
                        }else{
                            completion(null, Error(b.message))
                        }
                    }
                }else{
                    completion(null, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }
}