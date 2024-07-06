package com.latinka.tinkawork.breaks.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot

import java.util.Calendar

interface BreakRepository {
    fun getByWorkday(entryTime: Calendar, timeRecordRef: DocumentReference)
        : Task<QuerySnapshot>
    fun findByTimeRecord(timeRecordRef: DocumentReference) : Task<QuerySnapshot>
    fun getCompleteBreakByTimeRecord(timeRecordRef: DocumentReference): Task<QuerySnapshot>
    fun create(data: HashMap<String, Any>) : Task<Void>
}