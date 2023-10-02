package com.digitalawesome.clearchoice.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalawesome.dispensary.components.models.GenericError
import com.digitalawesome.dispensary.domain.application.DomainResponse
import com.digitalawesome.dispensary.domain.interactors.AuthInteractor
import kotlinx.coroutines.launch

class AuthViewModel(private val authInteractor: AuthInteractor) : ViewModel() {

    private val _uiState = MutableLiveData<AuthUiState>()
    val uiState: LiveData<AuthUiState> = _uiState

    var lastEmailInput = ""
        private set

    fun login(username: String, password: String) {
        lastEmailInput = username
        viewModelScope.launch {
            val response = authInteractor.login(username, password)

            when (response) {
                is DomainResponse.Success -> {
                    _uiState.value = AuthUiState.LoginSuccess
                }

                is DomainResponse.Error -> {
                    _uiState.value = AuthUiState.LoginError(GenericError(response.error.message))
                }
            }
        }
    }

    fun signUp(firstName: String, lastName: String, phone: String, email: String, password: String, passwordConfirmation: String, code: String?, optInEmail: Int, optInText: Int, state: String) {
        lastEmailInput = email
        viewModelScope.launch {
            val response = authInteractor.register(
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                email = email,
                password = password,
                passwordConfirmation = passwordConfirmation,
                code = code,
                optInEmail = optInEmail,
                optInText = optInText,
                state = state,
            )

            when (response) {
                is DomainResponse.Success -> {
                    _uiState.value = AuthUiState.SignupSuccess
                }

                is DomainResponse.Error -> {
                    _uiState.value = AuthUiState.SignupError(response.error)
                }
            }
        }
    }
}