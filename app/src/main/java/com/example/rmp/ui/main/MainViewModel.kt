package com.example.rmp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmp.data.storage.AppDatabase
import com.example.rmp.session.SessionManager
import com.example.rmp.data.model.User
import com.example.rmp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MainState {
    object Loading : MainState()
    data class UserLoaded(val user: User) : MainState()
    object LoggedOut : MainState()
    object Idle : MainState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)

    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state: StateFlow<MainState> = _state

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _state.value = MainState.Loading
            val user = repository.getCurrentUser()
            _state.value = if (user != null) {
                MainState.UserLoaded(user)
            } else {
                MainState.LoggedOut
            }
        }
    }

    fun logout() {
        repository.logout()
        _state.value = MainState.LoggedOut
    }
}