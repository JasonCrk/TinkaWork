package com.latinka.tinkawork.shared.viewmodel.validations

class ValidatePassword : ValidateField<String> {

    override fun validate(value: String): ValidationResult {
        if (value.isEmpty() || value.isBlank())
            return ValidationResult(errorMessage = "La contraseña es requerida")

        if (value.length < 5)
            return ValidationResult(errorMessage = "La contraseña necesita mínimo 5 caracteres")

        return ValidationResult(isValid = true)
    }
}