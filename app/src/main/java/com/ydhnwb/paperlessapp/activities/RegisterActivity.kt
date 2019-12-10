package com.ydhnwb.paperlessapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.contracts.activities.RegisterActivityContract
import com.ydhnwb.paperlessapp.presenters.activities.RegisterActivityPresenter
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), RegisterActivityContract.View{
    private var presenter = RegisterActivityPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        btn_register.setOnClickListener {
            val name = et_name.text.toString().trim()
            val email = et_email.text.toString().trim()
            val pass = et_password.text.toString().trim()
            val pass_c = et_password_conf.text.toString().trim()
            if(presenter.validate(name, email, pass, pass_c)){
                presenter.register(name, email, pass)
            }
        }
    }

    override fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    override fun success(email : String) {
        AlertDialog.Builder(this).apply {
            setMessage("Kami telah mengirim email ke $email. Pastikan anda telah memverifikasi email sebelum login")
            setPositiveButton("Mengerti"){ d, _ ->
                d.cancel()
                finish()
            }.create().show()
        }
    }

    override fun errorName(err: String?) { in_name.error = err }

    override fun errorEmail(err: String?) { in_email.error = err }

    override fun errorPassword(err: String?) { in_password.error = err }

    override fun errorPasswordConfirm(err: String?) { in_password_confirm.error = err }

    override fun isLoading(state: Boolean) {
        if(state){
            btn_register.isEnabled = false
            loading_bar.isIndeterminate = true
        }else{
            btn_register.isEnabled = true
            loading_bar.isIndeterminate = false
            loading_bar.progress = 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun failed() {
    }
}
