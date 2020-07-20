package com.ydhnwb.paperlessapp.ui.analytic.performance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.models.UserAndTransaction
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.fragment_customer_analytic.view.*
import kotlinx.android.synthetic.main.fragment_performance_analytic.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PerformanceAnalyticFragment : Fragment(R.layout.fragment_performance_analytic){
    private val performanceViewModel : PerformanceAnalyticViewModel by viewModel()


    companion object {
        fun instance(store: Store?) : PerformanceAnalyticFragment {
            if (store != null){
                val a = Bundle()
                a.putParcelable("store", store)
                return PerformanceAnalyticFragment().apply { arguments = a }
            }
            return PerformanceAnalyticFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observe()
        fetchHistory()
    }

    private fun setupRecyclerView(){
        requireView().performance_recyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = PerformanceAnalyticAdapter(mutableListOf())
        }
    }


    private fun fetchHistory() = arguments?.let { bundle ->
        val store = bundle.getParcelable<Store>("store")
        store?.let { s ->
            PaperlessUtil.getToken(requireActivity())?.let {
                performanceViewModel.fetchOrderHistory(it, s.id.toString())
            }
        }
    }

    private fun observe(){
        observeState()
        observePerformance()
    }

    private fun observeState() = performanceViewModel.getState().observer(viewLifecycleOwner, Observer { handleState(it) })
    private fun observePerformance() = performanceViewModel.getEmployeePerformance().observe(viewLifecycleOwner, Observer { handlePerformance(it) })
    private fun isLoading(b: Boolean) = if(b) requireView().loading.visible() else requireView().loading.gone()

    private fun handlePerformance(employeePerformances : HashMap<User, Int>?){
        employeePerformances?.let {
            val temps = mutableListOf<UserAndTransaction>()
            it.forEach { (t, u) -> temps.add(UserAndTransaction(t.id, t.name, u)) }
            requireView().performance_recyclerView.adapter?.let { a ->
                if(a is PerformanceAnalyticAdapter){
                    a.updateList(temps)
                }
            }
        }
    }

    private fun handleState(state: PerformanceAnalyticState){
        when(state){
            is PerformanceAnalyticState.Loading -> isLoading(state.isLoading)
            is PerformanceAnalyticState.ShowToast -> requireActivity().showToast(state.message)
        }
    }

}