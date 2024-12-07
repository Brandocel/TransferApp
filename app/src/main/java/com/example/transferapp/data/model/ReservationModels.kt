package com.example.transferapp.data.model
import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class MultipleReservationsRequest(
    @SerializedName("userId") val userId: String, // ID del usuario
    @SerializedName("zoneId") val zoneId: String, // ID de la zona
    @SerializedName("agencyId") val agencyId: String, // ID de la agencia
    @SerializedName("hotelId") val hotelId: String, // ID del hotel
    @SerializedName("unitId") val unitId: String, // ID de la unidad
    @SerializedName("seatNumber") val seatNumber: List<Int>, // Lista de números de asiento
    @SerializedName("pickupTime") val pickupTime: String, // Hora de recogida
    @SerializedName("reservationDate") val reservationDate: String, // Fecha de la reservación
    @SerializedName("clientName") val clientName: String, // Nombre del cliente
    @SerializedName("observations") val observations: String, // Observaciones
    @SerializedName("storeId") val storeId: String, // ID de la tienda
    @SerializedName("pax") val pax: Int, // Número total de personas
    @SerializedName("adults") val adults: Int, // Número de adultos
    @SerializedName("children") val children: Int, // Número de niños
    @SerializedName("status") val status: String, // Estado de la reservación
    @SerializedName("folio") val folio: String = "", // Folio generado o predeterminado
)

data class RegisterReservationRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("zoneId") val zoneId: String,
    @SerializedName("agencyId") val agencyId: String,
    @SerializedName("hotelId") val hotelId: String,
    @SerializedName("unitId") val unitId: String,
    @SerializedName("pickupTime") val pickupTime: String,
    @SerializedName("reservationDate") val reservationDate: String,
    @SerializedName("clientName") val clientName: String,
    @SerializedName("storeId") val storeId: String,
    @SerializedName("pax") val pax: Int,
    @SerializedName("adults") val adults: Int,
    @SerializedName("children") val children: Int,
    @SerializedName("status") val status: String
)

data class RegisterReservationResponse(
    val success: Boolean,
    val message: String,
    val data: String // El folio
)



data class ReservationRequest(
    val id: String, // Campo único
    val seatNumber: Int,
    val otherField: String
)

data class ReservationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: String // "F-103"
)


data class ReservationResponseItem(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("zoneId") val zoneId: String,
    @SerializedName("agencyId") val agencyId: String,
    @SerializedName("hotelId") val hotelId: String,
    @SerializedName("unitId") val unitId: String,
    @SerializedName("seatNumber") val seatNumber: List<Int>,
    @SerializedName("pickupTime") val pickupTime: String,
    @SerializedName("reservationDate") val reservationDate: String,
    @SerializedName("clientName") val clientName: String,
    @SerializedName("observations") val observations: String?,
    @SerializedName("storeId") val storeId: String,
    @SerializedName("pax") val pax: Int,
    @SerializedName("adults") val adults: Int,
    @SerializedName("children") val children: Int,
    @SerializedName("status") val status: String,
    @SerializedName("folio") val folio: String
)

data class PendingReservation(
    val id: String,
    val userId: String,
    val zoneId: String,
    val agencyId: String,
    val hotelId: String,
    val unitId: String,
    val storeId: String,
    val seatNumber: List<Int>,
    val pickupTime: String,
    val reservationDate: String,
    val clientName: String,
    val observations: String?,
    val pax: Int,
    val adults: Int,
    val children: Int,
    val status: String,
    val folio: String
)
