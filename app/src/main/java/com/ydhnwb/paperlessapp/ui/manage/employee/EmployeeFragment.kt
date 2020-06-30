package com.ydhnwb.paperlessapp.ui.manage.employee

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.manage.ManageStoreViewModel
import com.ydhnwb.paperlessapp.ui.search_user.SearchUserActivity
import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import kotlinx.android.synthetic.main.fragment_employee.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeFragment : Fragment(R.layout.fragment_employee) {
    private val employeeViewModel: EmployeeViewModel by viewModel()
    private val parentStoreViewModel : ManageStoreViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observe()
        initEmptyView()
        addEmployee()
    }

    private fun observe(){
        observeState()
        observeEmployees()
    }

    private fun observeState() = employeeViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
    private fun observeEmployees() = employeeViewModel.listenToEmployees().observe(viewLifecycleOwner, Observer {handleEmployee(it) })

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
            is EmployeeState.ShowToast -> requireActivity().showToast(it.message)
            is EmployeeState.SuccessDelete -> {
                employeeViewModel.fetchEmployees(PaperlessUtil.getToken(activity!!), parentStoreViewModel.listenToCurrentStore().value?.id.toString())
                requireActivity().showToast(resources.getString(R.string.info_success_delete_employee))
            }
        }
    }

    private fun setupUI(){
        view!!.rv_employee.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = EmployeeAdapter(
                mutableListOf(),
                activity!!,
                employeeViewModel,
                parentStoreViewModel.listenToCurrentStore().value?.id.toString()
            )
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
            val store : Store = parentStoreViewModel.listenToCurrentStore().value!!
            startActivity(Intent(activity, SearchUserActivity::class.java).apply {
                putExtra("store", store)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        employeeViewModel.fetchEmployees(PaperlessUtil.getToken(activity!!), parentStoreViewModel.listenToCurrentStore().value?.id.toString())
    }
}