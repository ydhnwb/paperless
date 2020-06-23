package com.ydhnwb.paperlessapp.activities.user_history

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.OrderHistory
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.activity_user_history.*
import kotlinx.android.synthetic.main.content_user_history.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserHistoryActivity : AppCompatActivity() {
    private val userHistoryViewModel : UserHistoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_history)
        setSupportActionBar(toolbar)
        setupToolbar()
        setupRecycler()
        observe()
    }

    private fun observe(){
        observeState()
        observeHistory()
    }

    private fun observeState() = userHistoryViewModel.listenToState().observer(this, Observer { handleState(it) })
    private fun observeHistory() = userHistoryViewModel.listenToHistories().observe(this, Observer { handleHistoryOrder(it) })

    private fun handleHistoryOrder(it: List<OrderHistory>){
        userHistory_recyclerView.adapter?.let { adapter ->
            if(adapter is UserHistoryAdapter){
                adapter.updateList(it)
            }
        }
    }

    private fun handleState(it: UserHistoryState){
        when(it){
            is UserHistoryState.Loading -> isLoading(it.state)
            is UserHistoryState.ShowToast -> showToast(it.message)
        }
    }

    private fun isLoading(b : Boolean) = if(b) userHistory_progressBar.visible() else userHistory_progressBar.gone()

    private fun setupToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecycler(){
        userHistory_recyclerView.apply {
            layoutManager = LinearLayoutManager(this@UserHistoryActivity)
            adapter = UserHistoryAdapter(mutableListOf(), this@UserHistoryActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        userHistoryViewModel.fetchHistory(PaperlessUtil.getToken(this))
    }
}