package com.example.transferapp.repository

import com.example.transferapp.data.api.ApiResponse
import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.model.AvailabilityResponse

import com.example.transferapp.ui.home.components.Reservation

class HomeRepository(private val apiService: ApiService) {
    suspend fun fetchAllInfo() = apiService.getAllInfo()

    suspend fun getUnitAvailability(unitId: String, pickupTime: String, reservationDate: String, hotelId: String): AvailabilityResponse {
        return apiService.getUnitAvailability(unitId, pickupTime, reservationDate, hotelId)
    }

    suspend fun getUserReservations(userId: String): ApiResponse<List<Reservation>> {
        return apiService.getUserReservations(userId)
    }
}
