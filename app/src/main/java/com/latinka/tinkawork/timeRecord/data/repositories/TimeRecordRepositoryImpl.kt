package com.latinka.tinkawork.timeRecord.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

import com.latinka.tinkawork.shared.data.db.FireStoreCollections
import com.latinka.tinkawork.timeRecord.data.db.TimeRecordFields

import java.util.Calendar
import java.util.TimeZone

class TimeRecordRepositoryImpl : TimeRecordRepository {

    private val db = Firebase.firestore

    override fun getByWorkday(entryTime: Calendar, userRef: DocumentReference, isEntry: Boolean)
        : Task<QuerySnapshot> {
        val peruTimezone = TimeZone.getTimeZone("America/Lima")

        val endToday = Calendar.getInstance(peruTimezone).apply {
            set(Calendar.DAY_OF_MONTH, entryTime.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        return db.collection(FireStoreCollections.TIME_RECORDS)
            .whereEqualTo(TimeRecordFields.USER, userRef)
            .whereEqualTo(TimeRecordFields.IS_ENTRY, isEntry)
            .whereGreaterThanOrEqualTo(TimeRecordFields.CREATED_AT, Timestamp(entryTime.time))
            .whereLessThanOrEqualTo(TimeRecordFields.CREATED_AT, Timestamp(endToday.time))
            .limit(1)
            .get()
    }

    override fun getById(id: String): Task<DocumentSnapshot> {
        return db.collection(FireStoreCollections.TIME_RECORDS).document(id).get()
    }

    override fun create(data: HashMap<String, Any>): Task<Void> {
        return db.collection(FireStoreCollections.TIME_RECORDS).document().set(data)
    }
}