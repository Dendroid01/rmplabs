package com.example.rmp.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.rmp.data.model.User

class UserStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("music_app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL    = "email"
        private const val KEY_PASSWORD = "password"
    }

    fun saveUser(user: User) {
        prefs.edit()
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_PASSWORD, user.password)
            .apply()
    }

    fun getUser(): User? {
        val username = prefs.getString(KEY_USERNAME, null) ?: return null
        val email    = prefs.getString(KEY_EMAIL, null)    ?: return null
        val password = prefs.getString(KEY_PASSWORD, null) ?: return null
        return User(username, email, password)
    }

    fun isUserRegistered(): Boolean {
        return prefs.contains(KEY_EMAIL)
    }
}