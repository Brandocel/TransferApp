package com.example.transferapp.repository

import android.util.Log
import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.model.SeatStatusResponse
import retrofit2.HttpException

class SeatSelectionRepository(private val apiService: ApiService) {

    // Obtener el estado de los asientos
    suspend fun getSeatStatus(
        unitId: String,
        pickupTime: String,
        reservationDate: String,
        hotelId: String
    ): SeatStatusResponse {
        Log.d(
            "Repository",
            "Fetching seat status for unitId=$unitId, pickupTime=$pickupTime, reservationDate=$reservationDate, hotelId=$hotelId"
        )
        return apiService.getSeatStatus(unitId, pickupTime, reservationDate, hotelId)
    }

    suspend fun getAgencyNameById(agencyId: String): String {
        Log.d("Repository", "Fetching agency name for ID: $agencyId")
        return try {
            val response = apiService.getAgencyInfoById(agencyId)
            val agencyName = response.data?.name // Asegúrate de acceder al campo `data`
            Log.d("Repository", "Agency name retrieved: $agencyName")
            agencyName ?: "Nombre no encontrado"
        } catch (e: Exception) {
            Log.e("Repository", "Error al obtener el nombre de la agencia", e)
            "Error al obtener nombre"
        }
    }

    suspend fun getUserNameById(userId: String): String {
        Log.d("Repository", "Fetching user name for ID: $userId")
        return try {
            val response = apiService.getUserInfoById(userId)
            val userName = response.data?.name // Asegúrate de acceder al campo `data`
            Log.d("Repository", "User name retrieved: $userName")
            userName ?: "Nombre no encontrado"
        } catch (e: Exception) {
            Log.e("Repository", "Error al obtener el nombre del usuario", e)
            "Error al obtener nombre"
        }
    }
}

