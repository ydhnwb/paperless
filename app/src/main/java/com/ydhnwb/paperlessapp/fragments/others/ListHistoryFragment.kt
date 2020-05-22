package com.ydhnwb.paperlessapp.fragments.others

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.HistoryAdapter
import com.ydhnwb.paperlessapp.models.History
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.HistoryState
import com.ydhnwb.paperlessapp.viewmodels.HistoryViewModel
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import kotlinx.android.synthetic.main.fragment_content_history.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListHistoryFragment : Fragment(R.layout.fragment_content_history){
    companion object {
        fun instance(isIn : Boolean) : ListHistoryFragment {
            val a = Bundle()
            a.putBoolean("isin", isIn)
            return ListHistoryFragment().apply { arguments = a }
        }
    }

    private val historyViewModel : HistoryViewModel by viewModel()
    private val parentStoreViewModel: StoreViewModel by sharedViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        historyViewModel.fetchHistory(PaperlessUtil.getToken(activity!!), parentStoreViewModel.getCurrentStore()?.id)
        historyViewModel.listenToState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
        historyViewModel.listenToOrderHistory().observe(viewLifecycleOwner, Observer { handle(it) })
    }

    private fun setupUI(){
        view!!.rv_history.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = HistoryAdapter(mutableListOf(), activity!!, false)
        }
    }

    private fun handle(it: History){
        val isIn = arguments?.getBoolean("isin", false)!!
        val o = if (isIn) it.store?.orderIn!! else it.store?.orderOut!!
        view!!.rv_history.adapter.let { a ->
            if(a is HistoryAdapter){
                a.updateList(o, isIn)
            }
        }
    }

    private fun handleUIState(it: HistoryState){
        when(it){
            is HistoryState.ShowToast -> toast(it.message)
            is HistoryState.IsLoading -> {
                if(it.state){
                    view!!.loading.visibility = View.VISIBLE
                }else{
                    view!!.loading.visibility = View.GONE
                }
            }
        }
    }

    private fun toast(m : String) = Toast.makeText(activity, m, Toast.LENGTH_LONG).show()
}