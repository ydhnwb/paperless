package com.ydhnwb.paperlessapp.presenters.fragments

import com.ydhnwb.paperlessapp.contracts.fragments.DashboardFragmentContract
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.webservices.ApiClient

class DashboardFragmentPresenter(var view : DashboardFragmentContract.View?) : DashboardFragmentContract.Interactor {
    private var api = ApiClient.instance()

    override fun allMyStores() {
        view?.isMyStoreLoading(true)
        val list = mutableListOf<Store>()
        for(i in 0..4){
            list.add(Store().apply {
                id = i
                name = "Toko Abadi $i"
                store_logo = "https://cdn.vox-cdn.com/thumbor/SVEQv9ZyogzkPLs4PwTCh1NBCHg=/0x0:2048x1365/1200x800/filters:focal(861x520:1187x846)/cdn.vox-cdn.com/uploads/chorus_image/image/59488337/20786021_1964885550462647_3189575152413374824_o.0.jpg"
            })
        }
        if(list.isEmpty()){ view?.showEmptyMyStore(true) }
        view?.attachToMyStores(list)
        view?.isMyStoreLoading(false)
    }

    override fun allOtherStore() {
        view?.isOtherStoreLoading(true)
        val list= mutableListOf<Store>()
        for(i in 0..4){
            list.add(Store().apply {
                id = i
                name = "Toko lain $i"
                store_logo = "https://cdn.vox-cdn.com/thumbor/SVEQv9ZyogzkPLs4PwTCh1NBCHg=/0x0:2048x1365/1200x800/filters:focal(861x520:1187x846)/cdn.vox-cdn.com/uploads/chorus_image/image/59488337/20786021_1964885550462647_3189575152413374824_o.0.jpg"
            })
        }
        if(list.isEmpty()) { view?.showEmptyOtherStore((true))}
        view?.attachToOtherStores(list)
        view?.isOtherStoreLoading(false)
    }

    override fun destroy() { view = null }
}