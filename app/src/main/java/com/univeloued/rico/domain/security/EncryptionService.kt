package com.univeloued.rico.domain.security

interface EncryptionService {
    fun encrypt(plainText: String): String
    fun decrypt(encryptedText: String): String
}
