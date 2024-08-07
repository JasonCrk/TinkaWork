package com.latinka.tinkawork.shared.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.latinka.tinkawork.databinding.ActivitySmileAtTheCameraBinding

class SmileAtTheCameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmileAtTheCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySmileAtTheCameraBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.takeAPhotoButton.setOnClickListener {
            startActivity(
                Intent(applicationContext, CameraActivity::class.java).apply {
                    putExtra("establishmentId", intent.getStringExtra("establishmentId"))
                    putExtra("timeRecordId", intent.getStringExtra("timeRecordId"))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}