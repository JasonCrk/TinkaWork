package com.latinka.tinkawork.account.ui.screens

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import com.latinka.tinkawork.R
import com.latinka.tinkawork.databinding.FragmentChangePasswordScreenBinding

class ChangePasswordScreenFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordScreenBinding

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

        val currentUser = auth.currentUser
        val uidUser = currentUser?.uid
        val email = currentUser?.email.toString()

        val txrPassword = binding.passwordField
        val txtNewPassword = binding.newPasswordField
        val txtFirstname = binding.accountFirstName
        val txtLastName = binding.accountLastName
        val txtRoles = binding.accountRole
        val btnChangePassword = binding.btnChangePassword

        val userRef = uidUser?.let { db.collection("users").document(it) }
        userRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    txtFirstname.text = documentSnapshot.getString("firstName")
                    txtLastName.text = documentSnapshot.getString("lastName")
                }
                obtenerNombreRol(uidUser).addOnSuccessListener { rol ->
                    txtRoles.text = rol
                }
            }

        btnChangePassword.setOnClickListener {
            val currentPassword = txrPassword.text.toString()
            val newPassword = txtNewPassword.text.toString()
            if (newPassword.isNotEmpty()) {
                val credentials = EmailAuthProvider.getCredential(email, currentPassword)

                binding.btnChangePassword.isEnabled = false
                binding.btnChangePassword.background.setTint(
                    ContextCompat.getColor(requireContext(), R.color.bg_disable)
                )
                binding.btnChangePassword.background.setTint(
                    ContextCompat.getColor(requireContext(), R.color.disable_text)
                )

                currentUser?.reauthenticate(credentials)?.addOnCompleteListener { reAuth ->
                    if (reAuth.isSuccessful) {
                        currentUser.updatePassword(newPassword).addOnCompleteListener {
                            if (it.isSuccessful) {
                                binding.passwordField.text = null
                                binding.newPasswordField.text = null

                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("Operacion exitosa")
                                    .setMessage( "Contraseña actualizada ")
                                    .setPositiveButton("Aceptar") { dialog, _ ->
                                        parentFragmentManager.beginTransaction()
                                            .replace(R.id.nav_container, AccountOptionsScreenFragment.newInstance())
                                            .addToBackStack(null)
                                            .commit()
                                        dialog.dismiss()
                                    }
                                    .setOnDismissListener {
                                        parentFragmentManager.beginTransaction()
                                            .replace(R.id.nav_container, AccountOptionsScreenFragment.newInstance())
                                            .addToBackStack(null)
                                            .commit()
                                    }
                                val dialog: AlertDialog = builder.create()
                                dialog.show()
                            } else {
                                Toast.makeText(context, "Ocurrió un error", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Error")
                            .setMessage( "Contraseña Actual Incorrecta ")
                            .setPositiveButton("Salir"){ _, _ ->
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.nav_container, AccountOptionsScreenFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }

                    binding.btnChangePassword.isEnabled = true
                    binding.btnChangePassword.background.setTint(
                        ContextCompat.getColor(requireContext(), R.color.bg_disable)
                    )
                    binding.btnChangePassword.background.setTint(
                        ContextCompat.getColor(requireContext(), R.color.disable_text)
                    )
                }
            } else
                Toast.makeText(context, "La contraseña es requerida", Toast.LENGTH_SHORT).show()
        }
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

    companion object {
        fun newInstance() : ChangePasswordScreenFragment = ChangePasswordScreenFragment()
    }
}