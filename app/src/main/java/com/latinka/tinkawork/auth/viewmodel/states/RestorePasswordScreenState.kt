package com.latinka.tinkawork.auth.viewmodel.states

data class RestorePasswordScreenState(
    var email: String = "",
    var emailError: String? = null
)