package com.latinka.tinkawork.account.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore

import com.latinka.tinkawork.R
import com.latinka.tinkawork.auth.ui.screens.LoginActivity
import com.latinka.tinkawork.databinding.FragmentAccountOptionsScreenBinding

class AccountOptionsScreenFragment : Fragment() {

    private lateinit var binding: FragmentAccountOptionsScreenBinding
    private lateinit var auth : FirebaseAuth

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
        val db = Firebase.firestore

        val currentUser = auth.currentUser
        val uidUser = currentUser?.uid
        val userRef = uidUser?.let { db.collection("users").document(it) }

        val txtFirstname = binding.accountFirstName
        val txtLastName = binding.accountLastName
        val txtRoles = binding.accountRole

        userRef?.get()
            ?.addOnSuccessListener { documentSnapshot->
                if(documentSnapshot.exists()){
                    txtFirstname.text = documentSnapshot.getString("firstName")
                    txtLastName.text  = documentSnapshot.getString("lastName")
                }
                fun obtenerNombreRol(userId: String): Task<String> {
                    val userRef = db.collection("users").document(userId)

                    return userRef.get().continueWithTask { task ->
                        val rolRef = task.result?.get("role") as? DocumentReference?
                        rolRef?.get()?.continueWith { rolTask ->
                            rolTask.result?.getString("name") ?: "Rol no encontrado"
                        }
                            ?: Tasks.forResult("Rol no asignado")
                    }
                }
                obtenerNombreRol(uidUser).addOnSuccessListener { rol ->
                    txtRoles.text = rol
                }
            }

        binding.personalInformationBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_container, PersonalInformationScreenFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnChangePassword.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_container, ChangePasswordScreenFragment())
                .addToBackStack(null)
                .commit()
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