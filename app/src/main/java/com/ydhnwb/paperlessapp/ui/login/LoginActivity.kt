package com.ydhnwb.paperlessapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.main.MainActivity
import com.ydhnwb.paperlessapp.ui.register.RegisterActivity
import com.ydhnwb.paperlessapp.ui.reset_password.ResetPasswordActivity
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        loginViewModel.listenToUIState().observer(this, Observer { handleState(it) })
        login()
        btn_register.setOnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }
        btn_forgot_password.setOnClickListener { startActivity(Intent(this, ResetPasswordActivity::class.java)) }
    }

    private fun handleState(it : LoginState){
        when(it){
            is LoginState.Validate -> {
                it.email?.let {
                    emailError(it)
                }
                it.password?.let {
                    passwordError(it)
                }
            }
            is LoginState.ShowToast -> toast(it.message)
            is LoginState.Reset -> {
                emailError(null)
                passwordError(null)
            }
            is LoginState.IsLoading -> {
                if(it.state){
                    showLoading()
                }else{
                    hideLoading()
                }
            }
            is LoginState.Success -> {
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
            val password = et_password.text.toString().trim()
            if (loginViewModel.validate(email, password)){
                loginViewModel.login(email, password)
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
}