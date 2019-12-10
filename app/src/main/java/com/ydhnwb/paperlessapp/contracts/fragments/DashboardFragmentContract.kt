package com.ydhnwb.paperlessapp.contracts.fragments

import com.ydhnwb.paperlessapp.models.Store

interface DashboardFragmentContract {
    interface View {
        fun showEmptyMyStore(state : Boolean)
        fun showEmptyOtherStore(state : Boolean)
        fun isMyStoreLoading(state : Boolean)
        fun isOtherStoreLoading(state : Boolean)
        fun attachToMyStores(my_stores : List<Store>)
        fun attachToOtherStores(other_stores : List<Store>)
    }
    interface Interactor {
        fun allMyStores()
        fun allOtherStore()
        fun destroy()
    }
}