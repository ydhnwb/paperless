package com.ydhnwb.paperlessapp.ui.manage.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.manage.history.list_history.ListHistoryFragment
import com.ydhnwb.paperlessapp.utilities.CustomFragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_history.view.*

class HistoryFragment : Fragment(R.layout.fragment_history){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI(){
        val fragmentAdapter = CustomFragmentPagerAdapter(childFragmentManager)
        fragmentAdapter.addFragment(ListHistoryFragment.instance(true), resources.getString(R.string.history_in))
        fragmentAdapter.addFragment(ListHistoryFragment.instance(false), resources.getString(R.string.history_out))
        view!!.viewpager.adapter = fragmentAdapter
        view!!.tabs.setupWithViewPager(view!!.viewpager)
    }

}