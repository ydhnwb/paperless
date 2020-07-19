package com.ydhnwb.paperlessapp.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.repositories.FirebaseRepository
import kotlinx.android.synthetic.main.activity_register.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity(){
    private val registerViewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        setupEditTextNameFilter()
        setupButtonRegister()
        observeState()
    }

    private fun observeState() = registerViewModel.listenToUIState().observer(this, Observer { handleState(it) })

    private fun setupButtonRegister(){
        btn_register.setOnClickListener {
            val name = et_name.text.toString().trim()
            val email = et_email.text.toString().trim()
            val pass = et_password.text.toString().trim()
            val confirmPassword = et_password_conf.text.toString().trim()
            if(registerViewModel.validate(name, email, pass, confirmPassword)){ registerViewModel.register(name, email, pass) }
        }
    }

    private fun setupEditTextNameFilter(){
        et_name.filters = arrayOf(object : InputFilter{
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
                source?.let { s ->
                    if(s == ""){
                        return s
                    }
                    if(s.toString().matches("[a-zA-Z]+".toRegex())){
                        return s
                    }
                    return ""
                }
                return ""
            }
        })
    }

    private fun handleState(it : RegisterState){
        when(it){
            is RegisterState.Validate -> {
                it.name?.let { errorName(it) }
                it.email?.let { errorEmail(it) }
                it.password?.let { errorPassword(it) }
                it.confirmPassword?.let { errorPasswordConfirm(it) }
            }
            is RegisterState.ShowToast -> toast(it.message)
            is RegisterState.Reset -> {
                errorName(null)
                errorEmail(null)
                errorPassword(null)
                errorPasswordConfirm(null)
            }
            is RegisterState.IsLoading -> {
                isLoading(it.state)
            }
            is RegisterState.Success -> {
                isLoading(false)
                showAlert("Kami telah mengirim email ke ${it.message}. Pastikan anda telah melakukan verifikasi email sebelum login")
            }
            is RegisterState.Failed -> {
                it.message?.let { showAlert(it) } ?: showAlert(resources.getString(R.string.failed_register))
            }
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private fun showAlert(param : String) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme)).apply {
            setMessage(param)
            setPositiveButton(resources.getString(R.string.info_understand)){ d, _ ->
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
        btn_register.isEnabled = !state
        loading_bar.isIndeterminate = state
        if(!state){ loading_bar.progress = 0 }
    }
}
