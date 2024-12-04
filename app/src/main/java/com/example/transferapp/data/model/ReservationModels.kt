package com.example.transferapp.data.model

data class MultipleReservationsRequest(
    val userId: String,
    val zoneId: String,
    val agencyId: String,
    val hotelId: String,
    val unitId: String,
    val seatNumber: List<Int>,
    val pickupTime: String,
    val reservationDate: String,
    val clientName: String,
    val observations: String,
    val storeId: String,
    val pax: Int,
    val adults: Int,
    val children: Int,
    val status: String , // Por defecto
    val folio: String = "" // Puede generarse en el backend
)

data class ReservationResponse(
    val success: Boolean,
    val message: String
)
