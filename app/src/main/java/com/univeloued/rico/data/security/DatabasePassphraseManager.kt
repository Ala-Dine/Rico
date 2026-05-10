package com.univeloued.rico.data.security

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabasePassphraseManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val keyStoreManager: KeyStoreManager,
    private val cryptoManager: CryptoManager,
) {
    private val sharedPrefs = context.getSharedPreferences("rico_secure_prefs", Context.MODE_PRIVATE)
    private var unlockedDatabasePassphrase: ByteArray? = null

    fun setUnlockedDatabasePassphrase(passphrase: ByteArray) {
        unlockedDatabasePassphrase = passphrase
    }

    fun getEncryptedDatabasePassphrase(): Pair<ByteArray, ByteArray>? {
        val encryptedString = sharedPrefs.getString(ENCRYPTED_DB_PASSPHRASE, null) ?: return null
        val combined = Base64.decode(encryptedString, Base64.DEFAULT)
        val iv = combined.sliceArray(0 until 12)
        val encryptedData = combined.sliceArray(12 until combined.size)
        return Pair(encryptedData, iv)
    }

    fun getDatabasePassphrase(): ByteArray {
        unlockedDatabasePassphrase?.let { return it }

        val encryptedPassphrase = sharedPrefs.getString(ENCRYPTED_DB_PASSPHRASE, null)
        return if (encryptedPassphrase != null) {
            cryptoManager.decrypt(encryptedPassphrase, keyStoreManager.getMasterKey())
        } else {
            val newPassphrase = ByteArray(32).apply { SecureRandom().nextBytes(this) }
            val encrypted = cryptoManager.encrypt(newPassphrase, keyStoreManager.getMasterKey())
            sharedPrefs.edit { putString(ENCRYPTED_DB_PASSPHRASE, encrypted) }
            newPassphrase
        }
    }

    companion object {
        private const val ENCRYPTED_DB_PASSPHRASE = "enc_db_pass"
    }
}
