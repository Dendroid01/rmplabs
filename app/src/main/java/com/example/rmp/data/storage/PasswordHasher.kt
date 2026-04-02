package com.example.rmp.data.storage

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.MessageDigest

object PasswordHasher {

    private const val ITERATIONS = 65536
    private const val KEY_LENGTH = 256

    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

    fun hash(password: String, salt: ByteArray): String {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(hash)
    }

    fun verify(password: String, salt: ByteArray, expectedHash: String): Boolean {
        val hash = hash(password, salt)
        return MessageDigest.isEqual(hash.toByteArray(), expectedHash.toByteArray())
    }
}