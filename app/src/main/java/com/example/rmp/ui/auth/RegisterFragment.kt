package com.example.rmp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.rmp.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etUsername = view.findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val btnGoToLogin = view.findViewById<Button>(R.id.btnGoToLogin)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        btnRegister.setOnClickListener {
            viewModel.register(
                username = etUsername.text.toString().trim(),
                email = etEmail.text.toString().trim(),
                password = etPassword.text.toString().trim()
            )
        }

        btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                androidx.lifecycle.Lifecycle.State.STARTED
            ) {
                viewModel.state.collect { state ->
                    when (state) {
                        is AuthState.Idle -> {
                            progressBar.visibility = View.GONE
                            btnRegister.isEnabled = true
                        }

                        is AuthState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            btnRegister.isEnabled = false
                        }

                        is AuthState.Success -> {
                            progressBar.visibility = View.GONE
                            btnRegister.isEnabled = true
                            findNavController().navigate(R.id.action_register_to_main)
                            viewModel.resetState()
                        }

                        is AuthState.Error -> {
                            progressBar.visibility = View.GONE
                            btnRegister.isEnabled = true
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                            viewModel.resetState()
                        }
                    }
                }
            }
        }
    }
}