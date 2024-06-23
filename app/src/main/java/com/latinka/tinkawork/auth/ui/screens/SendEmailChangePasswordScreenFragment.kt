package com.latinka.tinkawork.auth.ui.screens

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.latinka.tinkawork.R

import com.latinka.tinkawork.databinding.FragmentSendEmailChangePasswordPasswordBinding

class SendEmailChangePasswordScreenFragment : Fragment() {

    private lateinit var binding: FragmentSendEmailChangePasswordPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendEmailChangePasswordPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navigation = view.findNavController()
        auth = Firebase.auth

        binding.sendEmailToRecoveryPasswordBtn.setOnClickListener {
            binding.sendEmailToRecoveryPasswordBtn.isEnabled = false

            val disableColor = ContextCompat.getColor(requireContext(), R.color.gray)
            binding.sendEmailToRecoveryPasswordBtn.backgroundTintList = ColorStateList.valueOf(disableColor)

            val alertMessage = AlertDialog.Builder(requireContext())

            val email = binding.emailField.text.toString()

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    alertMessage
                        .setTitle("Recuperar contraseña")
                        .setMessage("Se le ha enviado un correo electrónico para que pueda cambiar su contraseña")
                        .setNeutralButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                    alertMessage.show()
                    navigation.popBackStack()
                }
        }

        binding.backToLoginBtn.setOnClickListener {
            navigation.popBackStack()
        }
    }
}