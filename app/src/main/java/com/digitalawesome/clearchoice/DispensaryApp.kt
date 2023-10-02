package com.digitalawesome.clearchoice

import android.app.Application
import com.digitalawesome.clearchoice.di.appModule
import com.digitalawesome.clearchoice.di.serviceModule
import com.digitalawesome.clearchoice.di.viewModelModule
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
