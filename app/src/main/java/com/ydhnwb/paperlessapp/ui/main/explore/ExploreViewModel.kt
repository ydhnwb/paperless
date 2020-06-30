package com.ydhnwb.paperlessapp.ui.main.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.repositories.CategoryRepository
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class ExploreViewModel (private val categoryRepository: CategoryRepository) : ViewModel(){
    private var state : SingleLiveEvent<ExploreState> = SingleLiveEvent()
    private var categories = MutableLiveData<List<Category>>()

    private fun setLoading(){ state.value = ExploreState.IsLoading(true) }
    private fun hideLoading(){ state.value = ExploreState.IsLoading(false) }
    private fun toast(message: String){ state.value = ExploreState.ShowToast(message) }

    fun fetchCategories(){
        setLoading()
        categoryRepository.getCategories(object: ArrayResponse<Category>{
            override fun onSuccess(datas: List<Category>?) {
                hideLoading()
                datas?.let { categories.postValue(it) }
            }
            override fun onFailure(err: Error) {
                hideLoading()
                err.message?.let { toast(it) }
            }
        })
    }

    fun listenToUIState() = state
    fun listenToCategories() = categories
}

sealed class ExploreState {
    data class IsLoading(var state : Boolean) : ExploreState()
    data class ShowToast(var message: String) : ExploreState()
}