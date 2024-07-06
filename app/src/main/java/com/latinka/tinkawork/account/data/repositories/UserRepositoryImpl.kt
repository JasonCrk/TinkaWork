package com.latinka.tinkawork.account.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

import com.latinka.tinkawork.shared.data.db.FireStoreCollections

class UserRepositoryImpl : UserRepository {

    private val db = Firebase.firestore

    override fun getById(id: String): Task<DocumentSnapshot> {
        return db.collection(FireStoreCollections.USERS).document(id).get()
    }
}