package com.univeloued.rico.data.security

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import com.univeloued.rico.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabasePassphraseManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val keyStoreManager: KeyStoreManager,
    private val cryptoManager: CryptoManager,
) {
    private val sharedPrefs = context.getSharedPreferences(Constants.SECURE_PREFS_NAME, Context.MODE_PRIVATE)
    private var unlockedDatabasePassphrase: ByteArray? = null
    private var encryptionSalt: ByteArray? = null
    private var masterPassword: ByteArray? = null
    
    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    fun setUnlockedDatabasePassphrase(passphrase: ByteArray?) {
        unlockedDatabasePassphrase = passphrase
        updateLockState()
    }

    fun setMasterPassword(password: ByteArray?) {
        masterPassword = password
        
        // Securely persist the master password encrypted with the non-biometric database key
        // This allows the background SyncWorker to access it without a fingerprint prompt.
        if (password != null) {
            try {
                val encrypted = cryptoManager.encrypt(password, keyStoreManager.getDatabaseKey())
                sharedPrefs.edit { putString(ENCRYPTED_MASTER_PASS, encrypted) }
            } catch (e: Exception) {
                android.util.Log.e("DBPassphraseManager", "Failed to securely persist master password", e)
            }
        }
        
        updateLockState()
    }

    private fun updateLockState() {
        // App is unlocked if we have the DB passphrase. 
        _isLocked.value = (unlockedDatabasePassphrase == null)
    }

    fun getMasterPassword(): ByteArray? {
        if (masterPassword != null) return masterPassword
        
        // Try to recover from secure storage using the background-safe key
        val encrypted = sharedPrefs.getString(ENCRYPTED_MASTER_PASS, null)
        if (encrypted != null) {
            try {
                masterPassword = cryptoManager.decrypt(encrypted, keyStoreManager.getDatabaseKey())
                return masterPassword
            } catch (e: Exception) {
                android.util.Log.e("DBPassphraseManager", "Failed to recover master password", e)
            }
        }
        return null
    }

    fun setEncryptionSalt(salt: ByteArray) {
        encryptionSalt = salt
        sharedPrefs.edit { putString(ENCRYPTION_SALT, Base64.encodeToString(salt, Base64.NO_WRAP)) }
    }

    fun getEncryptionSalt(): ByteArray? {
        if (encryptionSalt != null) return encryptionSalt
        val saltString = sharedPrefs.getString(ENCRYPTION_SALT, null)
        return saltString?.let { Base64.decode(it, Base64.NO_WRAP) }
    }

    fun getUnlockedDatabasePassphrase(): ByteArray? {
        return unlockedDatabasePassphrase
    }

    fun clear() {
        unlockedDatabasePassphrase = null
        masterPassword = null
        _isLocked.value = true
    }

    fun isLocked(): Boolean {
        return unlockedDatabasePassphrase == null || masterPassword == null
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
        if (encryptedPassphrase != null) {
            try {
                // Try non-biometric key first
                val decrypted = cryptoManager.decrypt(encryptedPassphrase, keyStoreManager.getDatabaseKey())
                unlockedDatabasePassphrase = decrypted
                updateLockState()
                return decrypted
            } catch (e: Exception) {
                // Fallback to biometric key for migration
                try {
                    val decrypted = cryptoManager.decrypt(encryptedPassphrase, keyStoreManager.getMasterKey())
                    // Re-encrypt with non-biometric key for next time
                    val reEncrypted = cryptoManager.encrypt(decrypted, keyStoreManager.getDatabaseKey())
                    sharedPrefs.edit { putString(ENCRYPTED_DB_PASSPHRASE, reEncrypted) }
                    unlockedDatabasePassphrase = decrypted
                    updateLockState()
                    return decrypted
                } catch (e2: Exception) {
                    // If everything fails, we must generate a new key (destructive for the DB file)
                    val newPassphrase = ByteArray(32).apply { SecureRandom().nextBytes(this) }
                    val encrypted = cryptoManager.encrypt(newPassphrase, keyStoreManager.getDatabaseKey())
                    sharedPrefs.edit { putString(ENCRYPTED_DB_PASSPHRASE, encrypted) }
                    unlockedDatabasePassphrase = newPassphrase
                    updateLockState()
                    return newPassphrase
                }
            }
        } else {
            val newPassphrase = ByteArray(32).apply { SecureRandom().nextBytes(this) }
            val encrypted = cryptoManager.encrypt(newPassphrase, keyStoreManager.getDatabaseKey())
            sharedPrefs.edit { putString(ENCRYPTED_DB_PASSPHRASE, encrypted) }
            unlockedDatabasePassphrase = newPassphrase
            updateLockState()
            return newPassphrase
        }
    }

    companion object {
        private const val ENCRYPTED_DB_PASSPHRASE = "enc_db_pass"
        private const val ENCRYPTION_SALT = "enc_salt"
        private const val ENCRYPTED_MASTER_PASS = "enc_master_pass"
    }
}
