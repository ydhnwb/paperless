package com.ydhnwb.paperlessapp.utilities

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.ydhnwb.paperlessapp.webservices.ApiClient
import java.text.NumberFormat
import java.util.*

class Constants {
    companion object {

    }
}

class PaperlessUtil {
    companion object {
        fun getToken(c : Context) : String {
            val s = c.getSharedPreferences("USER", MODE_PRIVATE)
            val token = s?.getString("TOKEN", "UNDEFINED")
            return token!!
        }

        fun setToken(context: Context, token : String){
            val pref = context.getSharedPreferences("USER", MODE_PRIVATE)
            pref.edit().apply {
                putString("TOKEN", token)
                apply()
            }
        }

        fun clearToken(context: Context){
            val pref = context.getSharedPreferences("USER", MODE_PRIVATE)
            pref.edit().clear().apply()
        }

        fun isFirstTime(context: Context) : Boolean {
            val pref = context.getSharedPreferences("UTILS", MODE_PRIVATE)
            val bool = pref.getBoolean("FIRST_TIME", true)
            return bool
        }

        fun setFirstTime(context: Context, value : Boolean){
            val pref = context.getSharedPreferences("UTILS", MODE_PRIVATE)
            pref.edit().apply{
                putBoolean("FIRST_TIME", value)
                apply()
            }
        }

        fun setToIDR(num : Int) : String {
            val localeID = Locale("in", "ID")
            val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
            return formatRupiah.format(num)
        }

        fun isValidEmail(email : String) : Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        fun isValidPassword(password : String) = password.length >= 8

    }
}