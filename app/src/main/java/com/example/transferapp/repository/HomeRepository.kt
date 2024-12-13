package com.example.transferapp.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.transferapp.data.api.AgencyInfo
import com.example.transferapp.data.api.ApiGenericResponse
import com.example.transferapp.data.api.ApiResponse
import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.api.UserInfo
import com.example.transferapp.data.local.SessionManager
import com.example.transferapp.data.model.*
import com.example.transferapp.ui.home.components.Reservation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeRepository(private val apiService: ApiService, private val sessionManager: SessionManager) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Obtener reservas pendientes
    suspend fun getPendingReservations(userId: String): ApiResponse<List<PendingReservation>> {
        return apiService.getPendingReservations(userId)
    }
    // Obtener toda la información necesaria para la pantalla de inicio
    suspend fun fetchAllInfo(): HomeResponse {
        return apiService.getAllInfo()
    }

    //generar cupon por primera vez
    suspend fun registerReservation(request: RegisterReservationRequest): ApiResponse<String> {
        return apiService.registerReservation(request)
    }



    // Obtener la disponibilidad de una unidad específica
    suspend fun getUnitAvailability(
        unitId: String,
        reservationDate: String,
        zoneId: String
    ): AvailabilityResponse {
        return apiService.getUnitAvailability(unitId, reservationDate, zoneId)
    }

    // Obtener el estado de los asientos de una unidad específica
    suspend fun getSeatStatus(
        unitId: String,
        reservationDate: String,
        zoneId: String
    ): SeatStatusResponse {
        return apiService.getSeatStatus(unitId, reservationDate, zoneId)
    }

    // Agregar múltiples reservas
//    suspend fun addMultipleReservations(request: MultipleReservationsRequest): ReservationResponse {
//        return apiService.addMultipleReservations(request)
//    }

    // Obtener las reservas de un usuario específico
    suspend fun getUserReservations(userId: String): ApiResponse<List<Reservation>> {
        return apiService.getUserReservations(userId)
    }

    //Logout
    fun clearUserSession() {
        repositoryScope.launch {
            sessionManager.clearAuthToken()
            Log.d("SessionManager", "Token de sesión eliminado.")
        }
    }

    // Obtener la agencia vinculada a un usuario
    suspend fun getUserAgency(userId: String): Agency? {
        return try {
            Log.d("HomeRepository", "Intentando obtener UserInfo con userId=$userId")
            val userInfoResponse: ApiGenericResponse<UserInfo> = apiService.getUserInfoById(userId)
            if (userInfoResponse.success) {
                val userInfo: UserInfo = userInfoResponse.data
                Log.d("HomeRepository", "UserInfo obtenido: ${userInfo.name}, agencyId=${userInfo.agencyId}")

                val agencyId = userInfo.agencyId
                Log.d("HomeRepository", "Obteniendo AgencyInfo con agencyId=$agencyId")
                val agencyInfoResponse: ApiGenericResponse<AgencyInfo> = apiService.getAgencyInfoById(agencyId)
                if (agencyInfoResponse.success) {
                    val agencyInfo: AgencyInfo = agencyInfoResponse.data
                    Log.d("HomeRepository", "AgencyInfo obtenido: ${agencyInfo.name}")
                    Agency(
                        id = agencyInfo.id,
                        name = agencyInfo.name
                    )
                } else {
                    Log.e("HomeRepository", "Error al obtener AgencyInfo: ${agencyInfoResponse.message}")
                    null
                }
            } else {
                Log.e("HomeRepository", "Error al obtener UserInfo: ${userInfoResponse.message}")
                null
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Excepción en getUserAgency: ${e.message}", e)
            null
        }
    }


}
