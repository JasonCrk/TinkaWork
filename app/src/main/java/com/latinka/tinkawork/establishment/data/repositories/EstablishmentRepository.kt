package com.latinka.tinkawork.establishment.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

interface EstablishmentRepository {
    fun getById(establishmentId: String) : Task<DocumentSnapshot>
}