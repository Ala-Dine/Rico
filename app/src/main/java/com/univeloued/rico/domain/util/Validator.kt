package com.univeloued.rico.domain.util

object Validator {
    private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$".toRegex()

    fun validateEmail(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult.Error("Email cannot be empty")
        }
        if (!EMAIL_REGEX.matches(email)) {
            return ValidationResult.Error("Invalid email format")
        }
        return ValidationResult.Success
    }

    fun validatePhone(phone: String): ValidationResult {
        if (phone.isBlank()) {
            return ValidationResult.Error("Phone number cannot be empty")
        }
        if (phone.length < 10) {
            return ValidationResult.Error("Phone number is too short")
        }
        return ValidationResult.Success
    }

    fun validateRequiredField(value: String, fieldName: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult.Error("$fieldName cannot be empty")
        }
        return ValidationResult.Success
    }
}
