package com.ydhnwb.paperlessapp.ui.employee_detail

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.activity_employee_detail.*
import kotlinx.android.synthetic.main.content_employee_detail.*
import kotlinx.android.synthetic.main.content_employee_detail.employee_image
import kotlinx.android.synthetic.main.list_item_employee.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeDetailActivity : AppCompatActivity() {
    private val employeeDetailViewModel : EmployeeDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_detail)
        setSupportActionBar(toolbar)
        setupToolbar()
        observeState()
        fill()
        saveChanges()
    }

    private fun isLoading(b: Boolean) = if(b) loading.visible() else loading.gone()
    private fun observeState() = employeeDetailViewModel.getState().observer(this, Observer { handleState(it) })
    private fun handleState(state: EmployeeDetailState){
        when(state){
            is EmployeeDetailState.Loading -> isLoading(state.isLoading)
            is EmployeeDetailState.Success -> {
                showToast("Sukses update")
                finish()
            }
            is EmployeeDetailState.ShowToast -> showToast(state.message)
        }
    }


    private fun saveChanges(){
        fab.setOnClickListener {
            val storeId = getPassedStoreId()!!
            val selectedRole = employee_role_spinnner.selectedItemPosition
            val userId = getPassedEmployee()!!.user!!.id
            PaperlessUtil.getToken(this@EmployeeDetailActivity)?.let { token ->
                employeeDetailViewModel.updateRoleEmployee(token, storeId, role = selectedRole, userId = userId!!)
            }
        }
    }

    private fun setupToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun fill(){
        getPassedEmployee()?.let {
            employee_image.load(it.user?.image)
            employee_name_textview.text = it.user?.name
            employee_joined_textview.text = it.joined
            it.role?.let { r ->
                employee_role_spinnner.setSelection(r)
            }
        }
    }

    private fun getPassedEmployee(): Employee?{
        return intent.getParcelableExtra("employee")
    }
    private fun getPassedStoreId() : String? = intent.getStringExtra("store_id")
}