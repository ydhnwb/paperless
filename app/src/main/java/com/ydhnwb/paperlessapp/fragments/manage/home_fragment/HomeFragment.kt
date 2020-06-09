package com.ydhnwb.paperlessapp.fragments.manage.home_fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.manage_activity.ManageStoreViewModel
import com.ydhnwb.paperlessapp.adapters.StoreMenuAdapter
import com.ydhnwb.paperlessapp.models.StoreMenu
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var storeMenus : List<StoreMenu>
    private val parentStoreViewModel : ManageStoreViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fill()
        storeMenu()
    }

    private fun fill(){
        parentStoreViewModel.listenToCurrentStore().observe(viewLifecycleOwner, Observer {
            if(it != null){
                with(view!!){
                    store_name.text = it.name
                    store_address.text = it.address
                    store_image.load(it.store_logo)
                    view!!.rv_store_menu.apply {
                        adapter = StoreMenuAdapter(storeMenus, context, parentStoreViewModel.listenToCurrentStore().value!!)
                        layoutManager = GridLayoutManager(activity, 2)
                    }
                }
            }
        })

    }

    private fun storeMenu(){
        storeMenus = listOf(
            StoreMenu(resources.getString(R.string.store_menu_report), R.drawable.ic_doodle_mail, ContextCompat.getColor(activity!!, R.color.colorFlueGreen)),
            StoreMenu(resources.getString(R.string.store_menu_invitation), R.drawable.ic_doodle_connection, ContextCompat.getColor(activity!!, R.color.colorRed)),
            StoreMenu(resources.getString(R.string.store_menu_customer), R.drawable.ic_doodle_enthusiast, ContextCompat.getColor(activity!!, R.color.colorOrange)),
            StoreMenu(resources.getString(R.string.store_menu_invitation), R.drawable.ic_doodle_connection, ContextCompat.getColor(activity!!, R.color.colorGreen))
        )
    }

}