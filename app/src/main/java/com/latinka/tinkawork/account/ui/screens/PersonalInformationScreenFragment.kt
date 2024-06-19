package com.latinka.tinkawork.account.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.latinka.tinkawork.databinding.FragmentPersonalInformationScreenBinding

class PersonalInformationScreenFragment: Fragment() {

    private lateinit var binding: FragmentPersonalInformationScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonalInformationScreenBinding.inflate(inflater, container, false)
        return binding.root
    }
}