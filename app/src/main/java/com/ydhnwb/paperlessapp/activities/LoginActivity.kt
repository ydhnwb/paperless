package com.ydhnwb.paperlessapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.contracts.activities.LoginActivityContract
import com.ydhnwb.paperlessapp.presenters.activities.LoginActivityPresenter
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginActivityContract.View {
    private val presenter = LoginActivityPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        login()
        btn_register.setOnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }
    }

    override fun emailError(message: String?) { input_email.error = message }

    override fun passwordError(message: String?) { input_password.error = message }

    override fun showLoading() {
        btn_login.isEnabled = false
        btn_register.isEnabled = false
        loading_bar.isIndeterminate = true
    }

    override fun hideLoading() {
        btn_login.isEnabled = true
        btn_register.isEnabled = true
        loading_bar.isIndeterminate = false
        loading_bar.progress = 0
    }

    override fun success(token : String) {
        toast("Selamat datang kembali")
        PaperlessUtil.setToken(this, "Bearer $token")
        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }).also { finish() }
    }


    override fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private fun login(){
        btn_login.setOnClickListener {
            val email = et_email.text.toString().trim()
            val passw = et_password.text.toString().trim()
            if (presenter.validate(email, passw)){
                presenter.doLogin(email, passw)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
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

    override fun failed(message: String) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme)).apply {
            setMessage("Tidak dapat masuk. Pastikan email terverifikasi dan katasandi anda benar")
            setPositiveButton("Mengerti"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
        }
    }
}
