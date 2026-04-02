package com.example.rmp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rmp.R
import com.example.rmp.data.storage.UserStorage
import com.example.rmp.session.SessionManager

class MainFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userStorage: UserStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        userStorage    = UserStorage(requireContext())

        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Показываем имя пользователя
        val user = userStorage.getUser()
        tvWelcome.text = "Привет, ${user?.username ?: "пользователь"}! 🎵"

        // Выход из аккаунта
        btnLogout.setOnClickListener {
            sessionManager.logout()
            findNavController().navigate(R.id.action_main_to_login)
        }
    }
}