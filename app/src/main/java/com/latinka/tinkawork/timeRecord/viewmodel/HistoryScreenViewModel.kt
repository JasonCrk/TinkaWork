package com.latinka.tinkawork.timeRecord.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QuerySnapshot

import com.latinka.tinkawork.account.data.repositories.UserRepository
import com.latinka.tinkawork.timeRecord.data.db.TimeRecordFields
import com.latinka.tinkawork.timeRecord.data.repositories.TimeRecordRepository
import com.latinka.tinkawork.timeRecord.viewmodel.events.HistoryScreenEvent

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import javax.inject.Inject

@HiltViewModel
class HistoryScreenViewModel @Inject constructor(
    private val timeRecordRepository: TimeRecordRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _historyScreenEvent = MutableSharedFlow<HistoryScreenEvent>()
    val historyScreenEvent = _historyScreenEvent.asSharedFlow()

    fun loadingAssistants(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _historyScreenEvent.emit(HistoryScreenEvent.Loading)
            }

            val userRef = userRepository.getById(userId).await().reference

            val timeRecords: QuerySnapshot
            try {
                timeRecords = timeRecordRepository.findEntryTimeByUser(userRef).await()
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _historyScreenEvent.emit(
                        HistoryScreenEvent.Error(
                            "Ha ocurrido un fallo al momento de obtener sus asistencias, vuelva a intentarlo más tarde"
                        )
                    )
                }
                return@launch
            }

            val attendanceGroups = withContext(Dispatchers.Default) {
                timeRecords.groupBy {
                    val createdAt = it.data[TimeRecordFields.CREATED_AT] as Timestamp
                    val date = Calendar.getInstance()
                    date.time = createdAt.toDate()

                    SimpleDateFormat(
                        "MMMM - yyyy",
                        Locale("es", "ES")
                    ).format(date.time)
                }
            }

            withContext(Dispatchers.Main) {
                _historyScreenEvent.emit(HistoryScreenEvent.Success(attendanceGroups))
            }
        }
    }
}