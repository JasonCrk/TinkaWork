package com.latinka.tinkawork.establishment.viewmodel.states

sealed class EstablishmentDetailsEvent {
    data object Loading: EstablishmentDetailsEvent()
    data object NotAuthorized: EstablishmentDetailsEvent()
    data class NotWorkAtEstablishment(val establishment: HashMap<String, String>): EstablishmentDetailsEvent()
    data object EstablishmentNotFound: EstablishmentDetailsEvent()
    data class Success(val establishment: HashMap<String, String>): EstablishmentDetailsEvent()
}
