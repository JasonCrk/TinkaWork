package com.latinka.tinkawork.shared.viewmodel.states

import java.util.Date

data class HomeScreenState(
    val startBreakTime: Date? = null,
    val endBreakTime: Date? = null,
    val endWorkTime: Date? = null,
    val startTimeRecordId: String? = null,
    val startTimeRecord: Date? = null
)
