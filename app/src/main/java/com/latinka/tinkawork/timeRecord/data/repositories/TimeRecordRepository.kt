package com.latinka.tinkawork.timeRecord.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot

import java.util.Calendar

interface TimeRecordRepository {
    fun getOneByTodayDate(today: Calendar, userRef: DocumentReference, isEntry: Boolean = true)
        : Task<QuerySnapshot>
}
