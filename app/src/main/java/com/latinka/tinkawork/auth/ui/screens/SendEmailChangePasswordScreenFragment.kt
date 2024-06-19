package com.latinka.tinkawork.auth.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.FragmentSendEmailChangePasswordPasswordBinding

class SendEmailChangePasswordScreenFragment : Fragment() {

    private lateinit var binding: FragmentSendEmailChangePasswordPasswordBinding

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

        binding.sendEmailToRecoveryPasswordBtn.setOnClickListener {
            navigation.navigate(R.id.action_sendEmailChangePasswordScreenFragment_to_loginScreenFragment)
        }
    }
}