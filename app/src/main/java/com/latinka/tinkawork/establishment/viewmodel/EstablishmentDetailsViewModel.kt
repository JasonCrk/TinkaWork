package com.latinka.tinkawork.establishment.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.FirebaseAuth

import com.latinka.tinkawork.account.data.models.User
import com.latinka.tinkawork.account.data.repositories.UserRepository
import com.latinka.tinkawork.establishment.data.models.City
import com.latinka.tinkawork.establishment.data.models.Department
import com.latinka.tinkawork.establishment.data.models.Establishment
import com.latinka.tinkawork.establishment.data.repositories.EstablishmentRepository
import com.latinka.tinkawork.establishment.viewmodel.states.EstablishmentDetailsEvent

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import javax.inject.Inject

@HiltViewModel
class EstablishmentDetailsViewModel @Inject constructor(
    private val establishmentRepository: EstablishmentRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _establishmentDetailsEvent = MutableSharedFlow<EstablishmentDetailsEvent>()
    val establishmentDetailsEvent: SharedFlow<EstablishmentDetailsEvent> =
        _establishmentDetailsEvent.asSharedFlow()

    private val auth = FirebaseAuth.getInstance()

    fun startActivity(establishmentId: String) {
        viewModelScope.launch {
            _establishmentDetailsEvent.emit(EstablishmentDetailsEvent.Loading)

            val authUser = auth.currentUser

            if (authUser == null) {
                _establishmentDetailsEvent.emit(EstablishmentDetailsEvent.NotAuthorized)
                return@launch
            }

            val establishmentSnapshot = withContext(Dispatchers.IO) {
                establishmentRepository.getById(establishmentId).await()
            }

            if (!establishmentSnapshot.exists()) {
                _establishmentDetailsEvent.emit(EstablishmentDetailsEvent.EstablishmentNotFound)
                return@launch
            }

            val user = withContext(Dispatchers.IO) {
                userRepository.getById(authUser.uid).await().toObject(User::class.java)
            }

            val establishment = establishmentSnapshot.toObject(Establishment::class.java)!!

            val city = withContext(Dispatchers.IO) {
                establishment.city!!.get().await().toObject(City::class.java)!!
            }

            val department = withContext(Dispatchers.IO) {
                establishment.department!!.get().await().toObject(Department::class.java)!!
            }

            val data = hashMapOf(
                "id" to establishmentSnapshot.id,
                "name" to establishment.name!!,
                "image" to establishment.image!!,
                "department" to department.name!!,
                "city" to city.name!!,
                "street" to establishment.street!!,
            )

            if (user?.establishment?.id == establishmentSnapshot.id) {
                _establishmentDetailsEvent.emit(EstablishmentDetailsEvent.Success(data))
            } else {
                _establishmentDetailsEvent.emit(EstablishmentDetailsEvent.NotWorkAtEstablishment(data))
            }
        }
    }
}