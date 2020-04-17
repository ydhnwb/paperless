package com.ydhnwb.paperlessapp.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.InvitationAdapter
import com.ydhnwb.paperlessapp.models.Invitation
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.InvitationState
import com.ydhnwb.paperlessapp.viewmodels.InvitationViewModel
import kotlinx.android.synthetic.main.activity_invitation.*
import kotlinx.android.synthetic.main.content_invitation.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InvitationActivity : AppCompatActivity() {
    private val invitationViewModel : InvitationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invitation)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        setupUI()
//        if (getPassedStore() != null){
//            supportActionBar?.setDisplayShowTitleEnabled(false)
//            setupSpinner()
//        }
        invitationViewModel.listenToUIState().observer(this, Observer { handleUIState(it) })
        fetchInvitation()
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun setupUI(){
        rv_invitation.apply {
            layoutManager = LinearLayoutManager(this@InvitationActivity )
            adapter = InvitationAdapter(mutableListOf<Invitation>(), this@InvitationActivity, getPassedStore() == null)
        }
    }
    private fun getPassedStore() = intent.getParcelableExtra<Store?>("store")
    private fun setupSpinner(){
        if(getPassedStore() != null){
            val menus = listOf<String>(
                resources.getString(R.string.invitation_waiting),
                resources.getString(R.string.invitation_rejected)
            )
//            val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, menus)
//            invitation_spinner.adapter = spinnerAdapter
//            invitation_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) {}
//                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
//            }
        }
    }

    private fun fetchInvitation(){
        if(getPassedStore() == null){
            invitationViewModel.listenToInvitationIn().observe(this, Observer {
                rv_invitation.adapter?.let { a ->
                    if(a is InvitationAdapter){
                        a.updateList(it)
                    }
                }
            })
            if(invitationViewModel.listenToInvitationIn().value == null || invitationViewModel.listenToInvitationIn().value!!.isEmpty()){
                empty_view.visibility = View.VISIBLE
            }else{
                empty_view.visibility = View.GONE
            }
            invitationViewModel.invitationIn(PaperlessUtil.getToken(this))
        }else{
            invitationViewModel.listenToInvitationSent().observe(this, Observer {
                rv_invitation.adapter?.let { a ->
                    if(a is InvitationAdapter){
                        a.updateList(it)
                        if(it.isNullOrEmpty()){
                            empty_view.visibility = View.VISIBLE
                        }else{
                            empty_view.visibility = View.GONE
                        }
                    }
                }
            })
            invitationViewModel.invitationSent(PaperlessUtil.getToken(this), getPassedStore()?.id!!)
        }
    }
    private fun handleUIState(it : InvitationState){
        when(it){
            is InvitationState.IsLoading -> {
//                empty_view.visibility = View.GONE
                if(it.state){
                    loading.visibility = View.VISIBLE
                }else{
                    loading.visibility = View.GONE
                }
            }
        }
    }
}
