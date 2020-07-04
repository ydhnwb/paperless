package com.ydhnwb.paperlessapp.ui.manage.employee

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.manage.ManageStoreViewModel
import com.ydhnwb.paperlessapp.ui.search_user.SearchUserActivity
import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.gone
import com.ydhnwb.paperlessapp.utilities.extensions.showInfoAlert
import com.ydhnwb.paperlessapp.utilities.extensions.showToast
import com.ydhnwb.paperlessapp.utilities.extensions.visible
import kotlinx.android.synthetic.main.fragment_employee.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeFragment : Fragment(R.layout.fragment_employee), EmployeeInterface {
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
                fetchEmployees()
                requireActivity().showToast(resources.getString(R.string.info_success_delete_employee))
            }
        }
    }

    private fun setupUI(){
        view!!.rv_employee.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = EmployeeAdapter(mutableListOf(), this@EmployeeFragment)
        }
    }

    private fun handleEmployee(it : List<Employee>){
        if(it.isNullOrEmpty()) view!!.empty_view.visible() else view!!.empty_view.gone()
        view!!.rv_employee.adapter?.let { adapter ->
            if(adapter is EmployeeAdapter){
                adapter.updateList(it)
            }
        }
    }

    private fun addEmployee(){
        if(getRole() == 0){
            requireView().fab.hide()
        }else{
            requireView().fab.show()
            requireView().fab.setOnClickListener {
                val store : Store = parentStoreViewModel.listenToCurrentStore().value!!
                startActivity(Intent(activity, SearchUserActivity::class.java).apply {
                    putExtra("store", store)
                })
            }
        }
    }

    private fun getRole() = requireActivity().intent.getIntExtra("ROLE", -1)

    override fun onResume() {
        super.onResume()
        fetchEmployees()
    }

    override fun click(employee: Employee) {
        println()
    }

    override fun moreClick(employee: Employee, v: View) {
        val storeId = parentStoreViewModel.listenToCurrentStore().value?.id.toString()
        PopupMenu(requireActivity(), v).apply {
            menuInflater.inflate(R.menu.menu_employee_adapter, menu)
            setOnMenuItemClickListener { menuItems ->
                when(menuItems.itemId){
                    R.id.menu_delete -> {
                        if(getRole() != -1){
                            removeEmployee(storeId, employee.id.toString())
                        }else{
                            requireActivity().showInfoAlert(resources.getString(R.string.permission_not_allowed))
                        }
                        true
                    }
                    else -> true
                }
            }
        }.show()
    }


    private fun removeEmployee(storeId: String, employeeId: String){
        PaperlessUtil.getToken(requireActivity())?.let {
            employeeViewModel.removeEmployee(it, storeId, employeeId)
        }
    }

    private fun fetchEmployees(){
        PaperlessUtil.getToken(requireActivity())?.let { it1 ->
            employeeViewModel.fetchEmployees(it1, parentStoreViewModel.listenToCurrentStore().value?.id.toString())
        }
    }
}