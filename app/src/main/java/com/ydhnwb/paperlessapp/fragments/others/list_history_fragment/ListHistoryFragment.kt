package com.ydhnwb.paperlessapp.fragments.others.list_history_fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.manage_activity.ManageStoreViewModel
import com.ydhnwb.paperlessapp.adapters.HistoryAdapter
import com.ydhnwb.paperlessapp.models.History
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.fragment_content_history.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListHistoryFragment : Fragment(R.layout.fragment_content_history){
    companion object {
        fun instance(isIn : Boolean) : ListHistoryFragment {
            val a = Bundle()
            a.putBoolean("isin", isIn)
            return ListHistoryFragment()
                .apply { arguments = a }
        }
    }

    private val listHistoryViewModel : ListHistoryViewModel by viewModel()
    private val parentStoreViewModel: ManageStoreViewModel by sharedViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        listHistoryViewModel.fetchHistory(PaperlessUtil.getToken(activity!!), parentStoreViewModel.listenToCurrentStore().value?.id!!)
        listHistoryViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
        listHistoryViewModel.listenToHistory().observe(viewLifecycleOwner, Observer { handle(it) })
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

    private fun handleUIState(it: ListHistoryState){
        when(it){
            is ListHistoryState.ShowToast -> toast(it.message)
            is ListHistoryState.IsLoading -> {
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