package com.latinka.tinkawork.auth.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope

import com.latinka.tinkawork.R
import com.latinka.tinkawork.auth.viewmodel.LoginScreenViewModel
import com.latinka.tinkawork.auth.viewmodel.events.LoginFormEvent
import com.latinka.tinkawork.auth.viewmodel.events.LoginScreenEvent
import com.latinka.tinkawork.databinding.ActivityLoginBinding
import com.latinka.tinkawork.shared.ui.screens.MainActivity
import com.latinka.tinkawork.shared.ui.utils.UIMode

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

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

            binding.signInBtn.setOnClickListener { onEvent(LoginFormEvent.Submit) }

            binding.forgetPasswordBtn.setOnClickListener {
                startActivity(Intent(applicationContext, RestorePasswordActivity::class.java))
            }
        }

        onListeningErrorFields()
        formEvents()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun onListeningErrorFields() {
        CoroutineScope(Dispatchers.Main).launch {
            loginViewModel.loginScreenState.collectLatest { state ->
                binding.apply {
                    emailFieldLayout.helperText = state.emailError
                    passwordFieldLayout.helperText = state.passwordError
                }
            }
        }
    }

    private fun formEvents() {
        lifecycleScope.launch {
            loginViewModel.loginScreenEvent.collectLatest { event ->
                when (event) {
                    LoginScreenEvent.Loading -> {
                        binding.signInBtn.isEnabled = false
                        binding.forgetPasswordBtn.isEnabled = false

                        binding.signInBtn.background.setTint(
                            ContextCompat.getColor(applicationContext, R.color.gray)
                        )
                    }
                    LoginScreenEvent.Success -> {
                        startActivity(
                            Intent(applicationContext, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }
                    is LoginScreenEvent.Error -> {
                        Toast.makeText(applicationContext, event.errorMessage, Toast.LENGTH_LONG).show()
                    }
                    LoginScreenEvent.Completed -> {
                        binding.signInBtn.isEnabled = true
                        binding.forgetPasswordBtn.isEnabled = true

                        binding.signInBtn.background.setTint(
                            ContextCompat.getColor(
                                applicationContext,
                                if (UIMode.isDarkMode(applicationContext)) R.color.yellow else R.color.green
                            )
                        )
                    }
                }
            }
        }
    }
}