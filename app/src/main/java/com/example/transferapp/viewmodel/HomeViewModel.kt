package com.example.transferapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transferapp.data.model.Agency
import com.example.transferapp.data.model.AvailabilityResponse
import com.example.transferapp.data.model.HomeData
import com.example.transferapp.repository.HomeRepository
import com.example.transferapp.ui.home.components.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.transferapp.data.model.PendingReservation
import com.example.transferapp.data.model.RegisterReservationRequest

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _homeData = MutableStateFlow<HomeData?>(null)
    val homeData: StateFlow<HomeData?> = _homeData

    // Agencia
    private val _userAgency = MutableStateFlow<Agency?>(null)
    val userAgency: StateFlow<Agency?> = _userAgency

    private val _availabilityData = MutableStateFlow<AvailabilityResponse?>(null)
    val availabilityData: StateFlow<AvailabilityResponse?> = _availabilityData

    private val _isLoadingReservations = MutableStateFlow(false)
    val isLoadingReservations: StateFlow<Boolean> = _isLoadingReservations

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _reservations

    private val _pendingReservations = MutableStateFlow<List<PendingReservation>?>(null)
    val pendingReservations: StateFlow<List<PendingReservation>?> = _pendingReservations

    fun registerReservation(
        request: RegisterReservationRequest,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = homeRepository.registerReservation(request)
                if (response.success && response.data != null) {
                    onSuccess(response.data) // Aquí enviamos el folio directamente
                } else {
                    onError(response.message)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            }
        }
    }



    fun fetchPendingReservations(userId: String) {
        viewModelScope.launch {
            try {
                val response = homeRepository.getPendingReservations(userId)
                if (response.success) {
                    _pendingReservations.value = response.data
                } else {
                    _pendingReservations.value = null
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al obtener reservas pendientes: ${e.message}")
                _pendingReservations.value = null
            }
        }
    }

    // Método para inicializar datos
    fun initializeData(userId: String) {
        fetchHomeData()
        fetchUserAgency(userId)
        fetchPendingReservations(userId)
    }


    fun fetchUserReservations(userId: String) {
        viewModelScope.launch {
            _isLoadingReservations.value = true
            Log.d("HomeViewModel", "Iniciando fetchUserReservations para userId: $userId")
            try {
                val response = homeRepository.getUserReservations(userId)
                Log.d("HomeViewModel", "Respuesta de getUserReservations: $response")
                if (response.success) {
                    val filteredReservations = response.data?.filter { it.status == "paid" } ?: emptyList()
                    Log.d("HomeViewModel", "Reservas filtradas: $filteredReservations")
                    _reservations.value = filteredReservations
                } else {
                    Log.e("HomeViewModel", "La respuesta no fue exitosa: ${response.message}")
                    _reservations.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al obtener reservas: ${e.message}", e)
                _reservations.value = emptyList()
            } finally {
                _isLoadingReservations.value = false
                Log.d("HomeViewModel", "Finalizando fetchUserReservations")
            }
        }
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("HomeViewModel", "Iniciando fetchHomeData")
            try {
                val response = homeRepository.fetchAllInfo()
                Log.d("HomeViewModel", "Respuesta de fetchAllInfo: $response")
                _homeData.value = response.data
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al obtener datos de inicio: ${e.message}", e)
            } finally {
                _isLoading.value = false
                Log.d("HomeViewModel", "Finalizando fetchHomeData")
            }
        }
    }

    // Función para obtener la agencia del usuario
    fun fetchUserAgency(userId: String) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "fetchUserAgency llamado con userId=$userId")
            val agency = homeRepository.getUserAgency(userId)
            if (agency != null) {
                Log.d("HomeViewModel", "Agencia obtenida: ${agency.name}")
                _userAgency.value = agency
            } else {
                Log.e("HomeViewModel", "No se pudo obtener la agencia.")
            }
        }
    }



    fun fetchUnitAvailability(unitId: String, pickupTime: String, reservationDate: String, hotelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("HomeViewModel", "Iniciando fetchUnitAvailability para unitId: $unitId, pickupTime: $pickupTime, reservationDate: $reservationDate, hotelId: $hotelId")
            try {
                val response = homeRepository.getUnitAvailability(unitId, pickupTime, reservationDate, hotelId)
                Log.d("HomeViewModel", "Respuesta de getUnitAvailability: $response")
                _availabilityData.value = response
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al obtener disponibilidad de unidad: ${e.message}", e)
                _availabilityData.value = null
            } finally {
                _isLoading.value = false
                Log.d("HomeViewModel", "Finalizando fetchUnitAvailability")
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
