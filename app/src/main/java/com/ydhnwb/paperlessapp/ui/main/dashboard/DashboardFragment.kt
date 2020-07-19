package com.ydhnwb.paperlessapp.ui.main.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.manage.ManageActivity
import com.ydhnwb.paperlessapp.ui.store.CreateStoreActivity
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.ydhnwb.paperlessapp.models.MyWorkplace
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.visible

class DashboardFragment : Fragment(R.layout.fragment_dashboard){
    private val dashboardViewModel: DashboardViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        dashboardViewModel.listenToMyStores().observe(viewLifecycleOwner, Observer { attachToMyStores(it) })
        dashboardViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
        dashboardViewModel.listenToMyWorkplace().observe(viewLifecycleOwner, Observer { handleMyWorkplace(it) })
        view.add_store.setOnClickListener { startActivity(Intent(activity, CreateStoreActivity::class.java)) }
    }

    private fun handleUIState(it : DashboardState){
        when(it){
            is DashboardState.SuccessDeleted -> PaperlessUtil.getToken(activity!!)?.let { it1 ->
                dashboardViewModel.fetchMyStores(
                    it1
                )
            }
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


    private fun handleMyWorkplace(it: MyWorkplace){
        if (dashboardViewModel.listenToMyWorkplace().value == null){
            requireView().workplace_root.visibility = View.GONE
            requireView().empty_other_store.visible()
        }else{
            if(dashboardViewModel.listenToMyWorkplace().value?.store?.id == null){
                requireView().empty_other_store.visible()
                requireView().workplace_root.visibility = View.GONE
            }else{
                requireView().empty_other_store.gone()
                requireView().workplace_root.visibility = View.VISIBLE
                with(requireView()){
                    workplace_name.text = it.store?.name.toString()
                    workplace_logo.load(it.store?.store_logo)
                    workplace_role.text = if(it.role == 0){ resources.getString(R.string.cashier) }else{ resources.getString(R.string.staff) }
                    workplace_layout.setOnClickListener {_ ->
                        startActivity(Intent(requireActivity(), ManageActivity::class.java).apply {
                            putExtra("STORE", it.store)
                            putExtra("ROLE", it.role)
                        })
                    }
                }
            }
        }
    }

    private fun setupUI(){
        view!!.rv_my_stores.apply {
            layoutManager = LinearLayoutManager(activity).apply { orientation = LinearLayoutManager.HORIZONTAL }
            adapter = StoreAdapter(mutableListOf(), activity!!, dashboardViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        PaperlessUtil.getToken(requireActivity())?.let { dashboardViewModel.fetchMyStores(it) }
        PaperlessUtil.getToken(requireActivity())?.let { dashboardViewModel.fetchMyWorkplace(it) }
    }

    private fun toast(message : String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    private fun isMyStoreLoading(state: Boolean) { if(state){ view!!.loading_mystore.visibility = View.VISIBLE }else { view!!.loading_mystore.visibility = View.GONE } }
    private fun isOtherStoreLoading(state: Boolean) { if(state){ view!!.loading_other_store.visibility = View.VISIBLE }else { view!!.loading_other_store.visibility = View.GONE } }
    private fun showEmptyMyStore(state: Boolean) { if(state){ view!!.empty_store.visibility = View.VISIBLE }else { view!!.empty_store.visibility = View.GONE } }
    private fun showEmptyOtherStore(state: Boolean) { if(state){ view!!.empty_other_store.visibility = View.VISIBLE }else{ view!!.empty_other_store.visibility = View.GONE } }
}