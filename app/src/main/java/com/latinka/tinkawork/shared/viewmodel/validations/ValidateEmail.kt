package com.latinka.tinkawork.shared.viewmodel.validations

import android.util.Patterns

class ValidateEmail : ValidateField<String> {

    override fun validate(value: String): ValidationResult {
        if (value.isEmpty() || value.isBlank())
            return ValidationResult(errorMessage = "El correo electrónico es requerida")

        if (!Patterns.EMAIL_ADDRESS.matcher(value).matches())
            return ValidationResult(errorMessage = "El correo electrónico es invalido")

        return ValidationResult(isValid = true)
    }
}