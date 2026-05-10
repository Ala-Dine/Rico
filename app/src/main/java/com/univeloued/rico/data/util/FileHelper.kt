package com.univeloued.rico.data.util

import android.content.Context
import android.net.Uri
import com.univeloued.rico.data.security.CryptoManager
import com.univeloued.rico.data.security.KeyStoreManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileHelper @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val keyStoreManager: KeyStoreManager,
    private val cryptoManager: CryptoManager
) {
    fun saveFileToInternalStorage(uri: Uri, subDir: String): String? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            
            val directory = File(context.filesDir, subDir)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            
            val fileName = UUID.randomUUID().toString()
            val file = File(directory, fileName)
            val outputStream = FileOutputStream(file)
            
            // Encrypt on the fly while saving
            val secureOutputStream = cryptoManager.getEncryptingOutputStream(outputStream, keyStoreManager.getMasterKey())
            
            inputStream.use { input ->
                secureOutputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getDecryptedFileStream(filePath: String): java.io.InputStream? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null
            
            val inputStream = java.io.FileInputStream(file)
            // Decrypt on the fly while reading
            cryptoManager.getDecryptingInputStream(inputStream, keyStoreManager.getMasterKey())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
