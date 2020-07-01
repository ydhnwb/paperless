package com.ydhnwb.paperlessapp.ui.main.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.repositories.CategoryRepository
import com.ydhnwb.paperlessapp.repositories.ProductRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class ExploreViewModel (private val productRepo: ProductRepository) : ViewModel(){
    private var state : SingleLiveEvent<ExploreState> = SingleLiveEvent()
    private val promotedProducts = MutableLiveData<HashMap<String, List<Product>>>()

    private fun setLoading(){ state.value = ExploreState.IsLoading(true) }
    private fun hideLoading(){ state.value = ExploreState.IsLoading(false) }
    private fun toast(message: String){ state.value = ExploreState.ShowToast(message) }


    fun fetchPromotedProducts(token: String){
        setLoading()
        productRepo.getPromotedProducts(token, object: ArrayResponse<Product>{
            override fun onSuccess(datas: List<Product>?) {
                hideLoading()
                datas?.let {
                    promotedProducts.postValue(it.groupBy { p->
                        p.category?.name.toString()
                    } as HashMap<String, List<Product>>?)
                }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }


    fun listenToUIState() = state
    fun listenToPromotedProducts() = promotedProducts
}

sealed class ExploreState {
    data class IsLoading(var state : Boolean) : ExploreState()
    data class ShowToast(var message: String) : ExploreState()
}