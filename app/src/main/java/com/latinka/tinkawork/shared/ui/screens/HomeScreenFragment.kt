package com.latinka.tinkawork.shared.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.latinka.tinkawork.databinding.FragmentHomeScreenBinding

class HomeScreenFragment : Fragment() {

    private lateinit var binding: FragmentHomeScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.entryButton.setOnClickListener {
        }
    }

    companion object {
        fun newInstance() : HomeScreenFragment = HomeScreenFragment()
    }
}