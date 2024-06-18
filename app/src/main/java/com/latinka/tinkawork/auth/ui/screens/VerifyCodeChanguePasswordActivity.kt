package com.latinka.tinkawork.auth.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.latinka.tinkawork.R

class VerifyCodeChanguePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verify_code_changue_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnVerifyCode = findViewById<Button>(R.id.btnVerifyCode)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        btnVerifyCode.setOnClickListener{
            val intent = Intent(this, ConfirmChangePasswordActivity::class.java)
            startActivity(intent)
        }
        btnCancelar.setOnClickListener{
           val inten = Intent (this, ConfirmChangePasswordActivity::class.java)
            startActivity(inten)
        }
    }
}