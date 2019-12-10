package com.ydhnwb.paperlessapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.contracts.activities.LoginActivityContract
import com.ydhnwb.paperlessapp.presenters.activities.LoginActivityPresenter
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.UserState
import com.ydhnwb.paperlessapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var userViewModel : UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        userViewModel.getUIState().observe(this, Observer {
            handleState(it)
        })
        login()
        btn_register.setOnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }
    }

    private fun handleState(it : UserState){
        when(it){
            is UserState.Failed -> {
                hideLoading()
                toast(it.message)
            }
            is UserState.Validate -> {
                it.email?.let {
                    emailError(it)
                }
                it.password?.let {
                    passwordError(it)
                }
            }
            is UserState.ShowToast -> toast(it.message)
            is UserState.Reset -> {
                emailError(null)
                passwordError(null)
            }
            is UserState.Popup -> popup(it.message)
            is UserState.IsLoading -> {
                if(it.state){
                    showLoading()
                }else{
                    hideLoading()
                }
            }
            is UserState.Error -> {
                toast(it.err.toString())
                hideLoading()
            }
            is UserState.Success -> {
                hideLoading()
                success(it.token)
            }
        }
    }

    private fun emailError(message: String?) { input_email.error = message }

    private fun passwordError(message: String?) { input_password.error = message }

    private fun showLoading() {
        btn_login.isEnabled = false
        btn_register.isEnabled = false
        loading_bar.isIndeterminate = true
    }

    private fun hideLoading() {
        btn_login.isEnabled = true
        btn_register.isEnabled = true
        loading_bar.isIndeterminate = false
        loading_bar.progress = 0
    }

    private fun success(token : String) {
        toast("Selamat datang kembali")
        PaperlessUtil.setToken(this, token)
        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }).also { finish() }
    }


    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private fun login(){
        btn_login.setOnClickListener {
            val email = et_email.text.toString().trim()
            val passw = et_password.text.toString().trim()
            if (userViewModel.validate(null, email, passw)){
                userViewModel.login(email, passw)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if(!PaperlessUtil.getToken(this@LoginActivity).equals("UNDEFINED")){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }.also { finish() })
        }
    }

    private fun popup(message: String) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme)).apply {
            setMessage(message)
            setPositiveButton("Mengerti"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
        }
    }
}