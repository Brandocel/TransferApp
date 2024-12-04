package com.example.transferapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.model.MultipleReservationsRequest
import com.example.transferapp.data.model.ReservationResponse
import com.example.transferapp.data.model.ReservationResponseItem
import com.example.transferapp.data.model.SeatStatusResponse
import com.example.transferapp.repository.SeatSelectionRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
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

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val _seatStatus = MutableStateFlow<SeatStatusResponse?>(null)
    val seatStatus: StateFlow<SeatStatusResponse?> = _seatStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _reservationResponse = MutableStateFlow<ReservationResponse?>(null)
    val reservationResponse: StateFlow<ReservationResponse?> = _reservationResponse

    fun fetchSeatStatus(unitId: String, pickupTime: String, reservationDate: String, hotelId: String) {
        Log.d(TAG, "fetchSeatStatus called with unitId=$unitId, pickupTime=$pickupTime, reservationDate=$reservationDate, hotelId=$hotelId")
        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Loading started for fetchSeatStatus")
            try {
                Log.d(TAG, "Fetching seat status from repository")
                val response = repository.getSeatStatus(unitId, pickupTime, reservationDate, hotelId)
                Log.d(TAG, "Received seat status response: ${gson.toJson(response)}")
                _seatStatus.value = response
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching seat status", e)
                _seatStatus.value = null
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Loading finished for fetchSeatStatus")
            }
        }
    }

    fun clearReservationResponse() {
        _reservationResponse.value = null
    }


    fun createMultipleReservations(request: MultipleReservationsRequest) {
        Log.d(TAG, "createMultipleReservations called with request: ${gson.toJson(request)}")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Realizar la llamada a la API
                val response = apiService.addMultipleReservations(request)
                Log.d(TAG, "Raw response: ${gson.toJson(response)}")

                // Manejar dinámicamente si `data` es un array o un objeto
                val reservationItems: List<ReservationResponseItem> = when {
                    response.data.isJsonArray -> {
                        // Si `data` es un array
                        gson.fromJson(response.data, object : TypeToken<List<ReservationResponseItem>>() {}.type)
                    }
                    response.data.isJsonObject -> {
                        // Si `data` es un objeto, conviértelo en una lista de un solo elemento
                        listOf(gson.fromJson(response.data, ReservationResponseItem::class.java))
                    }
                    else -> {
                        // Si `data` no es ni un array ni un objeto, devuelve una lista vacía
                        Log.e(TAG, "Unexpected data type: ${response.data}")
                        emptyList()
                    }
                }

                Log.d(TAG, "Processed data: $reservationItems")
                _reservationResponse.value = ReservationResponse(
                    success = response.success,
                    message = response.message,
                    data = gson.toJsonTree(reservationItems) // Convertimos la lista de nuevo a JsonElement
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error al crear múltiples reservaciones", e)
                if (e is HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e(TAG, "Cuerpo de Error HTTP: $errorBody")
                }
                // En caso de error, asigna un JsonElement vacío
                _reservationResponse.value = ReservationResponse(
                    success = false,
                    message = e.message ?: "Error desconocido",
                    data = gson.toJsonTree(emptyList<ReservationResponseItem>()) // Devuelve una lista vacía como JsonElement
                )
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Loading finished for createMultipleReservations")
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
