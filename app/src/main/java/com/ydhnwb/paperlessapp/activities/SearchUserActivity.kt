package com.ydhnwb.paperlessapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mancj.materialsearchbar.MaterialSearchBar
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.SearchResultUserAdapter
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.UserState
import com.ydhnwb.paperlessapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_search_user.*
import kotlinx.android.synthetic.main.content_search_user.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchUserActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)
        setupUI()
        userViewModel.listenToSearchResult().observe(this, Observer {
            handleSearchResult(it)
        })
        userViewModel.getUIState().observer(this, Observer {
            handleUIState(it)
        })

    }

    private fun getPassedStore() = intent.getParcelableExtra<Store>("store")

    private fun setupUI(){
        rv_user.apply {
            layoutManager = LinearLayoutManager(this@SearchUserActivity)
            adapter = SearchResultUserAdapter(mutableListOf(), this@SearchUserActivity, userViewModel, getPassedStore())
        }
        search_bar.setOnSearchActionListener(object: MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {}
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence?) {
                text?.let {
                    it.isNotEmpty().let { _ ->
                        userViewModel.search(PaperlessUtil.getToken(this@SearchUserActivity),it.toString())
                    }
                }
            }
        })
    }

    private fun handleUIState(it : UserState){
        when(it){
            is UserState.ShowToast -> toast(it.message)
            is UserState.IsLoading -> {
                isEmptyView(!it.state)
                if(it.state){
                    loading.visibility = View.VISIBLE
                }else{
                    loading.visibility = View.GONE
                }
            }
        }
    }

    private fun handleSearchResult(it: List<User>){
        isEmptyView(it.isEmpty())
        rv_user.adapter?.let {adapter ->
            if(adapter is SearchResultUserAdapter){
                adapter.updateList(it)
            }
        }
    }

    private fun isEmptyView(state : Boolean){
        if(state) empty_view.visibility = View.VISIBLE else empty_view.visibility = View.GONE
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

}
