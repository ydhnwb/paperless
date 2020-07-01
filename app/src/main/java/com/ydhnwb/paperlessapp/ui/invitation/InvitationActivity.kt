package com.ydhnwb.paperlessapp.ui.invitation

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.shared_adapter.InvitationAdapter
import com.ydhnwb.paperlessapp.models.Invitation
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.activity_invitation.*
import kotlinx.android.synthetic.main.content_invitation.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InvitationActivity : AppCompatActivity() {
    private val invitationViewModel : InvitationViewModel by viewModel()
    private var invitations = mutableListOf<Invitation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invitation)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        setupUI()
        if (getPassedStore() != null){
            invitation_spinner.visibility = View.VISIBLE
            supportActionBar?.setDisplayShowTitleEnabled(false)
            setupSpinner()
        }else{
            invitation_spinner.visibility = View.GONE
        }
        invitationViewModel.listenToUIState().observer(this, Observer { handleUIState(it) })
        fetchInvitation()
    }

    private fun setupUI(){
        rv_invitation.apply {
            layoutManager = LinearLayoutManager(this@InvitationActivity )
            adapter = InvitationAdapter(mutableListOf(), this@InvitationActivity, getPassedStore() == null, invitationViewModel)
        }
    }
    private fun getPassedStore() = intent.getParcelableExtra<Store?>("store")
    private fun setupSpinner(){
        if(getPassedStore() != null){
            val menus = listOf(resources.getString(R.string.invitation_all), resources.getString(R.string.invitation_waiting), resources.getString(R.string.invitation_accepted), resources.getString(R.string.invitation_rejected))
            val spinnerAdapter = ArrayAdapter(supportActionBar?.themedContext!!, android.R.layout.simple_spinner_dropdown_item, menus)
            invitation_spinner.adapter = spinnerAdapter
            invitation_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when(position){
                        0 -> updateListInvitationSent(invitations.reversed())
                        1 -> updateListInvitationSent(invitations.reversed().filter { x -> x.status == null })
                        2 -> updateListInvitationSent(invitations.reversed().filter { x -> x.status?.let { true } ?: kotlin.run { false } })
                        3 -> updateListInvitationSent(invitations.reversed().filter { x -> x.status != null && !x.status!! })
                        else -> updateListInvitationSent(invitations.reversed())
                    }
                }
            }
        }
    }

    private fun fetchInvitation(){
        if(getPassedStore() == null){
            invitationViewModel.listenToInvitationIn().observe(this, Observer {
                rv_invitation.adapter?.let { a ->
                    if(a is InvitationAdapter){
                        a.updateList(it.reversed().filter { x ->
                            x.status == null
                        })
                    }
                    if(it.isNullOrEmpty()){
                        empty_view.visibility = View.VISIBLE
                    }else{
                        empty_view.visibility = View.GONE
                    }
                }
            })
            if(invitationViewModel.listenToInvitationIn().value == null || invitationViewModel.listenToInvitationIn().value!!.isEmpty()){
                empty_view.visibility = View.VISIBLE
            }else{
                empty_view.visibility = View.GONE
            }
            PaperlessUtil.getToken(this)?.let { invitationViewModel.fetchInvitationIn(it) }
        }else{
            invitationViewModel.listenToInvitationSent().observe(this, Observer {
                updateListInvitationSent(it.reversed())
                invitations.clear()
                invitations.addAll(it)
                if(it.isNullOrEmpty()){ empty_view.visibility = View.VISIBLE }else{ empty_view.visibility = View.GONE }
            })
            if(invitationViewModel.listenToInvitationSent().value == null || invitationViewModel.listenToInvitationSent().value!!.isEmpty()){
                empty_view.visibility = View.VISIBLE
            }else{
                empty_view.visibility = View.GONE
            }
            PaperlessUtil.getToken(this)?.let { invitationViewModel.fetchInvitationSent(it, getPassedStore()?.id!!) }
        }
    }
    private fun handleUIState(it : InvitationState){
        when(it){
            is InvitationState.IsLoading -> {
                empty_view.visibility = View.GONE
                getPassedStore()?.let { _ -> invitation_spinner.isEnabled = !it.state }
                if(it.state){
                    loading.visibility = View.VISIBLE
                }else{
                    loading.visibility = View.GONE
                }
            }
            is InvitationState.ShowAlert -> showAlert(it.message)
            is InvitationState.ShowToast -> showToast(it.message)
            is InvitationState.Success -> PaperlessUtil.getToken(this)?.let { it1 ->
                invitationViewModel.fetchInvitationIn(
                    it1
                )
            }
        }
    }

    private fun updateListInvitationSent(it : List<Invitation>){
        rv_invitation.adapter?.let { a ->
            if (a is InvitationAdapter){
                a.updateList(it)
            }
        }
    }

    private fun showAlert(message : String){
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme)).apply{
            setMessage(message)
            setPositiveButton(resources.getString(R.string.info_understand)){d , _ -> d.dismiss()}
        }.show()
    }

    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
