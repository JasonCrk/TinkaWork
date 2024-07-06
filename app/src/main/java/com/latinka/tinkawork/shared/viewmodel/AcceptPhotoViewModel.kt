package com.latinka.tinkawork.shared.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.latinka.tinkawork.account.data.models.User

import com.latinka.tinkawork.account.data.repositories.UserRepository
import com.latinka.tinkawork.breaks.data.repositories.BreakRepository
import com.latinka.tinkawork.establishment.data.repositories.EstablishmentRepository
import com.latinka.tinkawork.shared.data.models.Schedule
import com.latinka.tinkawork.shared.data.services.FirebaseStorageService
import com.latinka.tinkawork.shared.viewmodel.events.AcceptPhotoEvent
import com.latinka.tinkawork.timeRecord.data.repositories.TimeRecordRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class AcceptPhotoViewModel @Inject constructor(
    private val storageService: FirebaseStorageService,
    private val timeRecordRepository: TimeRecordRepository,
    private val breakRepository: BreakRepository,
    private val establishmentRepository: EstablishmentRepository,
    private val userRepository: UserRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _acceptPhotoEvent = MutableSharedFlow<AcceptPhotoEvent>()
    val acceptPhotoEvent = _acceptPhotoEvent.asSharedFlow()

    private var locationClient = LocationServices.getFusedLocationProviderClient(application)

    private fun getLocation(): Task<Location>? {
        val context = getApplication<Application>().applicationContext

        if (ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        if (!isLocationEnable()) { throw Exception() }

        return locationClient.lastLocation
    }

    private fun isLocationEnable(): Boolean {
        return getApplication<Application>().applicationContext.getSystemService(LocationManager::class.java)
            .isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun createTimeRecord(
        establishmentId: String,
        photoUri: Uri,
        userId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { _acceptPhotoEvent.emit(AcceptPhotoEvent.Loading) }

            val location: Location?
            try {
                location = withContext(Dispatchers.IO) { getLocation()?.await() }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) { _acceptPhotoEvent.emit(AcceptPhotoEvent.ErrorGps) }
                return@launch
            }

            if (location == null) {
                withContext(Dispatchers.Main) { _acceptPhotoEvent.emit(AcceptPhotoEvent.ErrorGps) }
                return@launch
            }

            val userSnapshot: DocumentSnapshot
            try {
                userSnapshot = userRepository.getById(userId).await()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _acceptPhotoEvent.emit(
                        AcceptPhotoEvent.Error("El usuaro no existe")
                    )
                }
                return@launch
            }

            val schedule = userSnapshot.toObject(User::class.java)!!.schedule!!
                .get().await().toObject(Schedule::class.java)
                    ?: throw Exception("El horario del usuario no existe")

            val peruTimezone = TimeZone.getTimeZone("America/Lima")

            val entryTime = Calendar.getInstance(peruTimezone).apply {
                set(Calendar.HOUR_OF_DAY, schedule.entryTime["hour"]!!)
                set(Calendar.MINUTE, schedule.entryTime["minute"]!!)
            }

            val establishment = establishmentRepository.getById(establishmentId).await().reference
            val entryTimeRecordExist = timeRecordRepository
                .getByWorkday(entryTime, userSnapshot.reference).await()

            val photoUploadedUri: Uri
            try {
                photoUploadedUri = storageService.uploadImage(photoUri).await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }

            val timeRecordData = hashMapOf(
                "user" to userSnapshot.reference,
                "photoUrl" to photoUploadedUri,
                "location" to GeoPoint(location.latitude, location.longitude),
                "createdAt" to Timestamp(Calendar.getInstance(peruTimezone).time),
                "establishment" to establishment,
                "isEntry" to entryTimeRecordExist.isEmpty
            )

            try {
                timeRecordRepository.create(timeRecordData)

                withContext(Dispatchers.Main) {
                    _acceptPhotoEvent.emit(
                        AcceptPhotoEvent.Success(
                            if (timeRecordData["isEntry"] as Boolean)
                                "Registro de asistencia exitosa" else "Registro de salida exitosa"
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _acceptPhotoEvent.emit(
                        AcceptPhotoEvent.Error("Ha ocurrido un problema con al intentar iniciar o culminar el registro, intente de nuevo")
                    )
                }
            }
        }
    }

    fun createBreak(timeRecordId: String, photoUri: Uri, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { _acceptPhotoEvent.emit(AcceptPhotoEvent.Loading) }

            val userRef = userRepository.getById(userId).await().reference

            val location: Location?
            try {
                location = withContext(Dispatchers.IO) { getLocation()?.await() }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) { _acceptPhotoEvent.emit(AcceptPhotoEvent.ErrorGps) }
                return@launch
            }

            if (location == null) {
                withContext(Dispatchers.Main) {
                    _acceptPhotoEvent.emit(
                        AcceptPhotoEvent.Error("Se necesita otorgar permisos para obtener su ubicación")
                    )
                }
                return@launch
            }

            val timeRecord = timeRecordRepository.getById(timeRecordId).await()

            if ((timeRecord.data?.get("user") as DocumentReference) != userRef) {
                withContext(Dispatchers.Main) {
                    _acceptPhotoEvent.emit(
                        AcceptPhotoEvent.Error("Usted no tiene permitido hacer esta acción")
                    )
                }
                return@launch
            }

            val breakExists = breakRepository.findByTimeRecord(timeRecord.reference).await()
            val photoUploadedUri = storageService.uploadImage(photoUri).await()

            val peruTimezone = TimeZone.getTimeZone("America/Lima")

            val breakData = hashMapOf(
                "photoUrl" to photoUploadedUri,
                "location" to GeoPoint(location.latitude, location.longitude),
                "createdAt" to Timestamp(Calendar.getInstance(peruTimezone).time),
                "isStart" to breakExists.isEmpty,
                "timeRecord" to timeRecord.reference
            )

            try {
                breakRepository.create(breakData).await()

                withContext(Dispatchers.Main) {
                    _acceptPhotoEvent.emit(
                        AcceptPhotoEvent.Success(
                            if (breakData["isStart"] as Boolean)
                                "Descanso iniciado" else "Descanso culminado"
                        )
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _acceptPhotoEvent.emit(
                        AcceptPhotoEvent.Error("Ha ocurrido un problema con al intentar crear o culminar el descanso, intente de nuevo")
                    )
                }
            }
        }
    }

}