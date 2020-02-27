package com.ydhnwb.paperlessapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.viewmodels.UserState
import com.ydhnwb.paperlessapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(){
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.getUIState().observe(this, Observer {
            handleState(it)
        })
        btn_register.setOnClickListener {
            val name = et_name.text.toString().trim()
            val email = et_email.text.toString().trim()
            val pass = et_password.text.toString().trim()
            val pass_c = et_password_conf.text.toString().trim()
            if(userViewModel.validate(name, email, pass, pass_c)){
                userViewModel.register(name, email, pass)
            }
        }
    }

    private fun handleState(it : UserState){
        when(it){
            is UserState.Failed -> {
                isLoading(false)
                toast(it.message)
            }
            is UserState.Validate -> {
                it.name?.let {
                    errorName(it)
                }
                it.email?.let {
                    errorEmail(it)
                }
                it.password?.let {
                    errorPassword(it)
                }
                it.confirmPassword?.let {
                    errorPasswordConfirm(it)
                }
            }
            is UserState.ShowToast -> toast(it.message)
            is UserState.Reset -> {
                errorName(null)
                errorEmail(null)
                errorPassword(null)
                errorPasswordConfirm(null)
            }
            is UserState.IsLoading -> {
                isLoading(it.state)
            }
            is UserState.Success -> {
                isLoading(false)
                success(it.token)
            }
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private fun success(email : String) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme)).apply {
            setMessage("Kami telah mengirim email ke $email. Pastikan anda telah memverifikasi email sebelum login")
            setPositiveButton("Mengerti"){ d, _ ->
                d.cancel()
                finish()
            }.create().show()
        }
    }

    private fun errorName(err: String?) { in_name.error = err }

    private fun errorEmail(err: String?) { in_email.error = err }

    private fun errorPassword(err: String?) { in_password.error = err }

    private fun errorPasswordConfirm(err: String?) { in_password_confirm.error = err }

    private fun isLoading(state: Boolean) {
        if(state){
            btn_register.isEnabled = false
            loading_bar.isIndeterminate = true
        }else{
            btn_register.isEnabled = true
            loading_bar.isIndeterminate = false
            loading_bar.progress = 0
        }
    }
}
