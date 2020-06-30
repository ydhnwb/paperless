package com.ydhnwb.paperlessapp.utilities

interface SingleResponse<T>{
    fun onSuccess(data: T?)
    fun onFailure(err: Error)
}

interface ArrayResponse<T>{
    fun onSuccess(datas: List<T>?)
    fun onFailure(err: Error)
}