package com.latinka.tinkawork.shared.ui.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

import com.latinka.tinkawork.R
import com.latinka.tinkawork.auth.ui.screens.LoginActivity
import com.latinka.tinkawork.databinding.ActivityAcceptPhotoBinding
import com.latinka.tinkawork.shared.viewmodel.AcceptPhotoViewModel
import com.latinka.tinkawork.shared.viewmodel.events.AcceptPhotoEvent

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AcceptPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAcceptPhotoBinding
    private lateinit var locationClient: FusedLocationProviderClient

    private val auth = FirebaseAuth.getInstance()
    private val acceptPhotoViewModel : AcceptPhotoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAcceptPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (auth.currentUser == null) {
            startActivity(
                Intent(applicationContext, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            return
        }

        val photoUriString = intent.getStringExtra("photoUri")

        if (photoUriString == null) {
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            return
        }

        val photoUri = Uri.parse(photoUriString)
        binding.previewPhoto.setImageURI(photoUri)

        locationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            != MY_PERMISSIONS_REQUEST_LOCATION) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }

        val establishmentId = intent.getStringExtra("establishmentId")
        val timeRecordId = intent.getStringExtra("timeRecordId")

        onListeningEvents()

        binding.repeatPhotoButton.setOnClickListener {
            startActivity(
                Intent(applicationContext, CameraActivity::class.java).apply {
                    putExtra("establishmentId", establishmentId)
                    putExtra("timeRecordId", timeRecordId)
                }
            )
        }

        binding.acceptPhotoButton.setOnClickListener {
            if (!establishmentId.isNullOrEmpty()) {
                acceptPhotoViewModel.createTimeRecord(establishmentId, photoUri, auth.currentUser!!.uid)
            } else if (!timeRecordId.isNullOrEmpty()) {
                acceptPhotoViewModel.createBreak(timeRecordId, photoUri, auth.currentUser!!.uid)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (!(grantResults.isNotEmpty() && grantResults[0]
                            == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(applicationContext, "Se ha denegado la ubicación", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun enableGps() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun onListeningEvents() {
        lifecycleScope.launch {
            acceptPhotoViewModel.acceptPhotoEvent.collectLatest { event ->
                when (event) {
                    is AcceptPhotoEvent.Error -> {
                        AlertDialog.Builder(this@AcceptPhotoActivity)
                            .setMessage(event.message)
                            .setNeutralButton("OK") { dialog, _ -> dialog.dismiss() }
                            .setOnDismissListener {
                                startActivity(
                                    Intent(applicationContext, MainActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                )
                            }
                            .show()
                    }
                    AcceptPhotoEvent.Loading -> {
                        binding.apply {
                            disableImageButton(binding.acceptPhotoButton)
                            disableImageButton(binding.repeatPhotoButton)
                        }
                    }
                    is AcceptPhotoEvent.Success -> {
                        startActivity(
                            Intent(applicationContext, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }
                    AcceptPhotoEvent.ErrorGps -> {
                        AlertDialog.Builder(this@AcceptPhotoActivity)
                            .setMessage("Debe activar su GPS/localización")
                            .setPositiveButton("Activar GPS") { dialog, _ ->
                                enableGps()
                                dialog.dismiss()
                            }.show()

                        enableAcceptPhotoButton()
                        enableRepeatPhotoButton()
                    }
                }
            }
        }
    }

    private fun disableImageButton(button: ImageButton) {
        button.isEnabled = false
        button.background.setTint(ContextCompat.getColor(
            applicationContext,
            R.color.bg_disable
        ))
        button.drawable.setTint(ContextCompat.getColor(
            applicationContext,
            R.color.disable_text
        ))
    }

    private fun enableRepeatPhotoButton() {
        binding.repeatPhotoButton.isEnabled = true
        binding.repeatPhotoButton.background = ContextCompat.getDrawable(
            applicationContext,
            R.drawable.bg_repeat_photo_button
        )
        binding.repeatPhotoButton.drawable.setTint(ContextCompat.getColor(
            applicationContext,
            R.color.dark_yellow
        ))
    }

    private fun enableAcceptPhotoButton() {
        binding.acceptPhotoButton.isEnabled = true
        binding.acceptPhotoButton.background = ContextCompat.getDrawable(
            applicationContext,
            R.drawable.bg_accept_photo_button
        )
        binding.acceptPhotoButton.drawable.setTint(ContextCompat.getColor(
            applicationContext,
            R.color.green
        ))
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}