package com.ydhnwb.paperlessapp.ui.reset_password

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showInfoAlert
import com.ydhnwb.paperlessapp.utilities.extensions.visible

import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.content_reset_password.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPasswordActivity : AppCompatActivity() {
    private val resetPasswordViewModel: ResetPasswordViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        observe()
        updatePassword()
    }

    private fun observe(){ observeState() }

    private fun observeState() = resetPasswordViewModel.getState().observer(this, Observer { handleState(it) })
    private fun handleState(state: ResetPasswordState){
        when(state){
            is ResetPasswordState.IsLoading -> isLoading(state.state)
            is ResetPasswordState.Alert -> showInfoAlert(state.message)
            is ResetPasswordState.Success -> {
                success(state.email)
            }
        }
    }

    private fun success(email: String){
        AlertDialog.Builder(this).apply {
            setMessage("Kami telah mengirimkan link reset password ke $email. Mohon ikuti instruksi yang ada.")
            setPositiveButton(resources.getString(R.string.info_understand)){d , _ ->
                d.dismiss()
                finish()
            }
        }.show()
    }

    private fun updatePassword(){
        btn_reset.setOnClickListener {
            val email = et_email.text.toString().trim()
            if(validateEmail(email)){
                resetPasswordViewModel.resetPassword(email)
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        if(!PaperlessUtil.isValidEmail(email)){
            setErrorEmail(resources.getString(R.string.email_not_valid))
            return false
        }
        return true
    }

    private fun setErrorEmail(err: String?){
        in_email.error = err
    }

    private fun isLoading(b: Boolean){
        btn_reset.isEnabled = !b
        if(b) loading.visible() else loading.gone()
    }
}
