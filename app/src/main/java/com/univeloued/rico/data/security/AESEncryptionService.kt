package com.univeloued.rico.data.security

import com.univeloued.rico.domain.security.EncryptionService
import javax.inject.Inject
import javax.inject.Singleton
import java.security.KeyStore
import javax.crypto.SecretKey

@Singleton
class AESEncryptionService @Inject constructor(
    private val cryptoManager: CryptoManager,
    private val passphraseManager: DatabasePassphraseManager,
    private val masterKeyManager: MasterKeyManager
) : EncryptionService {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val legacyKeyAlias = "rico_e2ee_key"

    private fun getSecretKey(): SecretKey {
        val passphraseBytes = passphraseManager.getMasterPassword()
            ?: throw IllegalStateException("Encryption key not initialized. App must be unlocked.")
        
        val salt = passphraseManager.getEncryptionSalt() ?: run {
            // Check if we can get it from the legacy system for transition
            val legacySalt = getLegacySecretKey()
            if (legacySalt != null) return legacySalt
            
            // Critical: If we are here and have no salt, we cannot derive the master key.
            // We should NOT generate a random one here as it would break recovery.
            throw IllegalStateException("Security Salt missing. Please check your internet connection and restart the app.")
        }
        
        val password = String(passphraseBytes, Charsets.UTF_8)
        return masterKeyManager.deriveKey(password, salt)
    }

    private fun getLegacySecretKey(): SecretKey? {
        val existingKey = keyStore.getEntry(legacyKeyAlias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey
    }

    override fun encrypt(plainText: String): String {
        return cryptoManager.encrypt(plainText.toByteArray(), getSecretKey())
    }

    override fun decrypt(encryptedText: String): String {
        return try {
            // Try new passphrase-based key derived with PBKDF2 first
            String(cryptoManager.decrypt(encryptedText, getSecretKey()))
        } catch (e: Exception) {
            // Fallback to legacy device-specific key for migration
            val legacyKey = getLegacySecretKey()
            if (legacyKey != null) {
                try {
                    String(cryptoManager.decrypt(encryptedText, legacyKey))
                } catch (e2: Exception) {
                    throw e // If legacy also fails, throw the original error
                }
            } else {
                throw e
            }
        }
    }
}
