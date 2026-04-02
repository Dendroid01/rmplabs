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
import androidx.navigation.fragment.findNavController
import com.example.rmp.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import androidx.lifecycle.repeatOnLifecycle

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnGoToRegister = view.findViewById<Button>(R.id.btnGoToRegister)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        btnLogin.setOnClickListener {
            viewModel.login(
                email = etEmail.text.toString().trim(),
                password = etPassword.text.toString().trim()
            )
        }

        btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                androidx.lifecycle.Lifecycle.State.STARTED
            ) {
                viewModel.state.collect { state ->
                    when (state) {
                        is AuthState.Idle -> {
                            progressBar.visibility = View.GONE
                        }

                        is AuthState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                        }

                        is AuthState.Success -> {
                            progressBar.visibility = View.GONE
                            findNavController().navigate(R.id.action_login_to_main)
                            viewModel.resetState()
                        }

                        is AuthState.Error -> {
                            progressBar.visibility = View.GONE
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