package com.latinka.tinkawork.breaks.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.latinka.tinkawork.breaks.data.db.BreakFields
import com.latinka.tinkawork.shared.data.db.FireStoreCollections

import java.util.Calendar

class BreakRepositoryImpl : BreakRepository {

    private val db = FirebaseFirestore.getInstance()

    override fun getBreakByTodayDate(
        today: Calendar,
        timeRecordRef: DocumentReference
    ): Task<QuerySnapshot> {
        val endToday = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, today.get(Calendar.DAY_OF_WEEK))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time

        val breaks = db.collection(FireStoreCollections.BREAKS)
            .whereEqualTo(BreakFields.TIME_RECORD, timeRecordRef)
            .whereGreaterThanOrEqualTo(BreakFields.CREATED_AT, Timestamp(today.time))
            .whereLessThan(BreakFields.CREATED_AT, Timestamp(endToday))
            .limit(2)
            .orderBy(BreakFields.CREATED_AT, Query.Direction.ASCENDING)

        return breaks.get()
    }
}