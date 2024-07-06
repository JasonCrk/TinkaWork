package com.latinka.tinkawork.timeRecord.data.models

import java.time.Month

data class AttendanceGroup(
    val month: Month,
    val year: Int,
    val assistants: List<TimeRecord>
)
