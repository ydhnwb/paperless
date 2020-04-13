package com.ydhnwb.paperlessapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.CreateStoreActivity
import com.ydhnwb.paperlessapp.adapters.StoreAdapter
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.StoreState
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment(R.layout.fragment_dashboard){
    private val storeViewModel: StoreViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        storeViewModel.listenToMyStore().observe(viewLifecycleOwner, Observer { attachToMyStores(it) })
        storeViewModel.listenToOtherStore().observe(viewLifecycleOwner, Observer { attachToOtherStores(it) })
        storeViewModel.listenUIState().observe(viewLifecycleOwner, Observer { handleUIState(it) })
        view.add_store.setOnClickListener { startActivity(Intent(activity, CreateStoreActivity::class.java)) }
    }

    private fun handleUIState(it : StoreState){
        when(it){
            is StoreState.Deleted -> {
                storeViewModel.fetchStore(PaperlessUtil.getToken(activity!!))
            }
            is StoreState.ShowToast -> toast(it.message)
            is StoreState.IsLoading -> {
                if(it.isOther){
                    isOtherStoreLoading(it.isLoading)
                }else{
                    isMyStoreLoading(it.isLoading)
                }
            }
        }
    }

    private fun attachToMyStores(my_stores: List<Store>) {
        view!!.rv_my_stores.adapter?.let {
            if(it is StoreAdapter){
                it.updateList(my_stores)
            }
        }
        showEmptyMyStore(my_stores.isNullOrEmpty())
    }

    private fun attachToOtherStores(other_stores: List<Store>) {
        view!!.rv_other_stores.adapter?.let {
            if(it is StoreAdapter){
                it.updateList(other_stores)
            }
        }
        showEmptyOtherStore(other_stores.isNullOrEmpty())
    }

    private fun setupUI(){
        view!!.rv_my_stores.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = StoreAdapter(mutableListOf(), activity!!, storeViewModel)
        }
        view!!.rv_other_stores.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = StoreAdapter(mutableListOf(), activity!!, storeViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        storeViewModel.fetchOtherStore()
        storeViewModel.fetchStore(PaperlessUtil.getToken(activity!!))
    }

    private fun toast(message : String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    private fun isMyStoreLoading(state: Boolean) { if(state){ view!!.loading_mystore.visibility = View.VISIBLE }else { view!!.loading_mystore.visibility = View.GONE } }
    private fun isOtherStoreLoading(state: Boolean) { if(state){ view!!.loading_other_store.visibility = View.VISIBLE }else { view!!.loading_other_store.visibility = View.GONE } }
    private fun showEmptyMyStore(state: Boolean) { if(state){ view!!.empty_store.visibility = View.VISIBLE }else { view!!.empty_store.visibility = View.GONE } }
    private fun showEmptyOtherStore(state: Boolean) { if(state){ view!!.empty_other_store.visibility = View.VISIBLE }else{ view!!.empty_other_store.visibility = View.GONE } }
}