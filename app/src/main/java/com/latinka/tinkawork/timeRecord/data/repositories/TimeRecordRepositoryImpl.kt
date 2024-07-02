package com.latinka.tinkawork.timeRecord.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.latinka.tinkawork.shared.data.db.FireStoreCollections
import com.latinka.tinkawork.timeRecord.data.db.TimeRecordFields
import java.util.Calendar

class TimeRecordRepositoryImpl : TimeRecordRepository {

    private val db = Firebase.firestore

    override fun getOneByTodayDate(
        today: Calendar,
        userRef: DocumentReference,
        isEntry: Boolean
    ): Task<QuerySnapshot> {
        val endToday = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, today.get(Calendar.DAY_OF_WEEK))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time

        val timeRecords = db.collection(FireStoreCollections.TIME_RECORDS)
            .whereEqualTo(TimeRecordFields.USER, userRef)
            .whereEqualTo(TimeRecordFields.IS_ENTRY, isEntry)
            .whereGreaterThanOrEqualTo(TimeRecordFields.CREATED_AT, Timestamp(today.time))
            .whereLessThan(TimeRecordFields.CREATED_AT, Timestamp(endToday))
            .orderBy(TimeRecordFields.CREATED_AT, Query.Direction.ASCENDING)
            .limit(1)

        return timeRecords.get()
    }
}