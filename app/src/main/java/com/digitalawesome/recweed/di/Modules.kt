package com.digitalawesome.recweed.di

import com.digitalawesome.dispensary.domain.application.AppDomainConfiguration
import com.digitalawesome.dispensary.domain.application.AppDomainManager
import com.digitalawesome.dispensary.domain.application.AppDomainManagerInterface
import com.digitalawesome.dispensary.domain.application.DispatcherProvider
import com.digitalawesome.dispensary.domain.application.DispensaryClient
import com.digitalawesome.dispensary.domain.application.ServiceCentreInterface
import com.digitalawesome.dispensary.domain.interactors.AppInteractor
import com.digitalawesome.dispensary.domain.interactors.AuthInteractor
import com.digitalawesome.dispensary.domain.interactors.ProductInteractor
import com.digitalawesome.dispensary.domain.interactors.StoreInteractor
import com.digitalawesome.dispensary.domain.interactors.UserInteractor
import com.digitalawesome.dispensary.domain.interactors.implementations.AppInteractorImpl
import com.digitalawesome.dispensary.domain.interactors.implementations.AuthInteractorImpl
import com.digitalawesome.dispensary.domain.interactors.implementations.ProductInteractorImpl
import com.digitalawesome.dispensary.domain.interactors.implementations.StoreInteractorImpl
import com.digitalawesome.dispensary.domain.interactors.implementations.UserInteractorImpl
import com.digitalawesome.dispensary.domain.services.APIService
import com.digitalawesome.dispensary.domain.services.SharedPrefsService
import com.digitalawesome.recweed.ActivityProvider
import com.digitalawesome.recweed.BuildConfig
import com.digitalawesome.recweed.DispatcherProviderImpl
import com.digitalawesome.recweed.ServiceCentre
import com.digitalawesome.recweed.UserViewModel
import com.digitalawesome.recweed.auth.AuthViewModel
import com.digitalawesome.recweed.home.HomeViewModel
import com.digitalawesome.recweed.home.MainViewModel
import com.digitalawesome.recweed.home.product.ProductDetailsViewModel
import com.digitalawesome.recweed.home.stores.StoresViewModel
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module

object ApplicationScopeQualifier : Qualifier {
    override val value: QualifierValue
        get() = "application_scope"
}

val appModule = module {
    single { AppDomainConfiguration(dispensaryClient = DispensaryClient.EXCLUSIVE) }
    single<AppDomainManagerInterface> {
        AppDomainManager(
            serviceCentre = get(),
            appDomainConfiguration = get(),
        )
    }

    single(createdAtStart = true) { ActivityProvider(get()) }


    single<ServiceCentreInterface> {
        ServiceCentre(
            apiService = get(),
            sharedPrefsService = get()
        )
    }

    single<DispatcherProvider> { DispatcherProviderImpl() }
    single<CoroutineExceptionHandler> {
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }
    }
    single(ApplicationScopeQualifier) { CoroutineScope(Dispatchers.Main + SupervisorJob()) }
    single { GsonBuilder().create() }

}

val serviceModule = module {
    single { APIService.create(BuildConfig.API_URL) }
    single {
        SharedPrefsService().also {
            SharedPrefsService.init(context = get())
        }
    }
    single<AppInteractor> {
        AppInteractorImpl(get(), get())
    }
    single<AuthInteractor> {
        AuthInteractorImpl(get(), get())
    }
    single<UserInteractor> {
        UserInteractorImpl(get(), get())
    }
    single<StoreInteractor> {
        StoreInteractorImpl(get(), get(), get())
    }
    single<ProductInteractor> {
        ProductInteractorImpl(get(), get())
    }

}

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { UserViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { ProductDetailsViewModel(get()) }
    viewModel { StoresViewModel(get(), get()) }
}