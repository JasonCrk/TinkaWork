package com.latinka.tinkawork.timeRecord.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

import com.latinka.tinkawork.account.data.repositories.UserRepository
import com.latinka.tinkawork.breaks.data.db.BreakFields
import com.latinka.tinkawork.breaks.data.repositories.BreakRepository
import com.latinka.tinkawork.timeRecord.data.db.TimeRecordFields
import com.latinka.tinkawork.timeRecord.data.models.TimeRecord
import com.latinka.tinkawork.timeRecord.data.repositories.TimeRecordRepository
import com.latinka.tinkawork.timeRecord.viewmodel.events.AssistanceDetailsScreenEvent
import com.latinka.tinkawork.timeRecord.viewmodel.states.AssistanceDetailsScreenState

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AssistanceDetailsScreenViewModel @Inject constructor(
    private val timeRecordRepository: TimeRecordRepository,
    private val userRepository: UserRepository,
    private val breakRepository: BreakRepository
) : ViewModel() {

    private val _screenState = MutableStateFlow(AssistanceDetailsScreenState())
    val screenState: StateFlow<AssistanceDetailsScreenState> = _screenState.asStateFlow()

    private val _screenEvent = MutableSharedFlow<AssistanceDetailsScreenEvent>()
    val screenEvent: SharedFlow<AssistanceDetailsScreenEvent> = _screenEvent.asSharedFlow()

    fun loadingTimeRecord(timeRecordId: String, userId: String) {
        val spanishLocale = Locale("es", "ES")
        val simpleFormatter = SimpleDateFormat("hh:mm a", spanishLocale)

        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _screenEvent.emit(AssistanceDetailsScreenEvent.Loading)
            }

            val entryTimeRecordSnapshot: DocumentSnapshot
            try {
                entryTimeRecordSnapshot = timeRecordRepository.getById(timeRecordId).await()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(LOG_NAME, e.message ?: "")
                withContext(Dispatchers.Main) {
                    _screenEvent.emit(AssistanceDetailsScreenEvent.Error(
                        "Ha ocurrido un error al tratar de obtener la asistencia, vuelva intentarlo más tarde"
                    ))
                }
                return@launch
            }

            if (!entryTimeRecordSnapshot.exists()) {
                withContext(Dispatchers.Main) {
                    _screenEvent.emit(AssistanceDetailsScreenEvent.Error("La asistencia no existe"))
                }
                return@launch
            }

            val timeRecordData = entryTimeRecordSnapshot.toObject(TimeRecord::class.java)!!

            val entryTimeDate = timeRecordData.createdAt!!.toDate()

            _screenState.value = _screenState.value.copy(
                entryTime = simpleFormatter.format(entryTimeDate),
                entryTimeFullDate = SimpleDateFormat(
                    "EEEE d 'de' MMMM 'del' yyyy",
                    spanishLocale
                ).format(entryTimeDate)
            )

            val breakTimes = breakRepository
                .getCompleteBreakByTimeRecord(entryTimeRecordSnapshot.reference).await()

            if (breakTimes.size() > 1) {
                val startBreakTime = (breakTimes.documents[0].data?.get(BreakFields.CREATED_AT) as Timestamp)
                    .toDate()

                val endBreakTime = (breakTimes.documents[1].data?.get(BreakFields.CREATED_AT) as Timestamp)
                    .toDate()

                _screenState.value = _screenState.value.copy(
                    startBreakAndEndBreakTimes =
                        "${simpleFormatter.format(startBreakTime)} - ${simpleFormatter.format(endBreakTime)}"
                )
            }

            val userRef = userRepository.getById(userId).await().reference

            try {
                val departureTimeRecord = timeRecordRepository
                    .getDepartureByEntryTimeAndUser(entryTimeDate, userRef)
                    .await()
                    .first()

                val departureTimeDate = (departureTimeRecord.data[TimeRecordFields.CREATED_AT] as Timestamp)
                    .toDate()

                _screenState.value = _screenState.value.copy(
                    departureTime = simpleFormatter.format(departureTimeDate),
                    totalWorkingTime = getTotalWorkingTime(entryTimeDate, departureTimeDate)
                )
            } catch (_: Exception) { }

            withContext(Dispatchers.Main) {
                _screenEvent.emit(AssistanceDetailsScreenEvent.Success)
            }
        }
    }

    private fun getTotalWorkingTime(entryTime: Date, departureTime: Date): String {
        val elapsedTimeInMillis = departureTime.time - entryTime.time

        val seconds = (elapsedTimeInMillis / 1000) % 60
        val minutes = (elapsedTimeInMillis / (1000 * 60)) % 60
        val hours = (elapsedTimeInMillis / (1000 * 60 * 60)) % 24

        return String.format(
            Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    companion object {
        private const val LOG_NAME = "ASSISTANCE_DETAILS_SCREEN"
    }
}