package com.latinka.tinkawork.shared.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.latinka.tinkawork.databinding.FragmentAssistanceDetailsScreenBinding

class AssistanceDetailsScreenFragment : Fragment() {

    private lateinit var binding: FragmentAssistanceDetailsScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssistanceDetailsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToHistoryScreenBtn.setOnClickListener {
        }
    }
}