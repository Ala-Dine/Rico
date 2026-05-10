package com.univeloued.rico.ui.security

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import androidx.activity.compose.LocalActivity
import com.univeloued.rico.data.security.CryptoManager
import com.univeloued.rico.data.security.DatabasePassphraseManager
import com.univeloued.rico.data.security.KeyStoreManager
import javax.crypto.Cipher

@Composable
fun AuthScreen(
    keyStoreManager: KeyStoreManager,
    cryptoManager: CryptoManager,
    databasePassphraseManager: DatabasePassphraseManager,
    onAuthenticated: (ByteArray) -> Unit
) {
    val context = LocalActivity.current as FragmentActivity
    val executor = ContextCompat.getMainExecutor(context)
    
    val encryptedInfo = databasePassphraseManager.getEncryptedDatabasePassphrase()
    
    val biometricPrompt = remember {
        BiometricPrompt(context, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val cipher = result.cryptoObject?.cipher
                    if (cipher != null && encryptedInfo != null) {
                        try {
                            val decrypted = cryptoManager.decryptWithCipher(cipher, encryptedInfo.first)
                            onAuthenticated(decrypted)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Decryption failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Initial setup or fallback
                        onAuthenticated(databasePassphraseManager.getDatabasePassphrase())
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context, "Auth error ($errorCode): $errString", Toast.LENGTH_SHORT).show()
                }
            })
    }

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Secure Login")
            .setSubtitle("Authenticate to unlock your database")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }

    val cryptoObject = remember {
        try {
            val obj = if (encryptedInfo != null) {
                val cipher = cryptoManager.getInitializedCipher(Cipher.DECRYPT_MODE, keyStoreManager.getMasterKey(), encryptedInfo.second)
                BiometricPrompt.CryptoObject(cipher)
            } else {
                val cipher = cryptoManager.getInitializedCipher(Cipher.ENCRYPT_MODE, keyStoreManager.getMasterKey())
                BiometricPrompt.CryptoObject(cipher)
            }
            obj
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Rico Health Record", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                try {
                    if (cryptoObject != null) {
                        biometricPrompt.authenticate(promptInfo, cryptoObject)
                    } else {
                        biometricPrompt.authenticate(promptInfo)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Unlock Rico")
        }
    }
}
