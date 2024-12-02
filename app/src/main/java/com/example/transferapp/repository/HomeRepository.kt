package com.example.transferapp.repository

import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.model.AvailabilityResponse

class HomeRepository(private val apiService: ApiService) {
    suspend fun fetchAllInfo() = apiService.getAllInfo()

    suspend fun getUnitAvailability(unitId: String, pickupTime: String, reservationDate: String, hotelId: String): AvailabilityResponse {
        return apiService.getUnitAvailability(unitId, pickupTime, reservationDate, hotelId)
    }


}
