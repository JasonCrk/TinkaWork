package com.latinka.tinkawork.auth.viewmodel.events

sealed class RestorePasswordFormEvent {
    data class EmailChanged(val email: String): RestorePasswordFormEvent()
    data object Submit: RestorePasswordFormEvent()
}