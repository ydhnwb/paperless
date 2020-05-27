package com.ydhnwb.paperlessapp.fragments.others

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.CatalogAdapter
import com.ydhnwb.paperlessapp.models.Category
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import kotlinx.android.synthetic.main.fragment_store_catalog.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class StoreCatalogFragment : Fragment(R.layout.fragment_store_catalog){
    private val storeViewModel: StoreViewModel by sharedViewModel()

    companion object {
        fun instance(category : Category?) : StoreCatalogFragment {
            return if(category == null){
                StoreCatalogFragment()
            }else{
                val args = Bundle()
                args.putParcelable("category", category)
                StoreCatalogFragment().apply { arguments = args }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        if(arguments == null){
            storeViewModel.listenToStore().observe(viewLifecycleOwner, Observer {
                view.rv_catalog.adapter?.let { adapter ->
                    if(it.products.isNotEmpty()){
                        if(adapter is CatalogAdapter){
                            Toast.makeText(activity, it.products.size.toString(), Toast.LENGTH_LONG).show()
                            adapter.updateList(it.products)
                        }
                    }
                }
            })
        }else{
            arguments?.getParcelable<Category>("category")!!.let {c ->
                storeViewModel.listenToStore().observe(viewLifecycleOwner, Observer {
                    view.rv_catalog.adapter?.let { a ->
                        if(a is CatalogAdapter){
                            val filtered = it.products.filter { product ->
                                product.category!!.name!!.equals(c.name)
                            }
                            Toast.makeText(activity, it.products.size.toString(), Toast.LENGTH_LONG).show()
                            a.updateList(filtered)
                        }
                    }
                })
            }
        }
    }

    private fun setupUI(){
        view!!.rv_catalog.apply {
            layoutManager = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){ GridLayoutManager(activity, 2) }else{
                GridLayoutManager(activity, 4)
            }
            adapter = CatalogAdapter(mutableListOf(), activity!!)
        }
    }
}