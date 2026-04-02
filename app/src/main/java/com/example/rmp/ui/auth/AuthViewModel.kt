package com.example.rmp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _state.value = AuthState.Error("Заполните все поля")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val result = repository.login(email, password)
            if (result.isSuccess)  {
                _state.value = AuthState.Success
            } else {
                _state.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Ошибка")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _state.value = AuthState.Error("Заполните все поля")
            return
        }
        if (password.length < 6) {
            _state.value = AuthState.Error("Пароль минимум 6 символов")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val result = repository.register(username, email, password)
            _state.value = if (result.isSuccess) AuthState.Success
            else AuthState.Error(result.exceptionOrNull()?.message ?: "Ошибка")
        }
    }

    fun logout() {
        repository.logout()
        _state.value = AuthState.Idle
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}