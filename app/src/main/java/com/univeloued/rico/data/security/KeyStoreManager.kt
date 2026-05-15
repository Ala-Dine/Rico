package com.univeloued.rico.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyStoreManager @Inject constructor() {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    fun getMasterKey(): SecretKey {
        val existingKey = keyStore.getEntry(MASTER_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: generateKey(MASTER_KEY_ALIAS, true)
    }

    fun getDatabaseKey(): SecretKey {
        val existingKey = keyStore.getEntry(DATABASE_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: generateKey(DATABASE_KEY_ALIAS, false)
    }

    private fun generateKey(alias: String, requireAuth: Boolean): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore",
        )
        val specBuilder = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)

        if (requireAuth) {
            specBuilder.setUserAuthenticationRequired(true)
            specBuilder.setUserAuthenticationValidityDurationSeconds(3600) // 1 hour
        } else {
            specBuilder.setUserAuthenticationRequired(false)
        }

        keyGenerator.init(specBuilder.build())
        return keyGenerator.generateKey()
    }

    fun deriveKeyFromPassphrase(passphrase: String, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, 10000, 256)
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    companion object {
        private const val MASTER_KEY_ALIAS = "rico_master_key"
        private const val DATABASE_KEY_ALIAS = "rico_db_key"
    }
}
