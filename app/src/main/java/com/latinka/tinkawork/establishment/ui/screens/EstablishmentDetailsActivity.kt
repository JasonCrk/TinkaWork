package com.latinka.tinkawork.establishment.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope

import com.bumptech.glide.Glide

import com.latinka.tinkawork.R
import com.latinka.tinkawork.auth.ui.screens.LoginActivity
import com.latinka.tinkawork.databinding.ActivityEstablishmentDetailsBinding
import com.latinka.tinkawork.establishment.viewmodel.EstablishmentDetailsViewModel
import com.latinka.tinkawork.establishment.viewmodel.states.EstablishmentDetailsEvent
import com.latinka.tinkawork.shared.ui.screens.CameraActivity
import com.latinka.tinkawork.shared.ui.screens.MainActivity
import com.latinka.tinkawork.shared.ui.screens.SmileAtTheCameraActivity
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EstablishmentDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEstablishmentDetailsBinding

    private val establishmentDetailsViewModel: EstablishmentDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEstablishmentDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val establishmentId = intent.getStringExtra("establishmentId")

        if (establishmentId == null) {
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            return
        }

        onListeningEvents()
        establishmentDetailsViewModel.startActivity(establishmentId)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun onContinue(establishmentId: String) {
        val cameraActivityIntent = Intent(applicationContext, SmileAtTheCameraActivity::class.java).apply {
            putExtra("establishmentId", establishmentId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(cameraActivityIntent)
    }

    private fun setEstablishmentData(establishment: HashMap<String, String>) {
        binding.apply {
            establishmentCity.text = establishment["city"]
            establishmentName.text = establishment["name"]
            establishmentDepartment.text = establishment["department"]
            establishmentStreet.text = establishment["street"]

            Glide.with(establishmentImage)
                .load(establishment["image"]!!)
                .placeholder(R.drawable.bg_placeholder_img)
                .error(R.drawable.bg_placeholder_img)
                .into(establishmentImage)
        }
    }

    private fun showAlert() {
        AlertDialog.Builder(this)
            .setMessage("Usted no trabaja en este establecimiento")
            .setTitle("Alerta")
            .setPositiveButton("Volver al inicio") { dialog, _ ->
                startActivity(Intent(applicationContext, MainActivity::class.java))
                dialog.dismiss()
            }.show()
    }

    private fun onListeningEvents() {
        lifecycleScope.launch {
            establishmentDetailsViewModel.establishmentDetailsEvent.collectLatest { event ->
                binding.apply {
                    when (event) {
                        EstablishmentDetailsEvent.Loading -> {
                            continueButton.isEnabled = false
                            continueButton.setTextColor(
                                ContextCompat.getColor(applicationContext, R.color.disable_text))
                            continueButton.iconTint =
                                ContextCompat.getColorStateList(applicationContext, R.color.disable_text)
                        }
                        EstablishmentDetailsEvent.EstablishmentNotFound -> {
                            startActivity(
                                Intent(applicationContext, MainActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                        }
                        EstablishmentDetailsEvent.NotAuthorized -> {
                            startActivity(
                                Intent(applicationContext, LoginActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                        }
                        is EstablishmentDetailsEvent.NotWorkAtEstablishment -> {
                            showAlert()
                            setEstablishmentData(event.establishment)

                            continueButton.isEnabled = true
                            continueButton.setOnClickListener { showAlert() }
                            cancelButton.setOnClickListener {
                                startActivity(
                                    Intent(applicationContext, MainActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                )
                            }
                        }
                        is EstablishmentDetailsEvent.Success -> {
                            setEstablishmentData(event.establishment)

                            continueButton.isEnabled = true
                            continueButton.setTextColor(
                                ContextCompat.getColor(applicationContext, R.color.white))
                            continueButton.iconTint = ContextCompat.getColorStateList(applicationContext, R.color.white)
                            continueButton.background.setTint(
                                ContextCompat.getColor(applicationContext, R.color.green))
                            continueButton.setOnClickListener { onContinue(event.establishment["id"]!!) }
                            cancelButton.setOnClickListener {
                                startActivity(
                                    Intent(applicationContext, MainActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}