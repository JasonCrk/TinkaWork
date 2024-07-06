package com.latinka.tinkawork.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.FirebaseAuth

import com.latinka.tinkawork.auth.viewmodel.events.LoginFormEvent
import com.latinka.tinkawork.auth.viewmodel.events.LoginScreenEvent
import com.latinka.tinkawork.auth.viewmodel.states.LoginScreenState
import com.latinka.tinkawork.shared.viewmodel.validations.ValidateEmail
import com.latinka.tinkawork.shared.viewmodel.validations.ValidatePassword

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginScreenViewModel : ViewModel() {

    private val _loginScreenState = MutableStateFlow(LoginScreenState())
    val loginScreenState: StateFlow<LoginScreenState> = _loginScreenState.asStateFlow()

    private val _loginScreenEvent = MutableSharedFlow<LoginScreenEvent>()
    val loginScreenEvent = _loginScreenEvent.asSharedFlow()

    private val validateEmail = ValidateEmail()
    private val validatePassword = ValidatePassword()

    fun onEvent(event: LoginFormEvent) {
        when (event) {
            is LoginFormEvent.EmailChanged -> {
                _loginScreenState.value = _loginScreenState.value.copy(
                    email = event.email
                )
            }
            is LoginFormEvent.PasswordChanged -> {
                _loginScreenState.value = _loginScreenState.value.copy(
                    password = event.password
                )
            }
            LoginFormEvent.Submit -> { signIn() }
        }
    }

    private fun signIn() {
        val emailFieldResult = validateEmail.validate(_loginScreenState.value.email)
        val passwordFieldResult = validatePassword.validate(_loginScreenState.value.password)

        val hasError = listOf(emailFieldResult, passwordFieldResult).any { !it.isValid }

        if (hasError) {
            _loginScreenState.value = _loginScreenState.value.copy(
                emailError = emailFieldResult.errorMessage,
                passwordError = passwordFieldResult.errorMessage
            )
            return
        }

        val auth = FirebaseAuth.getInstance()

        viewModelScope.launch {
            try {
                _loginScreenEvent.emit(LoginScreenEvent.Loading)
                auth.signInWithEmailAndPassword(_loginScreenState.value.email, _loginScreenState.value.password).await()
                _loginScreenEvent.emit(LoginScreenEvent.Success)
            } catch (e: Exception) {
                _loginScreenEvent.emit(LoginScreenEvent.Error(e.message.toString()))
            } finally {
                _loginScreenEvent.emit(LoginScreenEvent.Completed)
            }
        }
    }
}
