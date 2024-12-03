package com.example.transferapp.repository

import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.model.SeatStatusResponse

class SeatSelectionRepository(private val apiService: ApiService) {
    suspend fun getSeatStatus(
        unitId: String,
        pickupTime: String,
        reservationDate: String,
        hotelId: String
    ): SeatStatusResponse {
        return apiService.getSeatStatus(unitId, pickupTime, reservationDate, hotelId)
    }
}
