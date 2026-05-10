package com.univeloued.rico.data.security

import android.util.Base64
import java.io.InputStream
import java.io.OutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor() {

    private val algorithm = "AES/GCM/NoPadding"

    fun getEncryptingOutputStream(outputStream: OutputStream, key: java.security.Key): OutputStream {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        outputStream.write(iv)
        return CipherOutputStream(outputStream, cipher)
    }

    fun getDecryptingInputStream(inputStream: InputStream, key: java.security.Key): InputStream {
        val iv = ByteArray(12)
        inputStream.read(iv)
        val cipher = Cipher.getInstance(algorithm)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return CipherInputStream(inputStream, cipher)
    }

    fun encrypt(data: ByteArray, key: java.security.Key): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedData = cipher.doFinal(data)
        val combined = cipher.iv + encryptedData
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decrypt(encryptedString: String, key: java.security.Key): ByteArray {
        val combined = Base64.decode(encryptedString, Base64.DEFAULT)
        val iv = combined.sliceArray(0 until 12)
        val encryptedData = combined.sliceArray(12 until combined.size)
        
        val cipher = Cipher.getInstance(algorithm)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(encryptedData)
    }

    fun decryptWithCipher(cipher: Cipher, encryptedData: ByteArray): ByteArray {
        return cipher.doFinal(encryptedData)
    }

    fun getInitializedCipher(mode: Int, key: java.security.Key, iv: ByteArray? = null): Cipher {
        val cipher = Cipher.getInstance(algorithm)
        if (mode == Cipher.ENCRYPT_MODE) {
            cipher.init(mode, key)
        } else {
            val spec = GCMParameterSpec(128, iv)
            cipher.init(mode, key, spec)
        }
        return cipher
    }
}
