package com.ydhnwb.paperlessapp.ui.analytic.customer

import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User

data class StoreAndTransaction(var store: Store, var sumOfTransaction: Int)


data class UserAndTransaction(var user: User, var sumOfTransaction: Int)