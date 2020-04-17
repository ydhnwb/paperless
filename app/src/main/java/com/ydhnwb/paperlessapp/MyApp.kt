package com.ydhnwb.paperlessapp

import android.app.Application
import com.ydhnwb.paperlessapp.viewmodels.*
import com.ydhnwb.paperlessapp.webservices.ApiClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import retrofit2.Retrofit

class MyApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApp)
            modules(listOf(retrofitModules, viewModelModules))
        }
    }
}

val retrofitModules = module {
    single { ApiClient.instance() }
}

val viewModelModules = module {
    viewModel { CategoryViewModel(get()) }
    viewModel { EmployeeViewModel(get()) }
    viewModel { ProductViewModel(get()) }
    viewModel { StoreViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { InvitationViewModel(get()) }
}