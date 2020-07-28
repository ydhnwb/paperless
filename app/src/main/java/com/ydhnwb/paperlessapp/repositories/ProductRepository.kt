package com.ydhnwb.paperlessapp.repositories

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ydhnwb.paperlessapp.models.GeneralProductSearch
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.*
import com.ydhnwb.paperlessapp.webservices.ApiService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

interface ProductContract {
    fun updateImageProductOnly(token: String, storeId: String, product: Product, listener: SingleResponse<Product>)
    fun getPromotedProducts(token: String, listener: ArrayResponse<Product>)
    fun searchProductCatalog(token: String, q: String, listener: SingleResponse<GeneralProductSearch>)
    fun deleteProduct(token: String, storeId: String, productId : String, listener: SingleResponse<Product>)
    fun updateProductOnly(token: String, storeId: String, product : Product,categoryId: Int, listener: SingleResponse<Product>)
    fun updateProductWithImage(token: String, storeId: String, product : Product, categoryId: Int, listener: SingleResponse<Product>)
    fun fetchAllProducts(token: String, storeId: String, listener: ArrayResponse<Product>)
    fun createProduct(token: String, storeId : String, product: Product, categoryId : Int, listener: SingleResponse<Product>)
}

class ProductRepository (private val api: ApiService) : ProductContract {

    /*fun applyPromo(token: String, productId: String, discountValueByPercent : Float, completion: (Boolean, Error?) -> Unit){
        api.promo_apply(token, productId, discountValueByPercent).enqueue(object : Callback<WrappedResponse<Product>>{
            override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) {
                println(t.message)
                completion(false, java.lang.Error(t.message.toString()))
            }

            override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                when{
                    response.isSuccessful -> {
                        val body = response.body()
                        if(body!!.status){
                            completion(true, null)
                        }else{
                            completion(false, Error(body.message))
                        }
                    }
                    !response.isSuccessful -> {
                        println(response.message())
                        completion(false, Error("Error ${response.message()} with status code ${response.code()}"))
                    }
                }
            }
        })
    }*/

    override fun updateImageProductOnly(token: String, storeId: String, product: Product, listener: SingleResponse<Product>) {
        val file = File(product.image.toString())
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, requestBodyForFile)
        api.product_update_image_only(token, storeId, product.id.toString(), image).enqueue(object : Callback<WrappedResponse<Product>>{
            override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) {
                println(t.message)
                listener.onFailure(Error(t.message))
            }
            override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                when{
                    response.isSuccessful -> {
                        val res = response.body()!!
                        if(res.status) listener.onSuccess(res.data) else listener.onFailure(Error(res.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun getPromotedProducts(token: String, listener: ArrayResponse<Product>) {
        api.get_promoted_product(token).enqueue(object: Callback<WrappedListResponse<Product>>{
            override fun onFailure(call: Call<WrappedListResponse<Product>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedListResponse<Product>>, response: Response<WrappedListResponse<Product>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }

        })
    }

    override fun searchProductCatalog(token: String, q: String, listener: SingleResponse<GeneralProductSearch>) {
        api.catalog_search(token, q).enqueue(object : Callback<WrappedResponse<GeneralProductSearch>>{
            override fun onFailure(call: Call<WrappedResponse<GeneralProductSearch>>, t: Throwable) = listener.onFailure(Error((t.message)))

            override fun onResponse(call: Call<WrappedResponse<GeneralProductSearch>>, response: Response<WrappedResponse<GeneralProductSearch>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun deleteProduct(token: String, storeId: String, productId: String, listener: SingleResponse<Product>) {
        api.product_delete(token, storeId, productId).enqueue(object : Callback<WrappedResponse<Product>>{
            override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body()
                        if(b!!.status) listener.onSuccess(b.data) else listener.onFailure(Error(b.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun updateProductOnly(token: String, storeId: String, product: Product, categoryId: Int, listener: SingleResponse<Product>) {
        product.categoryId = categoryId
        val gsonBuilder = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()
        val requestBody = gsonBuilder.toJson(product)
        println(Gson().toJson(product))
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody)
        api.product_update(token, storeId, product.id.toString(), body).enqueue(object : Callback<WrappedResponse<Product>>{
            override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body()
                        if(b!!.status) listener.onSuccess(b.data) else listener.onFailure(Error(b.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun updateProductWithImage(token: String, storeId: String, product: Product, categoryId: Int, listener: SingleResponse<Product>) {
        val requestBody = PaperlessUtil.jsonToMapRequestBody(Gson().toJson(product))
        val file = File(product.image.toString())
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        println(Gson().toJson(product))
        val image = MultipartBody.Part.createFormData("image", file.name, requestBodyForFile)
        api.product_update(token, storeId, product.id.toString(), requestBody, categoryId, image).enqueue(object : Callback<WrappedResponse<Product>>{
            override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body()
                        if(b!!.status) listener.onSuccess(b.data) else listener.onFailure(Error(b.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun fetchAllProducts(token: String, storeId: String, listener: ArrayResponse<Product>) {
        api.product_get(token, storeId).enqueue(object : Callback<WrappedListResponse<Product>> {
            override fun onFailure(call: Call<WrappedListResponse<Product>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedListResponse<Product>>, response: Response<WrappedListResponse<Product>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun createProduct(token: String, storeId: String, product: Product, categoryId: Int, listener: SingleResponse<Product>) {
        val file = File(product.image.toString())
        val requestBody = PaperlessUtil.jsonToMapRequestBody(Gson().toJson(product))
        val requestBodyForFile = RequestBody.create(MediaType.parse("image/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, requestBodyForFile)
        api.product_store(token, storeId, requestBody, categoryId , image).enqueue(object : Callback<WrappedResponse<Product>>{
            override fun onFailure(call: Call<WrappedResponse<Product>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedResponse<Product>>, response: Response<WrappedResponse<Product>>) {
                when{
                    response.isSuccessful -> {
                        val b = response.body()
                        if(b!!.status) listener.onSuccess(b.data) else listener.onFailure(Error(b.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }
}