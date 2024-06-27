package com.latinka.tinkawork.account.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.FragmentChangePasswordScreenBinding

class ChangePasswordScreenFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToAccountOptionsBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_container, AccountOptionsScreenFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        fun newInstance() : ChangePasswordScreenFragment = ChangePasswordScreenFragment()
    }
}