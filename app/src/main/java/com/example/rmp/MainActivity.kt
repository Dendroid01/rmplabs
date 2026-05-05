package com.example.rmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.rmp.session.SessionManager
import com.example.rmp.ui.AppNavigation
import com.example.rmp.ui.auth.AuthActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            startActivity(android.content.Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        val username = sessionManager.getSavedUsername() ?: "Пользователь"

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface {
                    AppNavigation(username = username)
                }
            }
        }
    }
}