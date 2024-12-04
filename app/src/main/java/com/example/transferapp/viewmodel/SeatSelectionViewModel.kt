package com.example.transferapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.model.MultipleReservationsRequest
import com.example.transferapp.data.model.ReservationResponse
import com.example.transferapp.data.model.SeatStatusResponse
import com.example.transferapp.repository.SeatSelectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SeatSelectionViewModel(private val repository: SeatSelectionRepository, private val apiService: ApiService) : ViewModel() {
    private val _seatStatus = MutableStateFlow<SeatStatusResponse?>(null)
    val seatStatus: StateFlow<SeatStatusResponse?> = _seatStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchSeatStatus(unitId: String, pickupTime: String, reservationDate: String, hotelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("Fetching seat status for unitId=$unitId, pickupTime=$pickupTime, reservationDate=$reservationDate, hotelId=$hotelId")
                val response = repository.getSeatStatus(unitId, pickupTime, reservationDate, hotelId)
                _seatStatus.value = response
            } catch (e: Exception) {
                _seatStatus.value = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }


    private val _reservationResponse = MutableStateFlow<ReservationResponse?>(null)
    val reservationResponse: StateFlow<ReservationResponse?> = _reservationResponse

    fun createMultipleReservations(request: MultipleReservationsRequest) {
        viewModelScope.launch {
            try {
                val response = apiService.addMultipleReservations(request)
                _reservationResponse.value = response
            } catch (e: Exception) {
                _reservationResponse.value = ReservationResponse(success = false, message = e.message ?: "Error")
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
