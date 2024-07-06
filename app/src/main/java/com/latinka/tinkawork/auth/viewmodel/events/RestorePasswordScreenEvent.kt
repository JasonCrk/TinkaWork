package com.latinka.tinkawork.auth.viewmodel.events

sealed class RestorePasswordScreenEvent {
    data object Loading: RestorePasswordScreenEvent()
    data object Success: RestorePasswordScreenEvent()
    data class Error(val error: String): RestorePasswordScreenEvent()
}