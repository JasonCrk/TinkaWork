package com.latinka.tinkawork.breaks.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot

import java.util.Calendar

interface BreakRepository {
    fun getBreakByTodayDate(today: Calendar, timeRecordRef: DocumentReference)
        : Task<QuerySnapshot>
}