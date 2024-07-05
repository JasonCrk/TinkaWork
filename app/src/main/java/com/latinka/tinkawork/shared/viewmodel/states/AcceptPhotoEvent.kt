package com.latinka.tinkawork.shared.viewmodel.states

sealed class AcceptPhotoEvent {
    data object Loading: AcceptPhotoEvent()
    data class Success(val message: String): AcceptPhotoEvent()
    data class Error(val message: String): AcceptPhotoEvent()
    data object ErrorGps: AcceptPhotoEvent()
}