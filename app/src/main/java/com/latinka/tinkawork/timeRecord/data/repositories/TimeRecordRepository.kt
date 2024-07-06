package com.latinka.tinkawork.timeRecord.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

import java.util.Calendar
import java.util.Date

interface TimeRecordRepository {
    fun findEntryTimeByUser(userRef: DocumentReference): Task<QuerySnapshot>
    fun getDepartureByEntryTimeAndUser(entryTime: Date, userRef: DocumentReference): Task<QuerySnapshot>
    fun getById(id: String): Task<DocumentSnapshot>
    fun getByWorkday(entryTime: Calendar, userRef: DocumentReference, isEntry: Boolean = true)
        : Task<QuerySnapshot>
    fun create(data: HashMap<String, Any>): Task<Void>
}
