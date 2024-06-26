package com.latinka.tinkawork.auth.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope

import com.latinka.tinkawork.R
import com.latinka.tinkawork.auth.viewmodel.RestorePasswordScreenViewModel
import com.latinka.tinkawork.auth.viewmodel.events.RestorePasswordFormEvent
import com.latinka.tinkawork.auth.viewmodel.events.RestorePasswordScreenEvent
import com.latinka.tinkawork.databinding.ActivityRestorePasswordBinding
import com.latinka.tinkawork.shared.ui.utils.UIMode

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RestorePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestorePasswordBinding
    private val restorePasswordViewModel: RestorePasswordScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRestorePasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)

        restorePasswordViewModel.apply {
            binding.emailField.doOnTextChanged { text, _, _, _ ->
                if (text?.isNotEmpty()!!) {
                    onEvent(RestorePasswordFormEvent.EmailChanged(text.toString()))
                    screenState.value.emailError = null
                } else {
                    screenState.value.email = ""
                }
            }

            binding.restorePasswordBtn.setOnClickListener {
                onEvent(RestorePasswordFormEvent.Submit)
            }

            binding.backToLoginBtn.setOnClickListener {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
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
            restorePasswordViewModel.screenState.collectLatest { state ->
                binding.emailFieldLayout.helperText = state.emailError
            }
        }
    }

    private fun formEvents() {
        lifecycleScope.launch {
            restorePasswordViewModel.screenEvent.collectLatest { event ->
                when (event) {
                    RestorePasswordScreenEvent.Loading -> {
                        binding.restorePasswordBtn.isEnabled = false
                        binding.backToLoginBtn.isEnabled = false

                        binding.restorePasswordBtn.background.setTint(
                            ContextCompat.getColor(applicationContext, R.color.gray)
                        )
                    }
                    RestorePasswordScreenEvent.Success -> {
                        AlertDialog.Builder(applicationContext)
                            .setMessage(
                                "Se le ha enviado un correo electrónico con un enlace de recuperación de contraseña"
                            )
                            .setPositiveButton("Iniciar sesión") { dialog, _ ->
                                dialog.dismiss()
                                startActivity(Intent(applicationContext, LoginActivity::class.java))
                            }
                            .setNeutralButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                            }
                    }
                    is RestorePasswordScreenEvent.Error -> {
                        Toast.makeText(applicationContext, event.error, Toast.LENGTH_LONG).show()

                        binding.restorePasswordBtn.isEnabled = true
                        binding.backToLoginBtn.isEnabled = true

                        binding.restorePasswordBtn.background.setTint(
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