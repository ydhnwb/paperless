package com.ydhnwb.paperlessapp.ui.main.explore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mancj.materialsearchbar.MaterialSearchBar
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.catalog.CatalogActivity
import com.ydhnwb.paperlessapp.shared_adapter.CategoryAdapter
import com.ydhnwb.paperlessapp.models.Category
import kotlinx.android.synthetic.main.fragment_explore.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExploreFragment : Fragment(R.layout.fragment_explore){
    private val exploreViewModel: ExploreViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupSearchBar()
        exploreViewModel.fetchCategories()
        exploreViewModel.listenToCategories().observe(viewLifecycleOwner, Observer { handleCategory(it) })
        exploreViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleCategoryState(it) })
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

    private fun handleCategoryState(it: ExploreState){
        when(it){
            is ExploreState.ShowToast -> toast(it.message)
            is ExploreState.IsLoading -> view!!.loading_category.isIndeterminate = it.state
        }
    }

    private fun setupSearchBar(){
        view!!.search_bar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {}
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence?) {
                if(text != null && text.isNotEmpty()){
                    startActivity(Intent(activity, CatalogActivity::class.java).apply {
                        putExtra("q", text.toString())
                    })
                }else{
                    toast(resources.getString(R.string.info_empty_query))
                }
            }
        })
    }

    private fun toast(message : String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

}