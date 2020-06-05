package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryRepository (private val api: ApiService) {

    fun getCategories(completion: (List<Category>?, Error?) -> Unit){
        api.category_get().enqueue(object: Callback<WrappedListResponse<Category>> {
            override fun onFailure(call: Call<WrappedListResponse<Category>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message))
            }

            override fun onResponse(call: Call<WrappedListResponse<Category>>, response: Response<WrappedListResponse<Category>>) {
                if(response.isSuccessful){
                    val b = response.body() as WrappedListResponse<Category>
                    if (b.status){
                        val cats = b.data
                        completion(cats, null)
                    }else{
                        completion(null, Error("Cannot get categories"))
                    }
                }else{
                    completion(null, Error("Error ${response.code()} with status code ${response.code()}"))
                }
            }
        })
    }

}