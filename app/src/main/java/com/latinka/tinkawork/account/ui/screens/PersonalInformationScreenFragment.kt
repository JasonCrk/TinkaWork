package com.latinka.tinkawork.account.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.FragmentPersonalInformationScreenBinding

import java.text.SimpleDateFormat
import java.util.Locale

class PersonalInformationScreenFragment: Fragment() {

    private lateinit var binding: FragmentPersonalInformationScreenBinding

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

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

        val currentUser = auth.currentUser
        val uidUser = currentUser?.uid

        val userRef = uidUser?.let { db.collection("users").document(it) }

        userRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {

                    binding.apply {
                        accountFirstName.text = documentSnapshot.getString("firstName")
                        accountLastName.text = documentSnapshot.getString("lastName")
                        accountDni.text = documentSnapshot.getString("dni")
                        accountNumber.text = documentSnapshot.get("phoneNumber").toString()
                        accountAddress.text = documentSnapshot.getString("address")
                        accountBirthday.text = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es", "ES"))
                            .format(documentSnapshot.getTimestamp("birthdate")!!.toDate().time)
                        accountMail.text = documentSnapshot.getString("email")
                    }

                    getRole(uidUser).addOnSuccessListener { role ->
                        binding.accountRole.text = role
                    }
                } else{
                    binding.accountRole.text = "Documento de usuario no encontrado"
                }
            }

        binding.accountInfo.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_container, AccountOptionsScreenFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getRole(userId: String): Task<String> {
        val userRef = db.collection("users").document(userId)

        return userRef.get().continueWithTask { task ->
            val rolRef = task.result?.get("role") as? DocumentReference?
            rolRef?.get()?.continueWith { rolTask ->
                rolTask.result?.getString("name") ?: "Rol no encontrado"
            } ?: Tasks.forResult("Rol no asignado")
        }
    }

    companion object {
        fun newInstance() : PersonalInformationScreenFragment = PersonalInformationScreenFragment()
    }
}