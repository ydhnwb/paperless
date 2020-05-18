package com.ydhnwb.paperlessapp.fragments.manage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.activities.SearchUserActivity
import com.ydhnwb.paperlessapp.adapters.EmployeeAdapter
import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.EmployeeState
import com.ydhnwb.paperlessapp.viewmodels.EmployeeViewModel
import com.ydhnwb.paperlessapp.viewmodels.StoreViewModel
import kotlinx.android.synthetic.main.fragment_employee.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeFragment : Fragment(R.layout.fragment_employee) {
    private val employeeViewModel: EmployeeViewModel by viewModel()
    private val parentStoreViewModel : StoreViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        employeeViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
        employeeViewModel.listenToEmployees().observe(viewLifecycleOwner, Observer {handleEmployee(it) })
        initEmptyView()
        addEmployee()

    }

    private fun initEmptyView(){
        if(employeeViewModel.listenToEmployees().value == null || employeeViewModel.listenToEmployees().value!!.isEmpty()){
            view!!.empty_view.visibility = View.VISIBLE
        }else{
            view!!.empty_view.visibility = View.GONE
        }
    }

    private fun handleUIState(it: EmployeeState){
        when(it){
            is EmployeeState.IsLoading -> { if(it.state){ view!!.loading.visibility = View.VISIBLE }else{ view!!.loading.visibility = View.GONE } }
            is EmployeeState.ShowToast -> toast(it.message)
        }
    }

    private fun setupUI(){
        view!!.rv_employee.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = EmployeeAdapter(mutableListOf(), activity!!)
        }
    }

    private fun handleEmployee(it : List<Employee>){
        if(it.isNullOrEmpty()){ view!!.empty_view.visibility = View.VISIBLE }else{ view!!.empty_view.visibility = View.GONE }
        view!!.rv_employee.adapter?.let { adapter ->
            if(adapter is EmployeeAdapter){
                adapter.updateList(it)
            }
        }
    }

    private fun addEmployee(){
        view!!.fab.setOnClickListener {
            val store : Store = parentStoreViewModel.getCurrentStore()!!
            startActivity(Intent(activity, SearchUserActivity::class.java).apply {
                putExtra("store", store)
            })
        }
    }

    private fun toast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

    override fun onResume() {
        super.onResume()
        employeeViewModel.fetchEmployees(PaperlessUtil.getToken(activity!!), parentStoreViewModel.getCurrentStore()?.id.toString())
    }
}