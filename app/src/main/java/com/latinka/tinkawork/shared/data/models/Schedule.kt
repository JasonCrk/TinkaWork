package com.latinka.tinkawork.shared.data.models

data class Schedule(
    @field:JvmField
    val breakDuration: Map<String, Int> = emptyMap(),
    val departureTime: Map<String, Int> = emptyMap(),
    val entryTime: Map<String, Int> = emptyMap(),
    val daysOff: List<Int> = emptyList()
)