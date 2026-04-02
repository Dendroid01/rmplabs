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
import com.example.rmp.data.storage.UserStorage
import com.example.rmp.session.SessionManager
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment() {

    private lateinit var userStorage: UserStorage
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userStorage    = UserStorage(requireContext())
        sessionManager = SessionManager(requireContext())

        val etEmail    = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin         = view.findViewById<Button>(R.id.btnLogin)
        val btnGoToRegister  = view.findViewById<Button>(R.id.btnGoToRegister)

        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверяем данные
            val savedUser = userStorage.getUser()
            if (savedUser != null && savedUser.email == email && savedUser.password == password) {
                sessionManager.login(email)
                findNavController().navigate(R.id.action_login_to_main)
            } else {
                Toast.makeText(requireContext(), "Неверный email или пароль", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }
}