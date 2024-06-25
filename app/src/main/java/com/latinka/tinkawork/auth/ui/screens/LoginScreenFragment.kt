package com.latinka.tinkawork.auth.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController

import com.latinka.tinkawork.R
import com.latinka.tinkawork.auth.viewmodel.LoginScreenViewModel
import com.latinka.tinkawork.auth.viewmodel.events.LoginFormEvent
import com.latinka.tinkawork.auth.viewmodel.events.LoginScreenEvent
import com.latinka.tinkawork.databinding.FragmentLoginScreenBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginScreenFragment : Fragment() {

    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var navigation: NavController
    private val loginViewModel: LoginScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigation = view.findNavController()

        onListeningTextFieldsChanges()
        onObserveTextFieldsChanges()
        onListeningFormEvents()

        binding.forgetPasswordBtn.setOnClickListener {
            navigation.navigate(R.id.action_loginScreenFragment_to_sendEmailChangePasswordScreenFragment)
        }
    }

    private fun onListeningTextFieldsChanges() {
        loginViewModel.apply {
            binding.emailField.doOnTextChanged { text, _, _, _ ->
                if (text?.isNotEmpty()!!) {
                    onEvent(LoginFormEvent.EmailChanged(text.toString()))
                    loginScreenState.value.emailError = null
                } else {
                    loginScreenState.value.email = ""
                }
            }

            binding.passwordField.doOnTextChanged { text, _, _, _ ->
                if (text?.isNotEmpty()!!) {
                    onEvent(LoginFormEvent.PasswordChanged(text.toString()))
                    loginScreenState.value.passwordError = null
                } else {
                    loginScreenState.value.password = ""
                }
            }

            binding.signInBtn.setOnClickListener {
                onEvent(LoginFormEvent.Submit)
            }
        }
    }

    private fun onObserveTextFieldsChanges() {
        CoroutineScope(Dispatchers.Main).launch {
            loginViewModel.loginScreenState.collectLatest {
                binding.apply {
                    emailFieldLayout.helperText = it.emailError
                    passwordFieldLayout.helperText = it.passwordError
                }
            }
        }
    }

    private fun onListeningFormEvents() {
        lifecycleScope.launch {
            loginViewModel.loginScreenEvent.collectLatest {
                when (it) {
                    LoginScreenEvent.Loading -> {
                        binding.signInBtn.isEnabled = false
                    }
                    LoginScreenEvent.Success -> {
                        navigation.navigate(R.id.action_loginScreenFragment_to_homeScreenFragment)
                        binding.signInBtn.isEnabled = true
                    }
                    is LoginScreenEvent.Error -> {}
                    LoginScreenEvent.Completed -> {
                        binding.signInBtn.isEnabled = true
                    }
                }
            }
        }
    }
}