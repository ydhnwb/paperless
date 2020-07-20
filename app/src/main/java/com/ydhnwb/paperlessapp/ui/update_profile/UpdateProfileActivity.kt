package com.ydhnwb.paperlessapp.ui.update_profile

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.content_update_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.io.File


class UpdateProfileActivity : AppCompatActivity() {
    private val updateProfileViewModel: UpdateProfileViewModel by viewModel()
    private lateinit var easyImage : EasyImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        setSupportActionBar(toolbar)
        setupToolbar()
        setupImagePicker()
        saveChanges()
        observe()
        fetchUser()
        setupEditTextNameFilter()
    }

    private fun setupEditTextNameFilter(){
        et_name.filters = arrayOf(object : InputFilter {
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
                source?.let { s ->
                    if(s == ""){
                        return s
                    }
                    if(s.toString().matches("[a-zA-Z\\s]+".toRegex())){
                        return s
                    }
                    return ""
                }
                return ""
            }
        })
    }

    private fun observe(){
        observeState()
        observeImagePath()
        observeUser()
    }

    private fun fetchUser() = PaperlessUtil.getToken(this)?.let {
        updateProfileViewModel.fetchProfile(it)
    }
    private fun observeImagePath() = updateProfileViewModel.getImagePath().observe(this, Observer { handleImagePath(it) })
    private fun observeUser() = updateProfileViewModel.getUser().observe(this, Observer { handleUser(it) })
    private fun observeState() = updateProfileViewModel.getState().observer(this, Observer { handleState(it) })
    private fun handleState(it: UpdateProfileState){
        when(it){
            is UpdateProfileState.ShowToast -> showToast(it.message)
            is UpdateProfileState.Loading -> isLoading(it.isLoading)
            is UpdateProfileState.Success -> finish()
        }
    }

    private fun handleUser(user: User){
        et_name.setText(user.name)
        et_phone.setText(user.phone)
        et_email.setText(user.email)
        user.image?.let { user_photo.load(it) } ?: user_photo.load(R.drawable.image_placeholder)
    }

    private fun handleImagePath(path: String?){
        path?.let {
            user_photo.load(File(it))
        }
    }

    private fun isLoading(b: Boolean){
        btn_update.isEnabled = !b
    }

    private fun setupImagePicker(){
        easyImage = EasyImage.Builder(this@UpdateProfileActivity)
            .setCopyImagesToPublicGalleryFolder(false)
            .setFolderName("Pilih gambar")
            .allowMultiple(false)
            .build()
        pickAnImage()
    }

    private fun pickAnImage(){
        user_photo.setOnClickListener {
            easyImage.openGallery(this)
        }
    }

    private fun saveChanges(){
        btn_update.setOnClickListener {
            val name = et_name.text.toString().trim()
            val phone = et_phone.text.toString().trim()
            if(validate(name, phone)){
                PaperlessUtil.getToken(this@UpdateProfileActivity)?.let { token ->
                    val u = User()
                    u.name = name
                    u.phone = phone
                    updateProfileViewModel.updateProfile(token, u)
                }
            }
        }
    }

    private fun setPhoneError(err: String?){ in_phone.error = err }
    private fun setNameError(err: String?){ in_name.error = err }
    private fun reset(){
        setPhoneError(null)
        setNameError(null)
    }

    private fun validate(name: String, phone: String) : Boolean{
        reset()
        if(name.isEmpty()){
            setNameError("Nama tidak boleh kosong")
            return false
        }
        if(phone.isNotEmpty()){
            if(phone.length < 13 || !phone.startsWith("+628")){
                setPhoneError("Nomor telepon tidak valid. Gunakan format +628")
                return false
            }
        }

        return true
    }

    private fun setupToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }


    private fun onPhotosReturned(images : Array<MediaFile>){
        val imagePath = images[0].file.absolutePath
        updateProfileViewModel.setImagePath(imagePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                onPhotosReturned(imageFiles)
            }

            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                error.printStackTrace()
            }
        })
    }
}