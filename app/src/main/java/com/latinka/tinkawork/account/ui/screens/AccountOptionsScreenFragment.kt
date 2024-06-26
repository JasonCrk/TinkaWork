package com.latinka.tinkawork.account.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.google.firebase.Firebase
import com.google.firebase.auth.auth

import com.latinka.tinkawork.R
import com.latinka.tinkawork.auth.ui.screens.LoginActivity
import com.latinka.tinkawork.databinding.FragmentAccountOptionsScreenBinding

class AccountOptionsScreenFragment : Fragment() {

    private lateinit var binding: FragmentAccountOptionsScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountOptionsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.personalInformationBtn.setOnClickListener {
        }

        binding.signOutBtn.setOnClickListener {
            binding.signOutBtn.isEnabled = false
            binding.personalInformationBtn.isEnabled = false

            binding.signOutBtn.background.setTint(
                ContextCompat.getColor(requireContext(), R.color.gray)
            )

            Firebase.auth.signOut()

            startActivity(
                Intent(requireContext(), LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

    companion object {
        fun newInstance() : AccountOptionsScreenFragment = AccountOptionsScreenFragment()
    }
}