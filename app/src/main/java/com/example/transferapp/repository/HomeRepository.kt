package com.example.transferapp.repository

import android.util.Log
import com.example.transferapp.data.api.AgencyInfo
import com.example.transferapp.data.api.ApiGenericResponse
import com.example.transferapp.data.api.ApiResponse
import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.api.UserInfo
import com.example.transferapp.data.model.*
import com.example.transferapp.ui.home.components.Reservation

class HomeRepository(private val apiService: ApiService) {

    // Obtener reservas pendientes
    suspend fun getPendingReservations(userId: String): ApiResponse<List<PendingReservation>> {
        return apiService.getPendingReservations(userId)
    }
    // Obtener toda la información necesaria para la pantalla de inicio
    suspend fun fetchAllInfo(): HomeResponse {
        return apiService.getAllInfo()
    }

    // Obtener la disponibilidad de una unidad específica
    suspend fun getUnitAvailability(
        unitId: String,
        pickupTime: String,
        reservationDate: String,
        hotelId: String
    ): AvailabilityResponse {
        return apiService.getUnitAvailability(unitId, pickupTime, reservationDate, hotelId)
    }

    // Obtener el estado de los asientos de una unidad específica
    suspend fun getSeatStatus(
        unitId: String,
        pickupTime: String,
        reservationDate: String,
        hotelId: String
    ): SeatStatusResponse {
        return apiService.getSeatStatus(unitId, pickupTime, reservationDate, hotelId)
    }

    // Agregar múltiples reservas
    suspend fun addMultipleReservations(request: MultipleReservationsRequest): ReservationResponse {
        return apiService.addMultipleReservations(request)
    }

    // Obtener las reservas de un usuario específico
    suspend fun getUserReservations(userId: String): ApiResponse<List<Reservation>> {
        return apiService.getUserReservations(userId)
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
