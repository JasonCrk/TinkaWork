package com.latinka.tinkawork.account.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.FragmentAccountOptionsScreenBinding

class AccountOptionsScreenFragment : Fragment() {

    private lateinit var binding: FragmentAccountOptionsScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountOptionsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val currentUser = auth.currentUser
        val db = Firebase.firestore

        binding.personalInformationBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_accountOptionsScreenFragment_to_personalInformationScreenFragment)
        }

        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userData = document.data
                        binding.accountFirstName.text = userData?.get("first name").toString()
                        binding.accountLastName.text = userData?.get("last name").toString()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                }
        }
    }
}