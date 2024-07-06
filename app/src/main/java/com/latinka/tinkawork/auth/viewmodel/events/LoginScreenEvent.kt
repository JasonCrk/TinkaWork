package com.latinka.tinkawork.auth.viewmodel.events

sealed class LoginScreenEvent {
    data object Loading: LoginScreenEvent()
    data class Error(val errorMessage: String): LoginScreenEvent()
    data object Success: LoginScreenEvent()
    data object Completed: LoginScreenEvent()
}