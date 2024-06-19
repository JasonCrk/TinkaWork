package com.latinka.tinkawork.auth.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.FragmentLoginScreenBinding

class LoginScreenFragment : Fragment() {

    private lateinit var binding: FragmentLoginScreenBinding

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

        binding.forgetPasswordBtn.setOnClickListener {
            navigationController.navigate(R.id.action_loginScreenFragment_to_sendEmailChangePasswordScreenFragment)
        }

        binding.signInBtn.setOnClickListener {
            navigationController.navigate(R.id.action_loginScreenFragment_to_homeScreenFragment)
        }
    }
}