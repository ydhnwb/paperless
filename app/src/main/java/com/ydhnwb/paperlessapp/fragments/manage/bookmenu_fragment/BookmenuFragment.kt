package com.ydhnwb.paperlessapp.fragments.manage.bookmenu_fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.manage_activity.ManageStoreState
import com.ydhnwb.paperlessapp.activities.manage_activity.ManageStoreViewModel
import com.ydhnwb.paperlessapp.adapters.EtalaseAdapter
import com.ydhnwb.paperlessapp.models.Category
import kotlinx.android.synthetic.main.fragment_bookmenu.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BookmenuFragment : Fragment(R.layout.fragment_bookmenu){
    companion object {
        fun instance(category: Category?) : BookmenuFragment {
            return if(category == null){
                BookmenuFragment()
            }else{
                val args = Bundle()
                args.putParcelable("category", category)
                BookmenuFragment()
                    .apply {
                    arguments = args
                }
            }
        }
    }

    private val parentViewModel: ManageStoreViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        parentViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
        if (arguments == null){
            view.bookmenu_empty_view.visibility = View.VISIBLE
            parentViewModel.listenToAllProducts().observe(viewLifecycleOwner, Observer {
                view.rv_bookmenu.adapter?.let { adapter -> with(adapter as EtalaseAdapter) {
                    adapter.updateList(it)
                    if (it.isEmpty()) { view.bookmenu_empty_view.visibility = View.VISIBLE }else{ view.bookmenu_empty_view.visibility = View.GONE }
                }}
            })
        }else{
            parentViewModel.listenToAllProducts().observe(viewLifecycleOwner, Observer {
                arguments!!.getParcelable<Category>("category")?.let {cat ->
                    view.rv_bookmenu.adapter?.let { adapter ->
                        adapter as EtalaseAdapter
                        val filtered = it.filter { product -> product.category!!.name!!.equals(cat.name) }
                        if (filtered.isEmpty()) { view.bookmenu_empty_view.visibility = View.VISIBLE }else{ view.bookmenu_empty_view.visibility = View.GONE }
                        adapter.updateList(filtered)
                    }
                }
            })
        }
    }

    private fun handleUIState(it: ManageStoreState){
        when(it){
            is ManageStoreState.IsLoading -> {
                if (it.state){
                    view!!.loading.visibility = View.VISIBLE
                }else{
                    view!!.loading.visibility = View.GONE
                }
            }
        }
    }

    private fun setupUI(){
        view!!.rv_bookmenu.apply {
            layoutManager = if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){ GridLayoutManager(activity, 2) }else{
                GridLayoutManager(activity, 4)
            }
            adapter = EtalaseAdapter(mutableListOf(), activity!!, parentViewModel)
        }
    }
}