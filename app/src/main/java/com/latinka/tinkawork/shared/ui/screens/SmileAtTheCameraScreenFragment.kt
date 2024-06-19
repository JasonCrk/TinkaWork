package com.latinka.tinkawork.shared.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.FragmentSmileAtTheCameraScreenBinding

class SmileAtTheCameraScreenFragment : Fragment() {

    private lateinit var binding: FragmentSmileAtTheCameraScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSmileAtTheCameraScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.takeAPhotoButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_smileAtTheCameraScreenFragment_to_acceptPhotoScreenFragment)
        }
    }
}