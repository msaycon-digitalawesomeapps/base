package com.digitalawesome.recweed.auth

import com.digitalawesome.dispensary.components.models.ErrorType
import com.digitalawesome.dispensary.domain.application.DomainError

sealed class AuthUiState {
    object Initial: AuthUiState()
    object InProgress: AuthUiState()
    object LoginSuccess: AuthUiState()
    class LoginError(val errorType: ErrorType): AuthUiState()
    object SignupSuccess: AuthUiState()
    class SignupError(val error: DomainError): AuthUiState()
    object ForgotPasswordSuccess: AuthUiState()
    class ForgotPasswordError(val errorType: ErrorType): AuthUiState()
    class Error(val errorType: ErrorType): AuthUiState()
}

