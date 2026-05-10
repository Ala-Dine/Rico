package com.univeloued.rico.data.util

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileHelper @Inject constructor(@ApplicationContext private val context: Context) {
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
            
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
