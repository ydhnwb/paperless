package com.ydhnwb.paperlessapp.utilities

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import com.google.zxing.common.BitMatrix
import java.text.NumberFormat
import java.util.*


class PaperlessUtil {
    companion object {
        private const val WHITE = -0x1
        private const val BLACK = -0x1000000

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

        fun createBitmap(matrix: BitMatrix): Bitmap? {
            val width = matrix.width
            val height = matrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (matrix[x, y]) BLACK else WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        }

    }
}