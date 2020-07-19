package com.ydhnwb.paperlessapp.ui.analytic.customer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.fragment_customer_analytic.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CustomerAnalyticFragment : Fragment(R.layout.fragment_customer_analytic){
    private val customerAnalyticViewModel: CustomerAnalyticViewModel by viewModel()

    companion object {
        fun instance(store: Store?) : CustomerAnalyticFragment {
            if (store != null){
                val a = Bundle()
                a.putParcelable("store", store)
                return CustomerAnalyticFragment().apply { arguments = a }
            }
            return CustomerAnalyticFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        observe()

    }

    private fun observe(){
        arguments?.let {
            val store = it.getParcelable<Store>("store")
            store?.let { s ->
                customerAnalyticViewModel.listenToState().observer(viewLifecycleOwner, Observer { state -> handleState(state) })
                customerAnalyticViewModel.listenStoreWhoBuy().observe(viewLifecycleOwner, Observer { x -> handleStoreWhoBuy(x) })
                customerAnalyticViewModel.listenToUserWhoBuy().observe(viewLifecycleOwner, Observer { x -> handleUserWhoBuy(x) })
                PaperlessUtil.getToken(requireActivity())?.let { it1 ->
                    customerAnalyticViewModel.fetchStoreInfo(
                        it1, s.id.toString())
                }
            }
        }
    }

    private fun setupRecycler(){
        view!!.rv_store_who_buy.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = StoreWhoBuyAdapter(mutableListOf())
        }

        view!!.rv_user_who_buy.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = UserWhoBuyAdapter(mutableListOf())
        }
    }

    private fun handleStoreWhoBuy(it: HashMap<Store, Int>){
        val temps = mutableListOf<StoreAndTransaction>()
        it.forEach { (t, u) -> temps.add(StoreAndTransaction(t, u)) }
        view!!.rv_store_who_buy.adapter?.let { a ->
            if(a is StoreWhoBuyAdapter){
                a.updateList(temps)
            }
        }
        if(temps.isNullOrEmpty()){
            requireView().store_who_buy_emptyView.visible()
        }else{
            requireView().store_who_buy_emptyView.gone()
        }
    }

    private fun handleUserWhoBuy(it: HashMap<User, Int>){
        val temps = mutableListOf<UserAndTransaction>()
        it.forEach { (t, u) -> temps.add(UserAndTransaction(t, u)) }
        view!!.rv_user_who_buy.adapter?.let { a ->
            if(a is UserWhoBuyAdapter){
                a.updateList(temps)
            }
        }
        if(temps.isNullOrEmpty()){
            requireView().user_who_buy_emptyView.visible()
        }else{
            requireView().user_who_buy_emptyView.gone()
        }
    }

    private fun handleState(it: CustomerAnalyticFragmentState){
        println("s")
    }

}