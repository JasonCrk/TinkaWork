package com.latinka.tinkawork.breaks.data.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

data class Break(
    val createdAt: com.google.firebase.Timestamp? = null,
    @field:JvmField
    val isStart: Boolean? = null,
    val location: GeoPoint? = null,
    val photoUrl: String? = null,
    val timeRecord: DocumentReference? = null
)
