package com.latinka.tinkawork.establishment.data.models

import com.google.firebase.firestore.DocumentReference

data class Establishment(
    val city: DocumentReference? = null,
    val department: DocumentReference? = null,
    val name: String? = null,
    val street: String? = null,
    val image: String? = null
)
