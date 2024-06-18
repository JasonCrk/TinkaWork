package com.latinka.tinkawork.auth.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.ActivityVerifyCodeChanguePasswordBinding

class SendEmailChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_send_email_change_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSendEmail = findViewById<Button>(R.id.sendButtonEmail)

        btnSendEmail.setOnClickListener{
            val intent = Intent(this, VerifyCodeChanguePasswordActivity::class.java)
            startActivity(intent)
        }
    }
}