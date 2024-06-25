package com.latinka.tinkawork.auth.viewmodel.states

data class LoginScreenState(
    var email: String = "",
    var emailError: String? = null,
    var password: String = "",
    var passwordError: String? = null
)
