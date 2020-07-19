package com.ydhnwb.paperlessapp.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.login.LoginActivity
import com.ydhnwb.paperlessapp.ui.register.RegisterActivity
import com.ydhnwb.paperlessapp.ui.scanner.ShowQRActivity
import com.ydhnwb.paperlessapp.models.Preference
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import kotlinx.android.synthetic.main.fragment_not_logged_in.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private var isNotLoggedIn : Boolean = false
    private var prefs = mutableListOf<Preference>().apply {
        add(Preference(1, R.string.pref_profile, R.drawable.ic_person_black_24dp))
        add(Preference(2, R.string.pref_invitation, R.drawable.ic_payment_black_24dp))
        add(Preference(3, R.string.pref_purchasement, R.drawable.ic_compare_arrows_black_24dp))
        add(Preference(4, R.string.sign_out, R.drawable.ic_baseline_power_settings_new_24))
    }
    private val profileViewModel : ProfileViewModel by viewModel()

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
            profileViewModel.listenToCurrentUser().observe(viewLifecycleOwner, Observer {
                it?.let {
                    with(view){
                        it.image?.let { imageUrl -> profile_image.load(imageUrl){ transformations(CircleCropTransformation()) } } ?: profile_image.load(R.drawable.image_placeholder){ transformations(CircleCropTransformation()) }
                        profile_name.text = it.name
                        profile_email.text = it.email
                        profile_qr.setOnClickListener { _ ->
                            startActivity(Intent(activity, ShowQRActivity::class.java).apply {
                                putExtra("ID", it.id.toString())
                                putExtra("IS_STORE", false)
                            })
                        }
                    }
                }
            })
            profileViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
            PaperlessUtil.getToken(activity!!)?.let { profileViewModel.fetchProfile(it) }
            view.rv_pref.apply {
                layoutManager = LinearLayoutManager(activity).apply { orientation = LinearLayoutManager.HORIZONTAL }
                adapter = PreferenceAdapter(activity!!, prefs)
            }
        }
    }

    private fun handleUIState(it: ProfileState){
        when(it){
            is ProfileState.ShowToast -> requireContext().showToast(it.message)
            is ProfileState.IsLoading -> isLoading(it.state)
        }
    }

    private fun isLoading(b: Boolean){
        with(requireView()){
            if(b){
                loading.apply {
                    visibility = View.VISIBLE
                    isIndeterminate = true
                }
            }else{
                loading.apply {
                    visibility = View.GONE
                    isIndeterminate = false
                }
            }
        }
    }
}