package com.example.transferapp.data.model

import com.google.gson.annotations.SerializedName

data class SeatStatusResponse(
    @SerializedName("success") val success: Boolean, // Indica si la solicitud fue exitosa
    @SerializedName("message") val message: String, // Mensaje de la API
    @SerializedName("data") val data: SeatStatusData // Datos del estado de los asientos
)


data class SeatStatusData(
    @SerializedName("totalSeats") val totalSeats: Int, // Número total de asientos disponibles
    @SerializedName("paid") val paid: List<Int>, // Lista de números de asiento ocupados
    @SerializedName("pending") val pending: List<Int>, // Lista de números de asiento pendientes
    @SerializedName("unitId") val unitId: String, // ID de la unidad
    @SerializedName("pickupTime") val pickupTime: String, // Hora de recogida
    @SerializedName("ReservationDate") val reservationDate: String // Fecha de la reservación
)

