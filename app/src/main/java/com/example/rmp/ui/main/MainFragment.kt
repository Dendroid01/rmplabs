package com.example.rmp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.rmp.R
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        btnLogout.setOnClickListener {
            viewModel.logout()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is MainState.Idle -> {}
                        is MainState.UserLoaded -> {
                            tvWelcome.text = "Привет, ${state.user.username}! 🎵"
                        }

                        is MainState.LoggedOut -> {
                            findNavController().navigate(R.id.action_main_to_login)
                        }
                    }
                }
            }

            viewModel.loadCurrentUser()
        }
    }
}