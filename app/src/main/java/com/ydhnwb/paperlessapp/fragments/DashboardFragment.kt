package com.ydhnwb.paperlessapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.StoreActivity
import com.ydhnwb.paperlessapp.adapters.StoreAdapter
import com.ydhnwb.paperlessapp.contracts.fragments.DashboardFragmentContract
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.presenters.activities.MainActivityPresenter
import com.ydhnwb.paperlessapp.presenters.fragments.DashboardFragmentPresenter
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class DashboardFragment(private var mainPresenter : MainActivityPresenter?) : Fragment(), DashboardFragmentContract.View {
    private var presenter = DashboardFragmentPresenter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.allMyStores()
        presenter.allOtherStore()
        view.add_store.setOnClickListener {
            startActivity(Intent(activity, StoreActivity::class.java))
        }
    }

    override fun attachToMyStores(my_stores: List<Store>) {
        view!!.rv_my_stores.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = StoreAdapter(my_stores, activity!!)
        }
    }

    override fun attachToOtherStores(other_stores: List<Store>) {
        view!!.rv_other_stores.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = StoreAdapter(other_stores, activity!!)
        }
    }

    override fun isMyStoreLoading(state: Boolean) { if(state){ view!!.loading_mystore.visibility = View.VISIBLE }else { view!!.loading_mystore.visibility = View.GONE } }

    override fun isOtherStoreLoading(state: Boolean) { if(state){ view!!.loading_other_store.visibility = View.VISIBLE }else { view!!.loading_other_store.visibility = View.GONE } }

    override fun showEmptyMyStore(state: Boolean) { if(state){ view!!.empty_store.visibility = View.VISIBLE }else { view!!.empty_store.visibility = View.GONE } }

    override fun showEmptyOtherStore(state: Boolean) { if(state){ view!!.empty_other_store.visibility = View.VISIBLE }else{ view!!.empty_other_store.visibility = View.GONE } }

}