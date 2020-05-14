package com.ydhnwb.paperlessapp.fragments.manage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import com.ydhnwb.paperlessapp.viewmodels.TransactionState
import com.ydhnwb.paperlessapp.viewmodels.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_transaction.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionFragment : Fragment(R.layout.fragment_transaction){
    private val transactionViewModel: TransactionViewModel by viewModel()
    private val parentStoreViewModel: StoreViewModel by sharedViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        transactionViewModel.allTransaction(PaperlessUtil.getToken(activity!!), parentStoreViewModel.getCurrentStore()?.id.toString())
        transactionViewModel.listenToState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
        transactionViewModel.listenToTransactions().observe(viewLifecycleOwner, Observer {
            println("Ya...")
        })
    }

    private fun setupUI(){
        view!!.rv_transaction.apply {
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun handleUIState(it: TransactionState){
        when(it){
            is TransactionState.ShowToast -> toast(it.message)
            is TransactionState.IsLoading -> {
                if(it.state){
                    view!!.loading.visibility = View.VISIBLE
                }else{
                    view!!.loading.visibility = View.GONE
                }
            }
        }
    }
    private fun toast(m : String) = Toast.makeText(requireActivity(), m, Toast.LENGTH_LONG).show()
}