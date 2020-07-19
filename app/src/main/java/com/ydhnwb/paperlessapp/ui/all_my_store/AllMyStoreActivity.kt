package com.ydhnwb.paperlessapp.ui.all_my_store

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.ui.manage.ManageActivity
import com.ydhnwb.paperlessapp.ui.store.CreateStoreActivity
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.activity_all_my_store.*
import kotlinx.android.synthetic.main.content_all_my_store.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllMyStoreActivity : AppCompatActivity(), AllMyStoreInterface {
    private val allMyStoreViewModel: AllMyStoreViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_my_store)
        setSupportActionBar(toolbar)
        setupToolbar()
        setupRecyclerView()
        observe()

    }

    override fun onResume() {
        super.onResume()
        fetchStores()
    }

    private fun setupToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun observe(){
        observeState()
        observeStores()
    }

    private fun observeStores() = allMyStoreViewModel.getStores().observe(this, Observer { handleStores(it) })
    private fun observeState() = allMyStoreViewModel.getState().observer(this, Observer { handleState(it) })

    private fun setupRecyclerView(){
        store_recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AllMyStoreActivity)
            adapter = AllMyStoreAdapter(mutableListOf(), this@AllMyStoreActivity)
        }
    }

    private fun isLoading(isLoading: Boolean){
        if(isLoading){
            loading.visible()
        }else{
            loading.gone()
        }
    }

    private fun handleStores(stores: List<Store>){
        store_recyclerView.adapter?.let { a ->
            if(a is AllMyStoreAdapter){
                a.updateList(stores)
            }
        }
    }

    private fun fetchStores() = PaperlessUtil.getToken(this@AllMyStoreActivity)?.let {
        allMyStoreViewModel.fetchMyStores(
            it
        )
    }

    private fun handleState(state: AllMyStoreState){
        when(state){
            is AllMyStoreState.SuccessDelete -> {
                showToast("Sukses menghapus toko")
                fetchStores()
            }
            is AllMyStoreState.Loading -> isLoading(state.isLoading)
            is AllMyStoreState.ShowToast -> showToast(state.message)
        }
    }

    override fun click(store: Store, view: View) {
        PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.menu_common_store, menu)
            setOnMenuItemClickListener { menuItems ->
                when (menuItems.itemId) {
                    R.id.menu_detail -> {
                        startActivity(
                            Intent(this@AllMyStoreActivity, ManageActivity::class.java).apply {
                                putExtra("STORE", store)
                            })
                        true
                    }
                    R.id.menu_edit -> {
                        startActivity(
                            Intent(this@AllMyStoreActivity, CreateStoreActivity::class.java).apply {
                                putExtra("STORE", store)
                            })
                        true
                    }
                    R.id.menu_delete -> {
                        val token = PaperlessUtil.getToken(this@AllMyStoreActivity)
                        token?.let { it1 -> allMyStoreViewModel.deleteStore(it1, store.id.toString()) }
                        true
                    }
                    else -> true
                }
            }
        }.show()
    }


}