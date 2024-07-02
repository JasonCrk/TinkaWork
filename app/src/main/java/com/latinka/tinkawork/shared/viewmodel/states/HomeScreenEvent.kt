package com.latinka.tinkawork.shared.viewmodel.states

import com.latinka.tinkawork.timeRecord.data.models.WorkingStatus

sealed class HomeScreenEvent {
    data class Error(val message: String) : HomeScreenEvent()
    data class Success(val status: WorkingStatus) : HomeScreenEvent()
    data object Loading : HomeScreenEvent()
}