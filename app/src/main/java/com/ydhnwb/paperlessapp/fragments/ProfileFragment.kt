package com.ydhnwb.paperlessapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.LoginActivity
import com.ydhnwb.paperlessapp.activities.RegisterActivity
import com.ydhnwb.paperlessapp.adapters.PreferenceAdapter
import com.ydhnwb.paperlessapp.models.Preference
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.UserState
import com.ydhnwb.paperlessapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_not_logged_in.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {
    private var isNotLoggedIn : Boolean = false
    private var prefs = mutableListOf<Preference>().apply {
        add(Preference(1, R.string.pref_profile, R.drawable.ic_person_black_24dp))
        add(Preference(2, R.string.pref_payment, R.drawable.ic_payment_black_24dp))
        add(Preference(3, R.string.pref_privacy, R.drawable.ic_security_black_24dp))
        add(Preference(4, R.string.pref_store, R.drawable.ic_dashboard_black_24dp))
    }
    private lateinit var userViewModel : UserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isNotLoggedIn = PaperlessUtil.getToken(activity!!).equals("UNDEFINED")
        return if(isNotLoggedIn){
            inflater.inflate(R.layout.fragment_not_logged_in, container, false)
        }else{
            inflater.inflate(R.layout.fragment_profile, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isNotLoggedIn){
            view.btn_register.setOnClickListener { startActivity(Intent(activity, RegisterActivity::class.java)) }
            view.btn_login.setOnClickListener { startActivity(Intent(activity, LoginActivity::class.java)) }
        }else{
            userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
            userViewModel.listenToCurrentUser().observe(viewLifecycleOwner, Observer {
                it?.let {
                    view.profile_image.load(R.drawable.ydhnwb){
                        transformations(CircleCropTransformation())
                    }
                    view.profile_name.text = it.name
                    view.profile_email.text = it.email
                }
            })
            userViewModel.getUIState().observer(viewLifecycleOwner, Observer {
                when(it){
                    is UserState.ShowToast -> Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                    is UserState.IsLoading -> {
                        if(it.state){
                            view.loading.apply {
                                visibility = View.VISIBLE
                                isIndeterminate = true
                            }
                        }else{
                            view.loading.apply {
                                visibility = View.GONE
                                isIndeterminate = false
                            }
                        }
                    }
                }
            })
            userViewModel.profile(PaperlessUtil.getToken(activity!!))
            view.rv_pref.apply {
                layoutManager = LinearLayoutManager(activity).apply { orientation = LinearLayoutManager.HORIZONTAL }
                adapter = PreferenceAdapter(activity!!, prefs)
            }
        }
    }
}