package com.latinka.tinkawork.timeRecord.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class TimeRecord(
    val createdAt: Timestamp? = null,
    @field:JvmField
    val isEntry: Boolean? = null,
    val user: DocumentReference? = null
)
