

package com.example.transferapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.model.MultipleReservationsRequest
import com.example.transferapp.data.model.ReservationResponse
import com.example.transferapp.data.model.SeatStatusResponse
import com.example.transferapp.repository.SeatSelectionRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SeatSelectionViewModel(
    private val repository: SeatSelectionRepository,
    private val apiService: ApiService
) : ViewModel() {

    companion object {
        private const val TAG = "SeatSelectionViewModel"
    }

    private val _agencyName = MutableStateFlow<String?>(null)
    val agencyName: StateFlow<String?> = _agencyName

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val _seatStatus = MutableStateFlow<SeatStatusResponse?>(null)
    val seatStatus: StateFlow<SeatStatusResponse?> = _seatStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _reservationResponse = MutableStateFlow<ReservationResponse?>(null)
    val reservationResponse: StateFlow<ReservationResponse?> = _reservationResponse

    fun fetchSeatStatus(unitId: String, pickupTime: String, reservationDate: String, hotelId: String) {
        if (_isLoading.value) {
            Log.w(TAG, "fetchSeatStatus está siendo ignorado porque ya hay una operación en curso.")
            return
        }

        Log.d(TAG, "fetchSeatStatus llamado con unitId=$unitId, pickupTime=$pickupTime, reservationDate=$reservationDate, hotelId=$hotelId")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getSeatStatus(unitId, pickupTime, reservationDate, hotelId)
                Log.d(TAG, "Received seat status response: ${gson.toJson(response)}")
                _seatStatus.value = response
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching seat status", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearReservationResponse() {
        _reservationResponse.value = null
    }

    fun createMultipleReservations(request: MultipleReservationsRequest) {
        // Log para verificar que se llama el método con el contenido de la solicitud
        Log.d(TAG, "createMultipleReservations - Llamada iniciada con request: ${gson.toJson(request)}")

        // Contador para identificar cuántas veces se ejecuta este método (opcional si quieres rastrear duplicados)
        val callId = System.currentTimeMillis() // Usamos un timestamp único para rastrear cada llamada

        viewModelScope.launch {
            // Indicamos que el proceso está cargando
            _isLoading.value = true
            Log.d(TAG, "[$callId] Cargando...")

            try {
                // Log para indicar que estamos realizando la llamada a la API
                Log.d(TAG, "[$callId] Realizando llamada a la API para crear múltiples reservaciones")

                // Llamada a la API
                val response = repository.addMultipleReservations(request)
                Log.d(TAG, "[$callId] Respuesta cruda recibida de la API: ${gson.toJson(response)}")

                // Verificación del tipo de dato `data` en la respuesta y manejo adecuado
                if (response.data.isNotEmpty()) {
                    Log.d(TAG, "[$callId] Reservación creada con éxito: ${response.data}")
                } else {
                    Log.e(TAG, "[$callId] `data` está vacío.")
                }

                // Actualizamos el estado con la respuesta procesada
                _reservationResponse.value = response

            } catch (e: Exception) {
                // Log detallado del error
                Log.e(TAG, "[$callId] Error al crear múltiples reservaciones", e)
                if (e is HttpException) {
                    // Log para mostrar el cuerpo de error HTTP si lo hay
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e(TAG, "[$callId] Cuerpo de Error HTTP: $errorBody")
                }

                // Asignamos una respuesta vacía en caso de error
                _reservationResponse.value = ReservationResponse(
                    success = false,
                    message = e.message ?: "Error desconocido",
                    data = ""
                )
            } finally {
                // Indicamos que la carga ha terminado
                _isLoading.value = false
                Log.d(TAG, "[$callId] Proceso finalizado para createMultipleReservations")
            }
        }
    }

    fun fetchAgencyName(agencyId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getAgencyNameById(agencyId)
                Log.d("ViewModel", "Nombre de la agencia obtenido del repositorio: $response")
                _agencyName.value = response
                Log.d("ViewModel", "Estado actualizado: AgencyName = ${_agencyName.value}")
            } catch (e: Exception) {
                Log.e("ViewModel", "Error al obtener el nombre de la agencia", e)
                _agencyName.value = "Error al cargar"
            }
        }
    }

    fun fetchUserName(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getUserNameById(userId)
                Log.d("ViewModel", "Nombre del usuario obtenido del repositorio: $response")
                _userName.value = response
                Log.d("ViewModel", "Estado actualizado: UserName = ${_userName.value}")
            } catch (e: Exception) {
                Log.e("ViewModel", "Error al obtener el nombre del usuario", e)
                _userName.value = "Error al cargar"
            }
        }
    }
}

class SeatSelectionViewModelFactory(
    private val repository: SeatSelectionRepository,
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeatSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SeatSelectionViewModel(repository, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
