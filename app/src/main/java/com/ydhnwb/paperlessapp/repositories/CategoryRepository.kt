package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


interface CategoryContract {
    fun getCategories(listener: ArrayResponse<Category>)
}

class CategoryRepository (private val api: ApiService) : CategoryContract {
    override fun getCategories(listener: ArrayResponse<Category>) {
        api.category_get().enqueue(object: Callback<WrappedListResponse<Category>> {
            override fun onFailure(call: Call<WrappedListResponse<Category>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedListResponse<Category>>, response: Response<WrappedListResponse<Category>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body() as WrappedListResponse<Category>
                        if (b.status){
                            listener.onSuccess(b.data)
                        }else{
                            listener.onFailure(Error(b.message))
                        }
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }
}