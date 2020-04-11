package com.ydhnwb.paperlessapp.activities

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.ydhnwb.paperlessapp.R

import kotlinx.android.synthetic.main.activity_search_user.*

class SearchUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)
        setSupportActionBar(toolbar)
    }

}
