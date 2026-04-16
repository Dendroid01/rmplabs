package com.example.rmp.repository

import android.content.Context
import com.example.rmp.data.model.User
import com.example.rmp.data.storage.AppDatabase
import com.example.rmp.data.storage.PasswordHasher
import com.example.rmp.session.SessionManager
import java.util.Base64

class UserRepository(context: Context) {

    private val userDao = AppDatabase.getInstance(context).userDao()
    private val sessionManager = SessionManager(context)

    suspend fun register(username: String, email: String, password: String): Result<Unit> {

        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            return Result.failure(Exception("Email уже занят"))
        }

        val saltBytes = PasswordHasher.generateSalt()
        val saltBase64 = Base64.getEncoder().encodeToString(saltBytes)
        val passwordHash = PasswordHasher.hash(password, saltBytes)

        val user = User(
            username = username,
            email = email,
            passwordHash = passwordHash,
            salt = saltBase64
        )

        val userId = userDao.insertUser(user).toInt()
        sessionManager.login(userId)

        return Result.success(Unit)
    }

    suspend fun login(email: String, password: String): Result<Unit> {

        val user = userDao.getUserByEmail(email)
            ?: return Result.failure(Exception("Неверный email или пароль"))

        val saltBytes = Base64.getDecoder().decode(user.salt)
        val isCorrect = PasswordHasher.verify(password, saltBytes, user.passwordHash)

        if (!isCorrect) {
            return Result.failure(Exception("Неверный email или пароль"))
        }

        sessionManager.login(user.id)
        return Result.success(Unit)
    }

    fun logout() {
        sessionManager.logout()
    }

    fun isLoggedIn() = sessionManager.isLoggedIn()

    fun getCurrentUserId() = sessionManager.getCurrentUserId()

    suspend fun getCurrentUser(): User? {
        val id = sessionManager.getCurrentUserId()
        return if (id != -1) userDao.getUserById(id) else null
    }
}