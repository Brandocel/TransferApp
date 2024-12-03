package com.example.transferapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.transferapp.data.model.SeatStatusResponse
import com.example.transferapp.repository.SeatSelectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SeatSelectionViewModel(private val repository: SeatSelectionRepository) : ViewModel() {
    private val _seatStatus = MutableStateFlow<SeatStatusResponse?>(null)
    val seatStatus: StateFlow<SeatStatusResponse?> = _seatStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchSeatStatus(unitId: String, pickupTime: String, reservationDate: String, hotelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getSeatStatus(unitId, pickupTime, reservationDate, hotelId)
                _seatStatus.value = response
            } catch (e: Exception) {
                _seatStatus.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class SeatSelectionViewModelFactory(
private val repository: SeatSelectionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeatSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SeatSelectionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
