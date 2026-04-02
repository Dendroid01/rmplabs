package com.example.rmp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import com.example.rmp.R
import com.example.rmp.data.storage.AppDatabase
import com.example.rmp.session.SessionManager
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        db    = AppDatabase.getInstance(requireContext())

        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val currentId = sessionManager.getCurrentUserId()

        lifecycleScope.launch {
            val user = if (currentId != -1) {
                db.userDao().getUserById(currentId)
            } else {
                null
            }
            tvWelcome.text = "Привет, ${user?.username ?: "пользователь"}! 🎵"

            if (user == null) {
                sessionManager.logout()
                findNavController().navigate(R.id.action_main_to_login)
                return@launch
            }
        }



        btnLogout.setOnClickListener {
            sessionManager.logout()
            findNavController().navigate(R.id.action_main_to_login)
        }
    }
}