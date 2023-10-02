package com.digitalawesome.recweed

import android.app.Application
import com.digitalawesome.recweed.di.appModule
import com.digitalawesome.recweed.di.serviceModule
import com.digitalawesome.recweed.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class DispensaryApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupDependencyInjection()
    }

    private fun setupDependencyInjection() {
        startKoin {
            androidContext(this@DispensaryApp)
            modules(
                listOf(
                    appModule,
                    serviceModule,
                    viewModelModule
                )
            )
        }
    }
}
