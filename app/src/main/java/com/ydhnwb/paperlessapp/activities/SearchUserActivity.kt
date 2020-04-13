package com.ydhnwb.paperlessapp.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mancj.materialsearchbar.MaterialSearchBar
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.SearchResultUserAdapter
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.UserState
import com.ydhnwb.paperlessapp.viewmodels.UserViewModel

import kotlinx.android.synthetic.main.activity_search_user.*
import kotlinx.android.synthetic.main.content_search_user.*

class SearchUserActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)
        setSupportActionBar(toolbar)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        setupUI()
        userViewModel.listenToSearchResult().observe(this, Observer {
            handleSearchResult(it)
        })
        userViewModel.getUIState().observer(this, Observer {
            handleUIState(it)
        })

    }

    private fun setupUI(){
        rv_user.apply {
            layoutManager = LinearLayoutManager(this@SearchUserActivity)
            adapter = SearchResultUserAdapter(mutableListOf(), this@SearchUserActivity, userViewModel)
        }
        search_bar.setOnSearchActionListener(object: MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {}
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence?) {
                text?.let {
                    it.isNotEmpty().let { b ->
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
