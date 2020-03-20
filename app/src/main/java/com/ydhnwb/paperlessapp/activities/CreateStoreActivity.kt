package com.ydhnwb.paperlessapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.fxn.pix.Pix
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.StoreState
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import kotlinx.android.synthetic.main.activity_create_store.*
import kotlinx.android.synthetic.main.content_create_store.*
import java.io.File

class CreateStoreActivity : AppCompatActivity() {
    companion object { private const val IMAGE_REQUEST_CODE = 167 }
    private lateinit var storeViewModel : StoreViewModel
    private var store = Store()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_store)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        storeViewModel = ViewModelProvider(this).get(StoreViewModel::class.java)
        storeViewModel.listenUIState().observe(this, Observer {
            handleUIState(it)
        })
        choosePhoto()
        saveChanges()
        fillForm()
    }

    private fun saveChanges(){
        btn_create_store.setOnClickListener {
            store.apply {
                name = et_store_name.text.toString().trim()
                description = et_store_desc.text.toString().trim()
                phone = et_store_phone.text.toString().trim()
                email = et_store_email.text.toString().trim()
                address = et_store_address.text.toString().trim()
            }
            var isUpdate = false
            if(getPassedStore() != null){
                isUpdate = true
//                println("New store logo is same? ${store.store_logo == getPassedStore()!!.store_logo}")
                if (store.store_logo.equals(getPassedStore()!!.store_logo)){
                    store.store_logo = null
                    println("store logo is null")
                }else{
                }
            }
            if(storeViewModel.validate(store, isUpdate)){
                if(getPassedStore() != null){
                    storeViewModel.storeUpdate(PaperlessUtil.getToken(this@CreateStoreActivity), store)
                }else{
                    storeViewModel.storeCreate(PaperlessUtil.getToken(this@CreateStoreActivity), store)
                }
            }
        }
    }

    private fun choosePhoto(){ btn_add_image.setOnClickListener { Pix.start(this@CreateStoreActivity, IMAGE_REQUEST_CODE) } }

    private fun handleUIState(it : StoreState){
        when(it){
            is StoreState.ShowToast -> toast(it.message)
            is StoreState.IsLoading -> {
                if(it.isLoading){
                    btn_create_store.isEnabled = false
                    btn_add_image.isEnabled = false
                    loading.visibility = View.VISIBLE
                }else{
                    loading.visibility = View.GONE
                    btn_create_store.isEnabled = true
                    btn_add_image.isEnabled = true
                }
            }
            is StoreState.Reset -> {
                setErrorName(null)
                setErrorDescription(null)
                setErrorPhone(null)
                setErrorEmail(null)
                setErrorAddress(null)
            }
            is StoreState.Success -> {
                toast(resources.getString(R.string.info_store_created))
                finish()
            }
            is StoreState.Validate -> {
                it.store_logo?.let { e -> toast(e) }
                it.store_name?.let { e -> setErrorName(e) }
                it.store_desc?.let { e -> setErrorDescription(e) }
                it.store_phone?.let { e -> setErrorPhone(e) }
                it.store_email?.let { e -> setErrorEmail(e) }
                it.store_address?.let { e -> setErrorAddress(e) }
            }
        }
    }

    private fun setErrorName(err : String?) { in_store_name.error = err }
    private fun setErrorDescription(err : String?) { in_store_desc.error = err }
    private fun setErrorPhone(err : String?) { in_store_phone.error = err }
    private fun setErrorEmail(err : String?) { in_store_email.error = err }
    private fun setErrorAddress(err : String?) { in_store_address.error = err }
    private fun toast(message : String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val selectedImageUri = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            selectedImageUri?.let{
                store.store_logo = it[0]
                store_image.load(File(it[0]))
                if(getPassedStore() != null){
                    println("New store logo is same? ${store.store_logo == getPassedStore()!!.store_logo}")
                    println(getPassedStore()!!.store_logo)
                    println(store.store_logo)
                }
            }
        }
    }

    private fun getPassedStore() : Store? = intent.getParcelableExtra("STORE")
    private fun fillForm(){
        getPassedStore()?.let {
            println(it.store_logo)
            et_store_name.setText(it.name)
            et_store_desc.setText(it.description)
            et_store_phone.setText(it.phone)
            et_store_email.setText(it.email)
            et_store_address.setText(it.address)
            store_image.load(it.store_logo)
            store.apply {
                id = it.id
                name = it.name
                description = it.description
                phone = it.phone
                email = it.email
                address = it.address
                store_logo = it.store_logo
            }
        }
    }

}
