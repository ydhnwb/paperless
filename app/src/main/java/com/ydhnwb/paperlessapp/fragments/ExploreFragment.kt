package com.ydhnwb.paperlessapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.CategoryAdapter
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.viewmodels.CategoryState
import com.ydhnwb.paperlessapp.viewmodels.CategoryViewModel
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.fragment_explore.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExploreFragment : Fragment(R.layout.fragment_explore){
    private val categoryViewModel : CategoryViewModel by viewModel()
    private val productViewModel : ProductViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryViewModel.fetchCategory()
        setupUI()
        categoryViewModel.listenCategories().observe(viewLifecycleOwner, Observer { handleCategory(it) })
        categoryViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleCategoryState(it) })
    }

    private fun setupUI(){
        view!!.rv_category.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = CategoryAdapter(mutableListOf<Category>(), activity!!)
        }
    }

    private fun handleCategory(it : List<Category>){
        view!!.rv_category.adapter?.let { adapter ->
            if(adapter is CategoryAdapter){
                adapter.updateList(it)
            }
        }
    }

    private fun handleCategoryState(it: CategoryState){
        when(it){
            is CategoryState.ShowToast -> toast(it.message)
            is CategoryState.IsLoading -> view!!.loading_category.isIndeterminate = it.state
        }
    }

    private fun toast(message : String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

}