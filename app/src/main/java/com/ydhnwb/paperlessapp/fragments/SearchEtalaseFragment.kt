package com.ydhnwb.paperlessapp.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mancj.materialsearchbar.MaterialSearchBar
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.EtalaseAdapter
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.fragment_search_etalase.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchEtalaseFragment : Fragment(R.layout.fragment_search_etalase){
    private val productViewModel : ProductViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        productViewModel.listenToFilteredProducts().observe(viewLifecycleOwner, Observer {
            handleState(it)
        })
    }

    private fun setupUI(){
        view!!.rv_search_result.apply {
            layoutManager = if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                GridLayoutManager(activity, 2)
            }else{
                GridLayoutManager(activity, 4)
            }
            adapter = EtalaseAdapter(mutableListOf(), activity!!, productViewModel)
        }
        view!!.search_bar.setOnSearchActionListener(object: MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {}
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence?) {
                text?.let {
                    it.toString().trim().isNotEmpty().let { _ ->
                        productViewModel.filterByName(it.toString().trim())
                        view!!.search_bar.clearFocus()
                    }
                }
            }
        })
    }

    private fun handleState(it : MutableList<Product>){
        view!!.rv_search_result.adapter?.let { a ->
            a as EtalaseAdapter
            a.updateList(it)
        }
    }
}