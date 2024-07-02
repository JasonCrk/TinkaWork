package com.latinka.tinkawork.account.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

interface UserRepository {
    fun getUserByUid(userUid: String): Task<DocumentSnapshot>
}