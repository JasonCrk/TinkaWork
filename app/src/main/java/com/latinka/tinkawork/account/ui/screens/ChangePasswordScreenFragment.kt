package com.latinka.tinkawork.account.ui.screens

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore

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

        val auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore
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
                    txtFirstname.text = documentSnapshot.getString("first_name")
                    txtLastName.text = documentSnapshot.getString("last_name")
                }
                fun obtenerNombreRol(userId: String): Task<String> {
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
                obtenerNombreRol(uidUser).addOnSuccessListener { rol ->
                    txtRoles.text = rol
                }
            }

        btnChangePassword.setOnClickListener {
            val passworActual = txrPassword.text.toString()
            val valuepass = txtNewPassword.text.toString()
            if (valuepass.isNotEmpty()) {
                val credentials = EmailAuthProvider.getCredential(email, passworActual)

                currentUser?.reauthenticate(credentials)?.addOnCompleteListener { reAuth ->
                    if (reAuth.isSuccessful) {
                        currentUser.updatePassword(valuepass).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("Operacion exitosa")
                                    .setMessage( "Contraseña actualizada ")
                                    .setPositiveButton("Aceptar") { dialog, which ->
                                        parentFragmentManager.beginTransaction()
                                            .replace(R.id.nav_container, AccountOptionsScreenFragment())
                                            .addToBackStack(null)
                                            .commit()
                                        dialog.dismiss()
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
                            .setPositiveButton("Intentar de nuevo") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setNegativeButton("Salir"){ _, _ ->
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.nav_container, AccountOptionsScreenFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }
            } else
                Toast.makeText(context, "La contraseña es requerida", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        fun newInstance() : ChangePasswordScreenFragment = ChangePasswordScreenFragment()
    }
}