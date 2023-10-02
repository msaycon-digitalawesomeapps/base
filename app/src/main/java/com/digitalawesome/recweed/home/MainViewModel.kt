package com.digitalawesome.recweed.home

import androidx.lifecycle.ViewModel
import com.digitalawesome.dispensary.domain.interactors.AuthInteractor

class MainViewModel(private val authInteractor: AuthInteractor) : ViewModel() {

    fun isLoggedIn() = authInteractor.isLoggedIn()

}