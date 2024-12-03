package com.example.transferapp.data.model

data class SeatStatusResponse(
    val success: Boolean,
    val message: String,
    val data: SeatStatusData
)

data class SeatStatusData(
    val paid: List<Int>, // Asientos ocupados
    val pending: List<Int> // Asientos pendientes
)
