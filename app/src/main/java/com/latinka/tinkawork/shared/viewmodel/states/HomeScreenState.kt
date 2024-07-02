package com.latinka.tinkawork.shared.viewmodel.states

import java.util.Date

data class HomeScreenState(
    val startBreakTime: Date? = null,
    val endBreakTime: Date? = null,
    val endWorkTime: Date? = null
)
