package com.ydhnwb.paperlessapp.utilities.extensions

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ydhnwb.paperlessapp.R

fun Context.showInfoAlert(message: String){
    AlertDialog.Builder(this).apply {
        setMessage(message)
        setPositiveButton(resources.getString(R.string.info_understand)){ dialog, _ ->
            dialog.dismiss()
        }
    }.show()
}


fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}