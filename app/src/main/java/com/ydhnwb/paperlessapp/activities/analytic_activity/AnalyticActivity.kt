package com.ydhnwb.paperlessapp.activities.analytic_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.fragments.analytic.customer.CustomerAnalyticFragment
import com.ydhnwb.paperlessapp.fragments.analytic.purchasement.PurchasementFragment
import com.ydhnwb.paperlessapp.fragments.analytic.selling.SellingFragment
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.CustomFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_analytic.*
import kotlinx.android.synthetic.main.content_analytic.*

class AnalyticActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytic)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        setupUI()
    }

    private fun setupUI(){
        val fragmentAdapter = CustomFragmentPagerAdapter(supportFragmentManager)
        fragmentAdapter.addFragment(SellingFragment.instance(getStore()), resources.getString(R.string.tab_title_selling))
        fragmentAdapter.addFragment(PurchasementFragment(), resources.getString(R.string.tab_title_purchasement))
        fragmentAdapter.addFragment(CustomerAnalyticFragment(), resources.getString(R.string.tab_title_customer))
        viewpager.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewpager)
    }

    private fun getStore() = intent.getParcelableExtra<Store>("store")!!

}