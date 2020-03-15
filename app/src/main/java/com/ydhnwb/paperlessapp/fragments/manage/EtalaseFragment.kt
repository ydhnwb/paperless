package com.ydhnwb.paperlessapp.fragments.manage

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.EtalaseAdapter
import com.ydhnwb.paperlessapp.viewmodels.ProductState
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.fragment_etalase.view.*

class EtalaseFragment : Fragment(R.layout.fragment_etalase) {
    private lateinit var productViewModel: ProductViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rv_etalase.apply {
            layoutManager = if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                GridLayoutManager(activity, 2)
            }else{
                GridLayoutManager(activity, 4)
            }
            adapter = EtalaseAdapter(mutableListOf(), activity!!)
        }
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        productViewModel.listenToMyProducts().observe(viewLifecycleOwner, Observer {
            view.rv_etalase.adapter?.let { adapter ->
                if(adapter is EtalaseAdapter){
                    adapter.updateList(it)
                }
            }
        })
        productViewModel.listenToUIState().observe(viewLifecycleOwner, Observer {
            when(it){
                is ProductState.ShowToast -> toast(it.message)
                is ProductState.IsLoading -> {
                    if(it.state){
                        view.loading.visibility = View.VISIBLE
                    }else{
                        view.loading.visibility = View.GONE
                    }
                }
            }
        })
        productViewModel.fetchMyProducts()
    }

    private fun toast(message : String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}