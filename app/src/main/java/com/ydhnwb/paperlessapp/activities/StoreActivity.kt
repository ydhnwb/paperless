package com.ydhnwb.paperlessapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.ydhnwb.paperlessapp.R
import kotlinx.android.synthetic.main.activity_store.*
import java.io.File

class StoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        fab.setOnClickListener { view ->
            Pix.start(this@StoreActivity, Options.init().setRequestCode(100));
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 100 && data != null){
            val path = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            store_logo.load(File(path[0]))
        }
    }
}
