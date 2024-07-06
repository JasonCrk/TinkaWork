package com.latinka.tinkawork.timeRecord.viewmodel.events

import com.google.firebase.firestore.QueryDocumentSnapshot

sealed class HistoryScreenEvent {
    data object Loading: HistoryScreenEvent()
    data class Success(val groups: Map<String, List<QueryDocumentSnapshot>>): HistoryScreenEvent()
    data class Error(val message: String): HistoryScreenEvent()
}