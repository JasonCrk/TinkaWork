package com.latinka.tinkawork.establishment.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import com.latinka.tinkawork.shared.data.db.FireStoreCollections

class EstablishmentRepositoryImpl : EstablishmentRepository {

    private val db = FirebaseFirestore.getInstance()

    override fun getById(establishmentId: String): Task<DocumentSnapshot> {
        return db.collection(FireStoreCollections.ESTABLISHMENTS).document(establishmentId).get()
    }
}