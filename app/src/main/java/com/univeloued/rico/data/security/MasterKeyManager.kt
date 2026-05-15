package com.univeloued.rico.data.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterKeyManager @Inject constructor() {

    private val iterations = 10000
    private val keyLength = 256

    /**
     * Generates a random 16-byte salt for key derivation.
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

    /**
     * Derives a 256-bit AES key from a password and salt using PBKDF2.
     */
    fun deriveKey(password: String, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }
    
    fun saltToString(salt: ByteArray): String = Base64.encodeToString(salt, Base64.NO_WRAP)
    
    fun stringToSalt(saltString: String): ByteArray = Base64.decode(saltString, Base64.NO_WRAP)
}
