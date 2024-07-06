package com.latinka.tinkawork.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.FirebaseAuth

import com.latinka.tinkawork.auth.viewmodel.events.RestorePasswordFormEvent
import com.latinka.tinkawork.auth.viewmodel.events.RestorePasswordScreenEvent
import com.latinka.tinkawork.auth.viewmodel.states.RestorePasswordScreenState
import com.latinka.tinkawork.shared.viewmodel.validations.ValidateEmail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RestorePasswordScreenViewModel : ViewModel() {

    private val _screenState = MutableStateFlow(RestorePasswordScreenState())
    val screenState = _screenState.asStateFlow()

    private val _screenEvent = MutableSharedFlow<RestorePasswordScreenEvent>()
    val screenEvent = _screenEvent.asSharedFlow()

    private val validateEmail = ValidateEmail()

    private val auth = FirebaseAuth.getInstance()

    fun onEvent(event: RestorePasswordFormEvent) {
        when (event) {
            is RestorePasswordFormEvent.EmailChanged -> {
                _screenState.value = _screenState.value.copy(
                    email = event.email
                )
            }
            RestorePasswordFormEvent.Submit -> {
                sendEmailToRestorePassword()
            }
        }
    }

    private fun sendEmailToRestorePassword() {
        val emailFieldResult = validateEmail.validate(_screenState.value.email)

        if (!emailFieldResult.isValid) {
            _screenState.value = _screenState.value.copy(emailError = emailFieldResult.errorMessage)
            return
        }

        viewModelScope.launch {
            try {
                _screenEvent.emit(RestorePasswordScreenEvent.Loading)
                withContext(Dispatchers.IO) {
                    auth.sendPasswordResetEmail(_screenState.value.email).await()
                }
                _screenEvent.emit(RestorePasswordScreenEvent.Success)
            } catch (e: Exception) {
                _screenEvent.emit(RestorePasswordScreenEvent.Error(e.message.toString()))
            }
        }
    }
}