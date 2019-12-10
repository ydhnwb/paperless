package com.ydhnwb.paperlessapp.presenters.activities

import com.ydhnwb.paperlessapp.contracts.activities.MainActivityContract
import com.ydhnwb.paperlessapp.webservices.ApiClient

class MainActivityPresenter(var view : MainActivityContract.View?) : MainActivityContract.Interactor {
    private val api = ApiClient.instance()

    override fun destroy() { view = null }

    override fun authChecker() {}


}