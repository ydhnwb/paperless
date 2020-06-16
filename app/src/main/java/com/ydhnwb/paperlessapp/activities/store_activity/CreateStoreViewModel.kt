package com.ydhnwb.paperlessapp.activities.store_activity

import androidx.lifecycle.ViewModel
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.repositories.StoreRepository
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.SingleLiveEvent

class CreateStoreViewModel (private val storeRepository: StoreRepository) : ViewModel(){
    private val state : SingleLiveEvent<CreateStoreState> = SingleLiveEvent()

    private fun setLoading(){ state.value = CreateStoreState.IsLoading(true) }
    private fun hideLoading(){ state.value = CreateStoreState.IsLoading(false) }
    private fun toast(message: String){ state.value = CreateStoreState.ShowToast(message) }
    private fun success(isCreate: Boolean){ state.value = CreateStoreState.Success(isCreate) }
    private fun successDelete(){ state.value = CreateStoreState.SuccessDeleted }


    fun validate(store : Store, isUpdate : Boolean) : Boolean {
        state.value = CreateStoreState.Reset
        if(store.store_logo.isNullOrEmpty()) {
            if(!isUpdate){
                state.value = CreateStoreState.Validate(store_logo = "Belum ada gambar toko yang dipilih. Anda wajib memberikan satu gambar untuk toko")
                return false
            }
        }
        if(store.name.isNullOrEmpty()){
            state.value = CreateStoreState.Validate(store_name = "Nama toko tidak boleh kosong")
            return false
        }else if(store.description.isNullOrEmpty()){
            state.value = CreateStoreState.Validate(store_desc = "Deskripsi toko tidak boleh kosong")
            return false
        }else if(store.phone.isNullOrEmpty() || store.phone.toString().length < 13 || !store.phone.toString().startsWith("+62")){
            state.value = CreateStoreState.Validate(store_phone = "Nomor telepon tidak valid")
            return false
        }else if(!PaperlessUtil.isValidEmail(store.email.toString())){
            state.value = CreateStoreState.Validate(store_email = "Email tidak valid")
            return false
        }else if(store.address.isNullOrEmpty()){
            state.value = CreateStoreState.Validate(store_address = "Alamat toko tidak boleh kosong")
            return false
        }
        return true
    }

    fun createStore(token : String, store: Store){
        setLoading()
        storeRepository.storeCreate(token, store){ resultBool, e ->
            hideLoading()
            e?.let { it.message?.let { x -> toast(x) } }
            if(resultBool){
                success(true)
            }
        }
    }

    fun updateStore(token: String, store: Store){
        setLoading()
        if(store.store_logo != null){
            storeRepository.storeUpdateWithImage(token, store){ resultBool, e ->
                hideLoading()
                e?.let { it.message?.let { m -> toast(m) } }
                if(resultBool){
                    success(false)
                }
            }
        }else{
            storeRepository.storeUpdateWithoutImage(token, store){ resultBool, e ->
                hideLoading()
                e?.let { it.message?.let { m -> toast(m) } }
                if(resultBool){
                    success(false)
                }
            }
        }
    }

    fun deleteStore(token: String, storeId: String){
        setLoading()
        storeRepository.deleteStore(token, storeId){ resultBool, e ->
            hideLoading()
            e?.let { it.message?.let { m -> toast(m) } }
            if(resultBool){ successDelete() }
        }
    }

    fun listenToUIState() = state
}

sealed class CreateStoreState{
    object SuccessDeleted : CreateStoreState()
    data class Success(var isCreate: Boolean) : CreateStoreState()
    data class ShowToast(var message : String) : CreateStoreState()
    data class IsLoading(var state : Boolean) : CreateStoreState()
    data class Validate(var store_name : String? = null, var store_desc : String? = null,
                        var store_phone : String? = null, var store_address : String? = null,
                        var store_email : String? = null, var store_logo : String? = null) : CreateStoreState()
    object Reset : CreateStoreState()

}

