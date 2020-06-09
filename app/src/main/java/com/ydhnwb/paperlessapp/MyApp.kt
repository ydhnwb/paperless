package com.ydhnwb.paperlessapp

import android.app.Application
import com.ydhnwb.paperlessapp.activities.catalog_activity.CatalogViewModel
import com.ydhnwb.paperlessapp.activities.checkout_activity.CheckoutViewModel
import com.ydhnwb.paperlessapp.activities.invitation_activity.InvitationViewModel
import com.ydhnwb.paperlessapp.activities.login_activity.LoginViewModel
import com.ydhnwb.paperlessapp.activities.manage_activity.ManageStoreViewModel
import com.ydhnwb.paperlessapp.activities.product_activity.ProductCreateEditViewModel
import com.ydhnwb.paperlessapp.activities.register_activity.RegisterViewModel
import com.ydhnwb.paperlessapp.activities.search_user_activity.SearchUserViewModel
import com.ydhnwb.paperlessapp.activities.store_activity.CreateStoreViewModel
import com.ydhnwb.paperlessapp.activities.store_page_activity.StorePageViewModel
import com.ydhnwb.paperlessapp.fragments.analytic.selling.SellingAnalyticViewModel
import com.ydhnwb.paperlessapp.fragments.dialog.InvitationDialogViewModel
import com.ydhnwb.paperlessapp.fragments.main.dashboard_fragment.DashboardViewModel
import com.ydhnwb.paperlessapp.fragments.main.explore_fragment.ExploreViewModel
import com.ydhnwb.paperlessapp.fragments.main.notification_fragment.NotificationViewModel
import com.ydhnwb.paperlessapp.fragments.main.profile.ProfileViewModel
import com.ydhnwb.paperlessapp.fragments.manage.employee_fragment.EmployeeViewModel
import com.ydhnwb.paperlessapp.fragments.manage.product_fragment.ProductViewModel
import com.ydhnwb.paperlessapp.fragments.others.list_history_fragment.ListHistoryViewModel
import com.ydhnwb.paperlessapp.repositories.*
import com.ydhnwb.paperlessapp.webservices.ApiClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class MyApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApp)
            modules(listOf(retrofitModules, viewModelModules, repositoryModules))
        }
    }
}

val retrofitModules = module {
    single { ApiClient.instance() }
}

val repositoryModules = module {
    factory { EmployeeRepository(get()) }
    factory { UserRepository(get()) }
    factory { CategoryRepository(get()) }
    factory { HistoryRepository(get()) }
    factory { OrderRepository(get()) }
    factory { ProductRepository(get()) }
    factory { StoreRepository(get()) }
    factory { InvitationRepository(get()) }
}

val viewModelModules = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { SearchUserViewModel(get()) }
    viewModel { ExploreViewModel(get()) }
    viewModel { ProductCreateEditViewModel(get(), get()) }
    viewModel { ProductViewModel(get()) }
    viewModel { ListHistoryViewModel(get()) }
    viewModel { EmployeeViewModel(get()) }
    viewModel { ManageStoreViewModel(get(), get(), get()) }
    viewModel { CreateStoreViewModel(get()) }
    viewModel { CheckoutViewModel(get()) }
    viewModel { InvitationViewModel(get()) }
    viewModel { CatalogViewModel(get()) }
    viewModel { StorePageViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { NotificationViewModel() }
    viewModel { InvitationDialogViewModel(get()) }
    viewModel { SellingAnalyticViewModel(get()) }
}