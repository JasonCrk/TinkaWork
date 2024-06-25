package com.latinka.tinkawork.shared.viewmodel.validations

interface ValidateField<T> {
    fun validate(value: T): ValidationResult
}