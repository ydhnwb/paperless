package com.ydhnwb.paperlessapp.ui.manage.employee

import android.view.View
import com.ydhnwb.paperlessapp.models.Employee

interface EmployeeInterface {
    fun click(employee: Employee)
    fun moreClick(employee: Employee, v : View)
}