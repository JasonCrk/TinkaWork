package com.latinka.tinkawork.auth.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.FragmentLoginScreenBinding

class LoginScreenFragment : Fragment() {

    private lateinit var binding: FragmentLoginScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInBtn.setOnClickListener {
            view.findNavController().popBackStack()
            view.findNavController().navigate(R.id.homeScreenFragment)
        }

        val btnForgetPassword = view.findViewById<Button>(R.id.btnForgetPassword)

        btnForgetPassword.setOnClickListener {
            val intent = Intent(activity, SendEmailChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }
}