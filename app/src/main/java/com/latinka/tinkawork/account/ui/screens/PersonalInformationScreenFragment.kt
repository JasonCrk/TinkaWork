package com.latinka.tinkawork.account.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.latinka.tinkawork.R

import com.latinka.tinkawork.databinding.FragmentPersonalInformationScreenBinding

class PersonalInformationScreenFragment: Fragment() {

    private lateinit var binding: FragmentPersonalInformationScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonalInformationScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val db = Firebase.firestore
        val currentUser = auth.currentUser
        val uidUser = currentUser?.uid

        val userRef = uidUser?.let { db.collection("users").document(it) }

        val txtFirstname = binding.accountFirstName
        val txtLastName = binding.accountLastName
        val txtRoles = binding.accountRole
        val txtDni = binding.accountDni
        val txtNumber = binding.accountNumber
        val txtAddress = binding.accountAddress
        val txtBirthday = binding.accountBirthday
        val txtMail = binding.accountMail

        val addOnSuccessListener = userRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {

                    txtFirstname.text = documentSnapshot.getString("first_name")
                    txtLastName.text = documentSnapshot.getString("last_name")
                    txtDni.text = documentSnapshot.getString("dni")
                    txtNumber.text = documentSnapshot.getString("phone_number")
                    txtAddress.text = documentSnapshot.getString("address")
                    txtBirthday.text= documentSnapshot.getString("birthdate")
                    txtMail.text = documentSnapshot.getString("email")

                    fun getRole(userId: String): Task<String> {
                        val userRef = db.collection("users").document(userId)
                        return userRef.get().continueWithTask { task ->
                            val rolRef = task.result?.get("role") as? DocumentReference?
                            if (rolRef != null) {
                                rolRef.get().continueWith { rolTask ->
                                    rolTask.result?.getString("name") ?: "Rol no encontrado"
                                }
                            } else {
                                Tasks.forResult("Rol no asignado")
                            }
                        }
                    }
                    getRole(uidUser).addOnSuccessListener { role ->
                        txtRoles.text = role
                    }
                }else{
                    txtRoles.text = "Documento de usuario no encontrado"
                }
            }
        binding.accountInfo.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_container, AccountOptionsScreenFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }
    companion object {
        fun newInstance() : PersonalInformationScreenFragment = PersonalInformationScreenFragment()
    }
}