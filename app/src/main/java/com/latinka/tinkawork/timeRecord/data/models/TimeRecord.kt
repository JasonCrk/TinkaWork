package com.latinka.tinkawork.timeRecord.data.models

import com.google.firebase.Timestamp

data class TimeRecord(
    val createdAt: Timestamp? = null,
    @field:JvmField
    val isEntry: Boolean? = null,
)
