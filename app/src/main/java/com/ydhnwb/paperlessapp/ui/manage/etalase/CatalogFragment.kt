package com.ydhnwb.paperlessapp.ui.manage.etalase

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.catalog.CatalogViewModel
import com.ydhnwb.paperlessapp.shared_adapter.CatalogAdapter
import com.ydhnwb.paperlessapp.models.Category
import kotlinx.android.synthetic.main.fragment_catalog.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CatalogFragment : Fragment(R.layout.fragment_catalog){
    companion object {
        fun instance(category : Category?) : CatalogFragment {
            return if(category == null){
                CatalogFragment()
            }else{
                val args = Bundle()
                args.putParcelable("category", category)
                CatalogFragment().apply {
                    arguments = args
                }
            }
        }
    }

    private val catalogViewModel : CatalogViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        if(arguments == null){
            catalogViewModel.listenToCatalogs().observe(viewLifecycleOwner, Observer {
                view.rv_catalog_products.adapter?.let { adapter ->
                    if(adapter is CatalogAdapter){
                        adapter.updateList(it)
                    }
                }
            })
        }else{
            arguments?.getParcelable<Category>("category")!!.let {c ->
                catalogViewModel.listenToCatalogs().observe(viewLifecycleOwner, Observer {
                    view.rv_catalog_products.adapter?.let { a -> if(a is CatalogAdapter){
                        val filtered = it.filter { product -> product.category!!.name!!.equals(c.name) }
                        a.updateList(filtered)
                    }}
                })
            }
        }
    }

    private fun setupUI(){
        view!!.rv_catalog_products.apply {
            layoutManager = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){ GridLayoutManager(activity, 2) }else{
                GridLayoutManager(activity, 4)
            }
            adapter = CatalogAdapter(mutableListOf(), activity!!)
        }
    }
}