package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProductRepository (private val api: ApiService){

    fun createProduct(token: String, storeId : String, product: Product, categoryId : Int, completion: (Boolean, Error?) -> Unit){
        val file = File(product.image.toString())
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, requestBodyForFile)
        api.product_store(token, storeId, product.name.toString(), product.description.toString() ,product.code, product.price!!, categoryId, product.availableOnline, product.weight, true, product.qty, image).enqueue(object : Callback<WrappedResponse<Product>>{
                override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) {
                    println(t.message)
                    completion(false, Error(t.message))
                }

                override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                    if(response.isSuccessful){
                        val body = response.body()
                        body?.let {
                            if(it.status){
                                completion(true, null)
                            }else{
                                completion(false, Error("Cannot create product. Try again later"))
                            }
                        }
                    }else{
                        completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                    }
                }
            })
    }


    fun fetchAllProducts(token: String, storeId: String, completion: (List<Product>?, Error?) -> Unit){
        api.product_get(token, storeId).enqueue(object : Callback<WrappedListResponse<Product>> {
            override fun onFailure(call: Call<WrappedListResponse<Product>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedListResponse<Product>>, response: Response<WrappedListResponse<Product>>) {
                if (response.isSuccessful){
                    val body = response.body()
                    body?.let {
                        if (it.status){
                            completion(it.data, null)
//                            hasFetched.value = true
                        }else{
                            completion(null, Error())
                        }
                    }
                }else{
                    completion(null, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun updateProductWithImage(token: String, storeId: String, product : Product, categoryId: Int, completion: (Boolean, Error?) -> Unit){
        val file = File(product.image.toString())
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, requestBodyForFile)
        api.product_update(token, storeId, product.id.toString(), product.name.toString(), product.description.toString(),
            product.code, product.price!!, categoryId, product.availableOnline, product.weight, true, product.qty, image)
            .enqueue(object : Callback<WrappedResponse<Product>>{
                override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) {
                    println(t.message)
                    completion(false, Error(t.message.toString()))
                }

                override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                    if(response.isSuccessful){
                        val b = response.body()
                        b?.let {
                            if(it.status){
                                completion(true, null)
                            }else{
                                completion(false, Error("Cannot update product. Try again later"))
                            }
                        }
                    }else{
                        completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                    }
                }
            })
    }

    fun updateProductOnly(token: String, storeId: String, product : Product, categoryId: Int, completion: (Boolean, Error?) -> Unit){
        api.product_update(token, storeId, product.id.toString(), product.name.toString(), product.description.toString(), product.code, product.price!!, categoryId, product.availableOnline, product.weight, true, product.qty).enqueue(object : Callback<WrappedResponse<Product>>{
            override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) {
                println(t.message)
                completion(false, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                println(response)
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if(it.status){
                            completion(true, null)
                        } else {
                            completion(false, Error("Cannot update product. Try again later"))
                        }
                    }
                    }else{
                        completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                    }
            }
        })
    }

    fun deleteProduct(token: String, storeId: String, productId : String, completion: (Boolean, Error?) -> Unit){
        api.product_delete(token, storeId, productId).enqueue(object : Callback<WrappedResponse<Product>>{
            override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) {
                println(t.message)
                completion(false, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    b?.let {
                        if (it.status){
                            completion(true, null)
                        }else{
                            completion(false, Error("Cannot delete product."))
                        }
                    }
                }else{
                    completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }

    fun searchProductCatalog(token: String, q: String, completion: (List<Product>?, Error?) -> Unit){
        api.catalog_search(token, q).enqueue(object : Callback<WrappedListResponse<Product>>{
            override fun onFailure(call: Call<WrappedListResponse<Product>>, t: Throwable) {
                println(t.message)
                completion(null, Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedListResponse<Product>>, response: Response<WrappedListResponse<Product>>) {
                if(response.isSuccessful){
                    val body = response.body()
                    body?.let {
                        if(it.status){
                            completion(it.data, null)
                        }else{
                            completion(null, Error())
                        }
                    }
                }else{
                    completion(null, Error("Error ${response.message()} with status code ${response.code()}"))
                }
            }
        })
    }


}