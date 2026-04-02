package com.example.rmp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmp.data.storage.AppDatabase
import com.example.rmp.session.SessionManager
import com.example.rmp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MainState {
    object Idle : MainState()
    data class UserLoaded(val user: User) : MainState()
    object LoggedOut : MainState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val db = AppDatabase.getInstance(application)

    private val _state = MutableStateFlow<MainState>(MainState.Idle)
    val state: StateFlow<MainState> = _state

    init{
        loadCurrentUser()
    }
    fun loadCurrentUser() {
        val currentId = sessionManager.getCurrentUserId()
        if (currentId == -1) {
            _state.value = MainState.LoggedOut
            return
        }

        viewModelScope.launch {
            val user = db.userDao().getUserById(currentId)
            if (user != null) {
                _state.value = MainState.UserLoaded(user)
            } else {
                sessionManager.logout()
                _state.value = MainState.LoggedOut
            }
        }
    }

    fun logout() {
        sessionManager.logout()
        _state.value = MainState.LoggedOut
    }
}