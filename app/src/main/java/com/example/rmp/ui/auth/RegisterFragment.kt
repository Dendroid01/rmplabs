package com.example.rmp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rmp.R
import com.example.rmp.data.model.User
import com.example.rmp.data.storage.UserStorage
import com.example.rmp.session.SessionManager
import com.google.android.material.textfield.TextInputEditText

class RegisterFragment : Fragment() {

    private lateinit var userStorage: UserStorage
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userStorage    = UserStorage(requireContext())
        sessionManager = SessionManager(requireContext())

        val etUsername = view.findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail    = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val btnRegister      = view.findViewById<Button>(R.id.btnRegister)
        val btnGoToLogin     = view.findViewById<Button>(R.id.btnGoToLogin)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Простая валидация
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(requireContext(), "Пароль минимум 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Сохраняем пользователя и создаём сессию
            val user = User(username, email, password)
            userStorage.saveUser(user)
            sessionManager.login(email)

            Toast.makeText(requireContext(), "Регистрация успешна!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_register_to_main)
        }

        btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }
}