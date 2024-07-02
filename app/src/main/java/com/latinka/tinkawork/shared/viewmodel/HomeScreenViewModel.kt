package com.latinka.tinkawork.shared.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.toObject

import com.latinka.tinkawork.breaks.data.repositories.BreakRepository
import com.latinka.tinkawork.schedule.data.models.Schedule
import com.latinka.tinkawork.shared.viewmodel.states.HomeScreenEvent
import com.latinka.tinkawork.timeRecord.data.models.WorkingStatus
import com.latinka.tinkawork.timeRecord.data.repositories.TimeRecordRepository
import com.latinka.tinkawork.account.data.models.User
import com.latinka.tinkawork.account.data.repositories.UserRepository
import com.latinka.tinkawork.breaks.data.models.Break
import com.latinka.tinkawork.shared.viewmodel.states.HomeScreenState
import com.latinka.tinkawork.timeRecord.data.models.TimeRecord

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

import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val timeRecordRepository: TimeRecordRepository,
    private val breakRepository: BreakRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val screenState: StateFlow<HomeScreenState> = _homeScreenState.asStateFlow()

    private val _homeScreenEvent = MutableSharedFlow<HomeScreenEvent>()
    val screenEvent: SharedFlow<HomeScreenEvent> = _homeScreenEvent.asSharedFlow()

    fun startHomeState() {
        viewModelScope.launch {
            _homeScreenEvent.emit(HomeScreenEvent.Loading)

            try {
                val userRef: DocumentReference

                val user = withContext(Dispatchers.IO) {
                    val response = userRepository.getUserByUid(auth.currentUser!!.uid).await()
                    userRef = response.reference
                    return@withContext response.toObject(User::class.java)
                        ?: throw Exception("El usuario no existe")
                }

                val schedule = withContext(Dispatchers.IO) {
                    user.schedule!!.get().await().toObject(Schedule::class.java)
                        ?: throw Exception("No se ha encontrado su horario")
                }

                val today = Calendar.getInstance()

                val currentDayOfWeek = today.get(Calendar.DAY_OF_WEEK)

                if (currentDayOfWeek in schedule.daysOff) {
                    _homeScreenEvent.emit(HomeScreenEvent.Success(WorkingStatus.DAY_OFF))
                    return@launch
                }

                val entryTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, schedule.entryTime["hour"]!!)
                    set(Calendar.MINUTE, schedule.entryTime["minute"]!!)
                }

                val departureTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, schedule.departureTime["hour"]!!)
                    set(Calendar.MINUTE, schedule.departureTime["minute"]!!)
                }

                val isWorkingTime = today.after(entryTime) && today.before(departureTime)

                if (isWorkingTime) {
                    onWorkingTime(today, userRef)
                } else if (today.before(entryTime)) {
                    _homeScreenEvent.emit(HomeScreenEvent.Success(WorkingStatus.EARLY))
                } else {
                    val startTimeRecordRef = withContext(Dispatchers.IO) {
                        val data = timeRecordRepository.getOneByTodayDate(today, userRef).await()
                        if (data.isEmpty) throw Exception("Ha ocurrido un error inesperado, vuelva a intentarlo más tarde")
                        return@withContext data.first()
                    }

                    val breaksTimeRecord = withContext(Dispatchers.IO) {
                        breakRepository.getBreakByTodayDate(today, startTimeRecordRef.reference)
                            .await()
                            .toObjects(Break::class.java)
                    }

                    val endTimeRecordRef = withContext(Dispatchers.IO) {
                        timeRecordRepository.getOneByTodayDate(
                            today,
                            userRef,
                            false
                        ).await()
                    }

                    var endTimeRecord: TimeRecord? = null

                    try {
                        endTimeRecord = endTimeRecordRef.first().toObject(TimeRecord::class.java)
                    } catch (e: NoSuchElementException) {
                        _homeScreenState.value = _homeScreenState.value.copy(
                            startBreakTime = breaksTimeRecord[0].createdAt!!.toDate(),
                            endBreakTime = breaksTimeRecord[1].createdAt!!.toDate(),
                        )
                        _homeScreenEvent.emit(HomeScreenEvent.Success(WorkingStatus.ENABLE_TO_DEPARTURE))
                    }

                    _homeScreenState.value = _homeScreenState.value.copy(
                        startBreakTime = breaksTimeRecord[1].createdAt!!.toDate(),
                        endBreakTime = breaksTimeRecord[1].createdAt!!.toDate(),
                        endWorkTime = endTimeRecord!!.createdAt!!.toDate()
                    )
                    _homeScreenEvent.emit(HomeScreenEvent.Success(WorkingStatus.WORK_COMPLETED))
                }
            } catch (e: Exception) {
                _homeScreenEvent.emit(HomeScreenEvent.Error(e.message.toString()))
                e.printStackTrace()
                Log.e("ERROR_HOME_SCREEN", e.toString())
            }
        }
    }

    private suspend fun onWorkingTime(today: Calendar, userRef: DocumentReference) {
        val entryTimeRecordResponse = withContext(Dispatchers.IO) {
            timeRecordRepository.getOneByTodayDate(today, userRef).await()
        }

        val startTimeRecordRef: DocumentReference

        try {
            startTimeRecordRef = entryTimeRecordResponse.first().reference
        } catch (e: NoSuchElementException) {
            _homeScreenEvent.emit(HomeScreenEvent.Success(WorkingStatus.ENABLE_TO_WORK))
            return
        }

        val timeRecordBreaks = withContext(Dispatchers.IO) {
            breakRepository.getBreakByTodayDate(today, startTimeRecordRef).await().toObjects(Break::class.java)
        }

        if (timeRecordBreaks.isEmpty()) {
            _homeScreenEvent.emit(HomeScreenEvent.Success(WorkingStatus.WORKING))
            return
        }

        when (timeRecordBreaks.size) {
            1 -> { _homeScreenEvent.emit(HomeScreenEvent.Success(WorkingStatus.RELAXING)) }
            2 -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    startBreakTime = timeRecordBreaks[1].createdAt!!.toDate(),
                    endBreakTime = timeRecordBreaks[1].createdAt!!.toDate()
                )
                _homeScreenEvent.emit(HomeScreenEvent.Success(WorkingStatus.WORKING_AFTER_RELAXING))
            }
            else -> {
                val message = "El usuario ${userRef.id} tiene más de un descanso"
                Log.e("HomeScreenFragment", message)
                throw Exception("Ha ocurrido un error inesperado, vuelva a intentarlo más tarde")
            }
        }
    }
}
