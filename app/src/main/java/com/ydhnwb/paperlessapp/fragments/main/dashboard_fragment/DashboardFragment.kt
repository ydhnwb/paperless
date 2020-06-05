package com.ydhnwb.paperlessapp.fragments.main.dashboard_fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.store_activity.CreateStoreActivity
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.ydhnwb.paperlessapp.fragments.main.dashboard_fragment.DashboardState

class DashboardFragment : Fragment(R.layout.fragment_dashboard){
    private val dashboardViewModel: DashboardViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        dashboardViewModel.listenToMyStores().observe(viewLifecycleOwner, Observer { attachToMyStores(it) })
        dashboardViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
        view.add_store.setOnClickListener { startActivity(Intent(activity, CreateStoreActivity::class.java)) }
    }

    private fun handleUIState(it : DashboardState){
        when(it){
            is DashboardState.SuccessDeleted -> dashboardViewModel.fetchMyStores(PaperlessUtil.getToken(activity!!))
            is DashboardState.ShowToast -> toast(it.message)
            is DashboardState.IsLoading -> {
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

//    private fun attachToOtherStores(other_stores: List<Store>) {
//        view!!.rv_other_stores.adapter?.let {
//            if(it is StoreAdapter){
//                it.updateList(other_stores)
//            }
//        }
//        showEmptyOtherStore(other_stores.isNullOrEmpty())
//    }

    private fun setupUI(){
        view!!.rv_my_stores.apply {
            layoutManager = LinearLayoutManager(activity).apply { orientation = LinearLayoutManager.HORIZONTAL }
            adapter =
                StoreAdapter(
                    mutableListOf(),
                    activity!!,
                    dashboardViewModel
                )
        }
        view!!.rv_other_stores.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter =
                StoreAdapter(
                    mutableListOf(),
                    activity!!,
                    dashboardViewModel
                )
        }
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.fetchMyStores(PaperlessUtil.getToken(requireActivity()))
    }

    private fun toast(message : String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    private fun isMyStoreLoading(state: Boolean) { if(state){ view!!.loading_mystore.visibility = View.VISIBLE }else { view!!.loading_mystore.visibility = View.GONE } }
    private fun isOtherStoreLoading(state: Boolean) { if(state){ view!!.loading_other_store.visibility = View.VISIBLE }else { view!!.loading_other_store.visibility = View.GONE } }
    private fun showEmptyMyStore(state: Boolean) { if(state){ view!!.empty_store.visibility = View.VISIBLE }else { view!!.empty_store.visibility = View.GONE } }
//    private fun showEmptyOtherStore(state: Boolean) { if(state){ view!!.empty_other_store.visibility = View.VISIBLE }else{ view!!.empty_other_store.visibility = View.GONE } }
}