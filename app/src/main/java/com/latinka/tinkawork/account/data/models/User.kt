package com.latinka.tinkawork.account.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class User(
    val email: String? = null,
    val dni: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val birthdate: Timestamp? = null,
    val establishment: DocumentReference? = null,
    val role: DocumentReference? = null,
    val schedule: DocumentReference? = null
)
