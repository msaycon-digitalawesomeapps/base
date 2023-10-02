package com.digitalawesome.recweed

import com.digitalawesome.dispensary.domain.application.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DispatcherProviderImpl : DispatcherProvider {
    override fun io(): CoroutineDispatcher = Dispatchers.IO

    override fun main(): CoroutineDispatcher = Dispatchers.Main

    override fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}