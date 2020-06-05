package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.Invitation
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvitationRepository (private val api: ApiService){

    fun invitationSent(token: String, storeId: Int, completion: (List<Invitation>?, Error?) -> Unit){
        api.invitation_sent(token, storeId).enqueue(object : Callback<WrappedListResponse<Invitation>>{
            override fun onFailure(call: Call<WrappedListResponse<Invitation>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedListResponse<Invitation>>, response: Response<WrappedListResponse<Invitation>>) {
                if (response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if (it.status){
                            completion(it.data, null)
                        }else{
                            completion(null, Error(b.message))
                        }
                    }
                }else{
                    completion(null, Error("Error ${response.errorBody()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun invite(token : String, storeId: Int, role: Boolean, to : Int, completion: (Boolean, Error?) -> Unit){
        val r = if(role) 1 else 0
        api.invite(token, storeId, r, to).enqueue(object : Callback<WrappedResponse<Invitation>>{
            override fun onFailure(call: Call<WrappedResponse<Invitation>>, t: Throwable) {
                println(t.message)
                completion(false, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Invitation>>, response: Response<WrappedResponse<Invitation>>) {
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
                    completion(false, Error("Error ${response.errorBody()} with status code ${response.code()}"))
                }
            }
        })
    }



    fun invitationIn(token: String, completion: (List<Invitation>?, Error?) -> Unit){
        api.invitation_in(token).enqueue(object : Callback<WrappedListResponse<Invitation>> {
            override fun onFailure(call: Call<WrappedListResponse<Invitation>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedListResponse<Invitation>>, response: Response<WrappedListResponse<Invitation>>) {
                if (response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if (it.status){
                            completion(it.data, null)
                        }
                        completion(it.data, Error("Cannot get data"))
                    }
                }else{
                    completion(null, Error("Error ${response.errorBody()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun acceptInvitation(token: String, invitationId : String, completion: (Boolean, Error?) -> Unit){
        api.invitation_acc(token, invitationId).enqueue(object :
            Callback<WrappedResponse<Invitation>> {
            override fun onFailure(call: Call<WrappedResponse<Invitation>>, t: Throwable) {
                println(t.message)
                completion(false, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Invitation>>, response: Response<WrappedResponse<Invitation>>) {
                if(response.isSuccessful){
                    if(response.body()!!.status){
                        completion(true, null)
                    }else{
                        completion(false, Error("Cannot accept invitation. Try again later"))
                    }
                }else{
                    completion(false, Error("Error ${response.errorBody()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun rejectInvitation(token: String, invitationId : String, completion: (Boolean, Error?) -> Unit){
        api.invitation_reject(token, invitationId).enqueue(object :
            Callback<WrappedResponse<Invitation>> {
            override fun onFailure(call: Call<WrappedResponse<Invitation>>, t: Throwable) {
                println(t.message)
                completion(false, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Invitation>>, response: Response<WrappedResponse<Invitation>>) {
                if(response.isSuccessful){
                    if(response.body()!!.status){
                        completion(true, null)
                    }else{
                        completion(false, Error("Cannot accept invitation. Try again later"))
                    }
                }else{
                    completion(false, Error("Error ${response.errorBody()} with status code ${response.code()}"))
                }
            }
        })
    }

}