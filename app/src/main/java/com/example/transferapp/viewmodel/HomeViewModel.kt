package com.example.transferapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transferapp.data.model.AvailabilityResponse
import com.example.transferapp.data.model.HomeData
import com.example.transferapp.repository.HomeRepository
import com.example.transferapp.ui.home.components.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _homeData = MutableStateFlow<HomeData?>(null)
    val homeData: StateFlow<HomeData?> = _homeData

    private val _availabilityData = MutableStateFlow<AvailabilityResponse?>(null)
    val availabilityData: StateFlow<AvailabilityResponse?> = _availabilityData

    private val _isLoadingReservations = MutableStateFlow(false)
    val isLoadingReservations: StateFlow<Boolean> = _isLoadingReservations

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _reservations

    fun fetchUserReservations(userId: String) {
        viewModelScope.launch {
            _isLoadingReservations.value = true
            try {
                val response = homeRepository.getUserReservations(userId)
                if (response.success) {
                    // Usa el operador `?.` para manejar el caso en que `response.data` sea nulo
                    _reservations.value = response.data?.filter { it.status == "paid" } ?: emptyList()
                } else {
                    _reservations.value = emptyList() // En caso de fallo en la respuesta
                }
            } catch (e: Exception) {
                _reservations.value = emptyList() // En caso de error, limpiar las reservas
            } finally {
                _isLoadingReservations.value = false
            }
        }
    }



    fun fetchHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = homeRepository.fetchAllInfo()
                _homeData.value = response.data
            } catch (e: Exception) {
                // Manejo de errores
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun fetchUnitAvailability(unitId: String, pickupTime: String, reservationDate: String, hotelId:String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = homeRepository.getUnitAvailability(unitId, pickupTime, reservationDate, hotelId)
                _availabilityData.value = response
            } catch (e: Exception) {
                _availabilityData.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

}



class HomeViewModelFactory(private val homeRepository: HomeRepository) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(homeRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
