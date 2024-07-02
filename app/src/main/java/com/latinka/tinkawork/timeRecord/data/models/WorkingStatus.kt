package com.latinka.tinkawork.timeRecord.data.models

enum class WorkingStatus(val fullName: String) {
    DAY_OFF("Día libre"),
    EARLY("Temprano"),
    ENABLE_TO_WORK("Listo para trabajar"),
    WORKING("Trabajando"),
    RELAXING("Descansando"),
    WORKING_AFTER_RELAXING(WORKING.fullName),
    ENABLE_TO_DEPARTURE(WORKING.fullName),
    WORK_COMPLETED("Jornada completada"),
}