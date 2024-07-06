package com.latinka.tinkawork.shared.viewmodel.validations

data class ValidationResult(
    val errorMessage: String = "",
    val isValid: Boolean = false
)