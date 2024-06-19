package com.latinka.tinkawork.account.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.latinka.tinkawork.R

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
            view.findNavController().navigate(R.id.action_accountOptionsScreenFragment_to_personalInformationScreenFragment)
        }
    }
}