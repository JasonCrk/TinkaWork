package com.latinka.tinkawork.auth.ui.screens

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.FragmentLoginScreenBinding

class LoginScreenFragment : Fragment() {

    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var auth: FirebaseAuth

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
        val navigationController = view.findNavController()
        auth = Firebase.auth

        binding.forgetPasswordBtn.setOnClickListener {
            navigationController.navigate(R.id.action_loginScreenFragment_to_sendEmailChangePasswordScreenFragment)
        }

        binding.signInBtn.setOnClickListener {
            binding.signInBtn.isEnabled = false

            val disableColor = ContextCompat.getColor(requireContext(), R.color.gray)
            binding.signInBtn.backgroundTintList = ColorStateList.valueOf(disableColor)

            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { auth ->
                    navigationController.navigate(R.id.action_loginScreenFragment_to_homeScreenFragment)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Jason", Toast.LENGTH_LONG).show()
                }
        }
    }
}