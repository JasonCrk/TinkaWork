package com.latinka.tinkawork.timeRecord.viewmodel.events

sealed class AssistanceDetailsScreenEvent {
    data object Loading: AssistanceDetailsScreenEvent()
    data object Success: AssistanceDetailsScreenEvent()
    data class Error(val message: String): AssistanceDetailsScreenEvent()
}