package com.ydhnwb.paperlessapp

import android.app.Application
import com.ydhnwb.paperlessapp.ui.catalog.CatalogViewModel
import com.ydhnwb.paperlessapp.ui.checkout.CheckoutViewModel
import com.ydhnwb.paperlessapp.ui.detail_order.DetailOrderViewModel
import com.ydhnwb.paperlessapp.ui.invitation.InvitationViewModel
import com.ydhnwb.paperlessapp.ui.login.LoginViewModel
import com.ydhnwb.paperlessapp.ui.manage.ManageStoreViewModel
import com.ydhnwb.paperlessapp.ui.product.ProductCreateEditViewModel
import com.ydhnwb.paperlessapp.ui.register.RegisterViewModel
import com.ydhnwb.paperlessapp.ui.search_user.SearchUserViewModel
import com.ydhnwb.paperlessapp.ui.store.CreateStoreViewModel
import com.ydhnwb.paperlessapp.ui.store_page.StorePageViewModel
import com.ydhnwb.paperlessapp.ui.user_history.UserHistoryViewModel
import com.ydhnwb.paperlessapp.ui.analytic.customer.CustomerAnalyticViewModel
import com.ydhnwb.paperlessapp.ui.analytic.purchasement.PurchasementFragmentViewModel
import com.ydhnwb.paperlessapp.ui.analytic.selling.SellingAnalyticViewModel
import com.ydhnwb.paperlessapp.fragments.dialog.InvitationDialogViewModel
import com.ydhnwb.paperlessapp.ui.main.dashboard.DashboardViewModel
import com.ydhnwb.paperlessapp.ui.main.explore.ExploreViewModel
import com.ydhnwb.paperlessapp.ui.main.notification.NotificationViewModel
import com.ydhnwb.paperlessapp.ui.main.profile.ProfileViewModel
import com.ydhnwb.paperlessapp.ui.manage.employee.EmployeeViewModel
import com.ydhnwb.paperlessapp.ui.manage.product.ProductViewModel
import com.ydhnwb.paperlessapp.ui.manage.history.list_history.ListHistoryViewModel
import com.ydhnwb.paperlessapp.repositories.*
import com.ydhnwb.paperlessapp.ui.quickupdate.QuickUpdateViewModel
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
    viewModel { PurchasementFragmentViewModel(get()) }
    viewModel { CustomerAnalyticViewModel(get()) }
    viewModel { DetailOrderViewModel(get()) }
    viewModel { UserHistoryViewModel(get()) }
    viewModel { QuickUpdateViewModel(get()) }
}