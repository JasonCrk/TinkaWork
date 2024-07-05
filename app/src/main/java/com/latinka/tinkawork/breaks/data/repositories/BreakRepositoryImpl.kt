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
import java.util.TimeZone

class BreakRepositoryImpl : BreakRepository {

    private val db = FirebaseFirestore.getInstance()

    override fun getByWorkday(
        entryTime: Calendar,
        timeRecordRef: DocumentReference
    ): Task<QuerySnapshot> {
        val peruTimezone = TimeZone.getTimeZone("America/Lima")

        val endToday = Calendar.getInstance(peruTimezone).apply {
            set(Calendar.DAY_OF_MONTH, entryTime.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val breaks = db.collection(FireStoreCollections.BREAKS)
            .whereEqualTo(BreakFields.TIME_RECORD, timeRecordRef)
            .whereGreaterThanOrEqualTo(BreakFields.CREATED_AT, Timestamp(entryTime.time))
            .whereLessThanOrEqualTo(BreakFields.CREATED_AT, Timestamp(endToday.time))
            .orderBy(BreakFields.CREATED_AT, Query.Direction.ASCENDING)
            .limit(2)

        return breaks.get()
    }

    override fun findByTimeRecord(timeRecordRef: DocumentReference): Task<QuerySnapshot> {
        return db.collection(FireStoreCollections.BREAKS)
            .whereEqualTo(BreakFields.TIME_RECORD, timeRecordRef)
            .orderBy(BreakFields.CREATED_AT, Query.Direction.ASCENDING)
            .limit(1)
            .get()
    }

    override fun create(data: HashMap<String, Any>): Task<Void> {
        return db.collection(FireStoreCollections.BREAKS).document().set(data)
    }
}